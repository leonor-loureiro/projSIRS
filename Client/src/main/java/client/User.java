package client;

import client.localFileHandler.FileWrapper;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the User
 * Holds every information needed to identify the user and execute his commands
 */
public class User {

    private String username;
    private char[] password;
    private Key privateKey;
    private Key publicKey;
    private List<FileWrapper> stagedFiles = new ArrayList<>();

    public User() {
    }

    public User(String username, char[] password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, char[] password, Key privateKey, Key publicKey) {
        this.username = username;
        this.password = password;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public Key getPrivateKey() { return privateKey; }

    public void setPrivateKey(Key privateKey) { this.privateKey = privateKey; }

    public char[] getPassword() { return password; }

    public void setPassword(char[] password) { this.password = password; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public Key getPublicKey() { return publicKey; }

    public void setPublicKey(Key publicKey) { this.publicKey = publicKey; }

    public List<FileWrapper> getstagedFiles() {
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
    
    public List<String> getStagedFilesNames(){
        List<String> result = new ArrayList<>();
        for(FileWrapper fw : stagedFiles){
            result.add(fw.getFileName());
        }

        return result;
    }
}
