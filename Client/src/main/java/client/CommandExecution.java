package client;

import client.localFileHandler.FileManager;
import client.localFileHandler.FileWrapper;
import client.security.Login;
import crypto.Crypto;
import crypto.exception.CryptoException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.Entry;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class CommandExecution {

    /**
     * User under which the commands will be executed
     */
    private User user;


    /**
     * Communicates with the server
     */
    private Communication communication = new Communication();

    private void setUser(User user) {this.user = user; }

    public void login(Login login) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, CryptoException, OperatorCreationException {
        setUser(new User(login.getUsername(), login.getPassword()));
        communication.login(user);

        KeyStore ks = KeyStore.getInstance("jks");

        char[] pwdArray = login.getPassword();

        ks.load(null,pwdArray);

        Certificate selfSignedCertificate = selfSign(Crypto.generateRSAKeys(), "CN=esketit");
        KeyStore.PrivateKeyEntry secret = new KeyStore.PrivateKeyEntry(Crypto.generateRSAKeys().getPrivate(),new Certificate[] { selfSignedCertificate });
        KeyStore.ProtectionParameter password
                = new KeyStore.PasswordProtection(pwdArray);
        ks.setEntry(login.getUsername(), secret, password);

        try (FileOutputStream fos = new FileOutputStream("./" + login.getUsername() + "keys" + ".jks")) {
            ks.store(fos, pwdArray);
        }


    }

    public  Certificate selfSign(KeyPair keyPair, String subjectDN)
            throws OperatorCreationException, CertificateException, IOException
    {
        Provider bcProvider = new BouncyCastleProvider();
        Security.addProvider(bcProvider);

        long now = System.currentTimeMillis();
        Date startDate = new Date(now);

        X500Name dnName = new X500Name(subjectDN);

        // Using the current timestamp as the certificate serial number
        BigInteger certSerialNumber = new BigInteger(Long.toString(now));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        // 1 Yr validity
        calendar.add(Calendar.YEAR, 1);

        Date endDate = calendar.getTime();

        // Use appropriate signature algorithm based on your keyPair algorithm.
        String signatureAlgorithm = "SHA256WithRSA";

        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair
                .getPublic().getEncoded());

        X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(dnName,
                certSerialNumber, startDate, endDate, dnName, subjectPublicKeyInfo);

        ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).setProvider(
                bcProvider).build(keyPair.getPrivate());

        X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);

        Certificate selfSignedCert = new JcaX509CertificateConverter()
                .getCertificate(certificateHolder);

        return selfSignedCert;
    }

    public void register(Login login){
        // Generate key pair
        KeyPair keyPair = null;
        try {
            keyPair = Crypto.generateRSAKeys();

        } catch (CryptoException e) {
            e.printStackTrace();
        }

        //Create the user
        User user = new User(login.getUsername(), login.getPassword());
        user.setPrivateKey(keyPair.getPrivate());
        user.setPublicKey(keyPair.getPublic());

        setUser(user);
        communication.register(user);
    }

    /**
     * adds a file to staging file Lists, allowing it to be pushed
     * @param filename name of the file to be staged
     */
    public void add(String filename){

        if(user == null){
            System.out.println("User must be logged for this operation!!");
            return;
        }

        System.out.println("Adding File ..." + filename);

        FileWrapper file = FileManager.loadFile(filename);

        if(file == null){
            System.out.println("File " + filename + " not found.");
            return;
        }

        file.setFileCreator(user.getUsername());
        user.addFileToStaged(file);

        file.setFileName("testtttttttttttt");
        try {
            FileManager.saveFile(file);
        } catch (IOException e) {
            System.out.println("Unable to Store file");
            e.printStackTrace();
        }

    }

    /**
     * get all remote files and adds them to staging
     */
    public void pull(){
        System.out.println("Pulling files from remote Files ...");
        if(user == null){
            System.out.println("User must be logged for this operation!!");
            return;
        }

        List<FileWrapper> files = communication.getFiles(user);

        user.addFilesToStaged(files);

    }

    /**
     * sends all staged file to remote
     */
    public void push(){
        System.out.println("Pushing Files to remote ...");
        if(user == null){
            System.out.println("User must be logged for this operation!!");
            return;
        }

        communication.putFiles(user, user.getstagedFiles());
    }

    /**
     * Shares a user's file with a given user
     */
    public void share(String dest, String fileName){
        System.out.println("Sharing file with user ...");
        System.out.println("It doesn't work yet! :P");
        if(user == null){
            System.out.println("User must be logged for this operation!!");
            return;
        }
    }

    /**
     * lists all local and staging files
     */
    public void list(){

        if(user == null){
            System.out.println("User must be logged for this operation!!");
            return;
        }

        System.out.println("Listing Files...");
        System.out.println("Local files:");
        for(String name : FileManager.listFileNames()){
            System.out.println(name);
        }
        System.out.println();

        System.out.println("Staged files:");
        for(String name : user.getStagedFilesNames()){
            System.out.println(name);
        }
    }

    /**
     * Exits program
     */
    public void exit(){
        System.out.println("Shutting down...");
        System.exit(0);

    }

    public void getBackup(String fileName) {

        FileWrapper files = communication.getBackup(user, fileName);

    }
}
