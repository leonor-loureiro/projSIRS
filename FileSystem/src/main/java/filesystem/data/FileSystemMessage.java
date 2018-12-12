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

    private String userToShareWith;

    private String backUpFileName;

    private Boolean corrupted = false;

    public FileSystemMessage(){
    }

    public String getBackUpFileName() {
        return backUpFileName;
    }

    public void setBackUpFileName(String backUpFileName) {
        this.backUpFileName = backUpFileName;
    }

    public void setCorrupted(Boolean corrupted) {
        this.corrupted = corrupted;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String user){
        userName = user;
    }

    public void setUserToShareWith(String user){userToShareWith = user;}

    public String getUserToShareWith(){return userToShareWith;}


    public void setCorrupted(){
        corrupted = true;
    }

    public Boolean getCorrupted() {
        return corrupted;
    }


}
