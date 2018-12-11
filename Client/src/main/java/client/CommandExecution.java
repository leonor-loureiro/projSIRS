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

import java.io.IOException;
import java.security.*;
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

    private void setUser(User user) {this.user = user; }

    public void login(Login login) throws BadArgument, InvalidUser {
        setUser(new User(login.getUsername(), login.getPassword()));
        communication.login(user);
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
                    "./" + login.getUsername() + "keys" + ".jceks",
                    user.getUsername(),
                    pwdArray);

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
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

        FileWrapper file = getFileWrapper(filename);

        if(file == null){
            System.out.println("File " + filename + " not found.");
            return;
        }

        file.setFileCreator(user.getUsername());
        user.addFileToStaged(file);

    }

    private FileWrapper getFileWrapper(String filename) {
        FileWrapper file = null;
        try {
            file = FileManager.loadFile(filename);
        } catch (IOException e) {
            System.out.println("Unable to read file. Wrong filename or no permission.");
            e.printStackTrace();
        }
        return file;
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

        List<FileWrapper> files = communication.getFiles(user);

        user.removeFilesFromStaged(files);
        try {
            FileManager.saveFiles(files);
        } catch (IOException e) {
            System.out.println("Unable to save pulled files");
            e.printStackTrace();
        }

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

        FileWrapper files = communication.getBackup(user, fileName);

    }

    /**
     * Exits program
     */
    public void exit(){
        System.out.println("Shutting down...");

    }


}
