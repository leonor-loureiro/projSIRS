package client.localFileHandler;

import java.io.File;
import java.security.Key;

public class FileWrapper {
    private String fileName;
    private File file;
    private Key fileKey;
    private String fileCreator;
    private String fileMAC;


    public FileWrapper(){

    }

    public FileWrapper(String fileName, File file, Key fileKey, String fileCreator, String fileMAC) {
        this.fileName = fileName;
        this.file = file;
        this.fileKey = fileKey;
        this.fileCreator = fileCreator;
        this.fileMAC = fileMAC;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Key getFileKey() {
        return fileKey;
    }

    public void setFileKey(Key fileKey) {
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
