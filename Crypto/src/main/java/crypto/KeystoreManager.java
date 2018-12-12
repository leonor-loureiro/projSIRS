package crypto;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.Date;

public class KeystoreManager {

    public static final String KEYSTORE_TYPE = "JCEKS";
    /**
     * Generates a self signed certificate
     * @param keyPair private and public key
     * @param subjectDN certified subject name
     * @return self signed certificate
     * @throws OperatorCreationException
     * @throws CertificateException
     */
    public static Certificate selfSign(KeyPair keyPair, String subjectDN)
            throws OperatorCreationException, CertificateException {
        Provider bcProvider = new BouncyCastleProvider();
        Security.addProvider(bcProvider);

        long now = System.currentTimeMillis();
        Date startDate = new Date(now);

        String cn = "CN="+subjectDN;
        X500Name dnName = new X500Name(cn);

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

    public static Certificate CreateAndStoreCertificate(KeyPair keyPair, String keystoreFileName, String alias, char[] passwordArray)
            throws CertificateException, OperatorCreationException, IOException, KeyStoreException, NoSuchAlgorithmException {

        //Create a keystore
        KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
        ks.load(null,passwordArray);

        //Create self signed exception
        Certificate selfSignedCertificate = selfSign(keyPair, alias);

        //Create private key entry
        KeyStore.PrivateKeyEntry privateKeyEntry = new KeyStore.PrivateKeyEntry(keyPair.getPrivate(),new Certificate[] { selfSignedCertificate });

        //Create a protection parameter used to protect the contents of the keystore
        KeyStore.ProtectionParameter password = new KeyStore.PasswordProtection(passwordArray);
        ks.setEntry(alias + "-privateKey", privateKeyEntry, password);

        //Create certificate entry
        ks.setCertificateEntry(alias + "-certificate", selfSignedCertificate);


        //Stores the entry in the keystore
        try (FileOutputStream fos = new FileOutputStream( keystoreFileName)){
            ks.store(fos, passwordArray);
        }

        return selfSignedCertificate;
    }

    public static void  StoreSecretKey(SecretKey key, String keystoreFileName, String alias, char[] passwordArray)
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {

        //Load the keystore
        KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
        ks.load(new FileInputStream(keystoreFileName), passwordArray);

        //Create a protection parameter used to protect the contents of the keystore
        KeyStore.ProtectionParameter passwordParam = new KeyStore.PasswordProtection(passwordArray);

        //Create secret key entry
        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(key);

        //Set keystore entry
        ks.setEntry(alias + "-secretKey", secretKeyEntry, passwordParam);

        //Stores the entry in the keystore
        try (FileOutputStream fos = new FileOutputStream( keystoreFileName)){
            ks.store(fos, passwordArray);
        }
    }

    public static SecretKey getSecretKey(String keystoreFileName, String alias, char[] passwordArray)
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {

        //Load the keystore
        KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
        ks.load(new FileInputStream(keystoreFileName), passwordArray);


        return (SecretKey) ks.getKey(alias + "-secretKey", passwordArray);
    }

    public static PublicKey getPublicKey(String keystoreFileName, String alias, char[] passwordArray)
            throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {

        //Load the keystore
        KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
        ks.load(new FileInputStream(keystoreFileName), passwordArray);

        Certificate cer = ks.getCertificate(alias);
        return cer.getPublicKey();
    }

    public static Key getPrivateKey(String keystoreFileName, String alias, char[] passwordArray)
            throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {

        //Load the keystore
        KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
        ks.load(new FileInputStream(keystoreFileName), passwordArray);

        return ks.getKey(alias, passwordArray);
    }




}
