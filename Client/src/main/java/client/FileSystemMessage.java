package client;

import client.security.EncryptedFileWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used for communication with File System server
 */
public class FileSystemMessage {

    private List<EncryptedFileWrapper> files;

    private String token;

    FileSystemMessage(){
        files = new ArrayList<>();
    }

    public List<EncryptedFileWrapper> getFiles() {
        return files;
    }

    public void setFiles(List<EncryptedFileWrapper> files) {
        this.files = files;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
