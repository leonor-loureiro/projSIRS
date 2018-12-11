package client;

import client.exception.BadArgument;
import client.exception.InvalidUser;
import client.exception.TokenInvalid;
import client.exception.UserAlreadyExists;
import client.localFileHandler.FileManager;
import client.localFileHandler.FileWrapper;
import client.security.EncryptedFileWrapper;
import client.security.Login;
import client.security.SecurityHandler;
import crypto.KeystoreManager;
import crypto.Crypto;
import crypto.exception.CryptoException;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandExecution {

    /**
     * User under which the commands will be executed
     */
    private User user;


    /**
     * Communicates with the server
     */
    private Communication communication = new Communication();

    public void setUser(User user) {this.user = user; }

    public void login(Login login) throws BadArgument, InvalidUser {
        setUser(new User(login.getUsername(), login.getPassword()));

        // Tries to login at auth server
        if(!communication.login(user))
            return;

        // Extract public key
        PublicKey publicKey = null;
        PrivateKey privateKey = null;
        try {
            publicKey = KeystoreManager.getPublicKey(getKeystoreFileName(user.getUsername()),
                    user.getUsername() + "-certificate", user.getPassword());
            privateKey = (PrivateKey) KeystoreManager.getPrivateKey(getKeystoreFileName(user.getUsername()),
                    user.getUsername() + "-privateKey", user.getPassword());
        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        user.setPublicKey(publicKey);
        user.setPrivateKey(privateKey);
    }


    /**
     * Register the given user to the service and generates his needed private information
     * @param login
     */
    public boolean register(Login login) throws BadArgument, UserAlreadyExists {
        //Create the user
        User user = new User(login.getUsername(), login.getPassword());

        // Generate key pair
        KeyPair keyPair = null;
        try {
            keyPair = Crypto.generateRSAKeys();
            user.setPrivateKey(keyPair.getPrivate());
            user.setPublicKey(keyPair.getPublic());

        } catch (CryptoException e) {
            e.printStackTrace();
            return false;
        }

        // Send register request
        setUser(user);
        if(!communication.register(user)){
            return false;
        }

        // Create keystore and store key pair
        char[] pwdArray = login.getPassword();

        try {
            KeystoreManager.CreateAndStoreCertificate(keyPair,
                    getKeystoreFileName(login.getUsername()),
                    user.getUsername(),
                    pwdArray);

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private String getKeystoreFileName(String username) {
        return "./" + username + "keys" + ".jceks";
    }

    /**
     * adds a file to staging file Lists, allowing it to be pushed
     * @param filename name of the file to be staged
     */
    public void add(String filename) throws BadArgument {

        if(user == null){
            System.out.println("User must be logged for this operation!!");
            return;
        }

        System.out.println("Adding File ..." + filename);

        FileWrapper file = getFileWrapper(filename);

        if(file == null){
            System.out.println("File " + filename + " not found.");
            return;
        }

        file.setFileCreator(user.getUsername());
        user.addFileToStaged(file);

    }

    /**
     * Gets file and generates a file wrapper by filename
     * @param filename file's name
     * @return file wrapper
     */
    public FileWrapper getFileWrapper(String filename) throws BadArgument {
        FileWrapper file = null;
        try {
            // Reading File
            file = FileManager.loadFile(filename, user.getUsername());

            if(file == null)
                throw new BadArgument("No such file: " + filename);

            // Get file's key (if it exists)
            if (storeFileKey(filename, file)) return null;

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to read file. Wrong filename or no permission.");
        }
        return file;
    }

    /**
     * Verifies if file Key is in KeyStore, if not, adds it
     * @param filename the file's name
     * @param file the file's wrapper
     * @return true if successfully stored
     */
    private boolean storeFileKey(String filename, FileWrapper file){
        try{
            SecretKey key  = KeystoreManager.getSecretKey(getKeystoreFileName(user.getUsername()), filename, user.getPassword());

            // If key doesn't exist, generate it
            if(key == null){
                System.out.println("Generating File Key...");

                if(file.getFileKey() == null)
                    key = Crypto.generateSecretKey();

                else
                    key = Crypto.extractSecretKey(file.getFileKey());

                KeystoreManager.StoreSecretKey(key, getKeystoreFileName(user.getUsername()), filename, user.getPassword());
            }

            if(file == null || key == null)
                return true;

            file.setFileKey(key.getEncoded());

        } catch (CryptoException | CertificateException | UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Stores files and their keys
     * @param fw file wrapper to be stored
     */
    public void saveFileWrappers(List<FileWrapper> fw)  {

        for(FileWrapper f : fw){
            storeFileKey(f.getFileName(), f);
            try {
                FileManager.saveFile(f);
            } catch (IOException e) {
                System.out.println("Unable to save file: " + f.getFileName());
            }
        }

    }

    /**
     * get all remote files and removes them from staging
     */
    public void pull(){
        System.out.println("Pulling files from remote Files ...");
        if(user == null){
            System.out.println("User must be logged for this operation!!");
            return;
        }

        List<FileWrapper> files = SecurityHandler.decryptFileWrappers(Arrays.asList(communication.getFiles(user)), user.getPrivateKey());

        user.removeFilesFromStaged(files);

        saveFileWrappers(files);

    }

    /**
     * sends all staged files to remote
     */
    public void push(){
        System.out.println("Pushing Files to remote ...");
        if(user == null){
            System.out.println("User must be logged for this operation!!");
            return;
        }

        if(user.getStagedFiles().size() == 0){
            System.out.println("No files staged");
            return;
        }

        communication.putFiles(user, user.getStagedFiles());
        user.removeFilesFromStaged(user.getStagedFiles());
    }

    /**
     * Shares a user's file with a given user
     */
    public void share(String dest, String fileName) throws TokenInvalid, BadArgument {

        //Get user's public key
        PublicKey publicKey = null;
        try {
            publicKey = communication.getUserKey(user.getUsername(), dest);
        } catch (CryptoException e) {
            e.printStackTrace();
        }

        //Get file key and encrypt it
        FileWrapper fileWrapper = getFileWrapper(fileName);

        //TODO: Remove this set when wrapper already has key
        try {
            fileWrapper.setFileName(fileName);
            fileWrapper.setFileCreator(dest);
            fileWrapper.setFileKey(Crypto.generateSecretKey().getEncoded());
        } catch (CryptoException e) {
            e.printStackTrace();
        }

        EncryptedFileWrapper encFile = SecurityHandler.encryptFileWrapper(fileWrapper, publicKey);

        System.out.println("File shared with success");

        //Share file
        communication.shareFile(user, encFile, dest );
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
     * Requests an older version of a given file
     * @param fileName
     */
    public void getBackup(String fileName) {


        System.out.println(user.getUsername());
        System.out.println(fileName);
        FileWrapper file = communication.getOldVersion(user, fileName);


        ArrayList<FileWrapper> files = new ArrayList<FileWrapper>();
        files.add(file);
        try {
            FileManager.saveFiles(files);
        } catch (IOException e) {
            System.out.println("Unable to save pulled files");
            e.printStackTrace();
        }

    }

    public void getBackupCorrupted(String fileName){

    }

    /**
     * Exits program
     */
    public void exit(){
        System.out.println("Shutting down...");

    }


}
