package client;

import client.localFileHandler.FileWrapper;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the User
 * Holds every information needed to identify the user and execute his commands
 */
public class User {

    private String username;
    private char[] password;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private List<FileWrapper> stagedFiles = new ArrayList<>();

    public User() {
    }

    public User(String username, char[] password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, char[] password, PrivateKey privateKey, PublicKey publicKey) {
        this.username = username;
        this.password = password;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() { return privateKey; }

    public void setPrivateKey(PrivateKey privateKey) { this.privateKey = privateKey; }

    public char[] getPassword() { return password; }

    public void setPassword(char[] password) { this.password = password; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public PublicKey getPublicKey() { return publicKey; }

    public void setPublicKey(PublicKey publicKey) { this.publicKey = publicKey; }

    public List<FileWrapper> getStagedFiles() {
        return stagedFiles;
    }

    /**
     * Adds File to user's staging files
     * @param fw file wrapper with file to be staged
     */
    public void addFileToStaged(FileWrapper fw){
        stagedFiles.remove(fw);
        this.stagedFiles.add(fw);
    }

    /**
     * Adds a list of files to staging
     * @param fw the file to be added
     */
    public void addFilesToStaged(List<FileWrapper> fw){
        for(FileWrapper file : fw)
            this.addFileToStaged(file);
    }

    public void removeFileFromStaged(FileWrapper fw){
        stagedFiles.remove(fw);
    }

    public void removeFilesFromStaged(List<FileWrapper> fw){
        stagedFiles.removeAll(fw);
    }

    public void clearStagedFiles(){
        stagedFiles = new ArrayList<>();
    }


    public List<String> getStagedFilesNames(){
        List<String> result = new ArrayList<>();
        for(FileWrapper fw : stagedFiles){
            result.add(fw.getFileName());
        }

        return result;
    }
}
