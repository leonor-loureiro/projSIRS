package client;

import client.security.EncryptedFileWrapper;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used for communication with File System server
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class,property="@id", scope = FileSystemMessage.class)
public class FileSystemMessage {

    private EncryptedFileWrapper[] files;

    private String token;

    private String userName;

    private String userToShareWith;

    private String fileName;


    private Boolean corrupted = false;

    public FileSystemMessage(){
    }

    public EncryptedFileWrapper[] getFiles() {
        return files;
    }

    public void setFiles(EncryptedFileWrapper[] files) {
        this.files = files;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return userName;
    }

    public void setUserName(String user){
        userName = user;
    }

    public void setUserToShareWith(String user){userToShareWith = user;}

    public String getUserToShareWith(){return userToShareWith;}

    public void setFileName(String filename) {
        fileName = filename;
    }

    public String getBackUpFileName() {
        return fileName;
    }


    public void setCorrupted(){
        corrupted = true;
    }

}
