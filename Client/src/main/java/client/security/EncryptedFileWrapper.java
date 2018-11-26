package client.security;

import client.localFileHandler.FileWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EncryptedFileWrapper {

    private String fileName;

    private byte[] file;

    private byte[] fileKey;

    private String fileCreator;

    private String fileMAC;

    public EncryptedFileWrapper(){}

    public EncryptedFileWrapper(FileWrapper fp){
        fileName = fp.getFileName();

        File inputFile = fp.getFile();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(inputFile);
            file = new byte[(int) inputFile.length()];
            // TODO:Encrypt File
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO: Encrypt fileKey
        fileKey = null;

        fileCreator = fp.getFileCreator();

        fileMAC = "MAC"; // TODO: Generate MAC
    }

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
