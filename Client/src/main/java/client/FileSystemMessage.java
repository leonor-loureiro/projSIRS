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

    FileSystemMessage(){
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
}
