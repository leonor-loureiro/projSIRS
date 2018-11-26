package filesystem;

import java.io.Serializable;

public class EncryptedFileWrapper implements Serializable {

    private String fileName;

    private byte[] file;

    private byte[] fileKey;

    private String fileCreator;

    private String fileMAC;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public byte[] getFileKey() {
        return fileKey;
    }

    public void setFileKey(byte[] fileKey) {
        this.fileKey = fileKey;
    }

    public String getFileCreator() {
        return fileCreator;
    }

    public void setFileCreator(String fileCreator) {
        this.fileCreator = fileCreator;
    }

    public String getFileMAC() {
        return fileMAC;
    }

    public void setFileMAC(String fileMAC) {
        this.fileMAC = fileMAC;
    }
}
