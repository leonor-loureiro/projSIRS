package filesystem.data;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * Class used for communication with File System server
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class,property="@id", scope = FileSystemMessage.class)

public class FileSystemMessage {

    private EncryptedFileWrapper[] files;

    private String token;

    private String userName;

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

    public void setName(String user){
        userName = user;
    }
}
