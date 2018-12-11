package client.localFileHandler;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;

public class FileWrapper {
    /**
     * File name with extension included
     */
    private String fileName;

    /**
     * The file being wrapped around
     */
    private byte[] file;

    /**
     * Key used to encrypt this file
     */
    private byte[] fileKey;

    /**
     * Creator of the file
     */
    private String fileCreator;

    /**
     * Empty Default Constructor
     */
    public FileWrapper(){

    }

    /**
     * FileWrapper constructor Overload
     * @param fileName file name and extension of the wrapped file
     * @param file file to be wrapped
     * @param fileKey key used to encrypt this file when not stored locally
     * @param fileCreator original creator of this file
     */
    public FileWrapper(String fileName, byte[] file, byte[] fileKey, String fileCreator) {
        this.fileName = fileName;
        this.file = file;
        this.fileKey = fileKey;
        this.fileCreator = fileCreator;
    }


    /**
     * Compares files by their name
     * Each file's name should be unique
     * @param obj the target to compare to
     * @return true if both have same name
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!FileWrapper.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final FileWrapper other = (FileWrapper) obj;
        return this.fileName.equals(other.fileName);
    }


    public String getFileName() { return fileName; }

    public void setFileName(String fileName) { this.fileName = fileName; }

    public byte[] getFile() { return file; }

    public void setFile(byte[] file) { this.file = file; }

    public byte[] getFileKey() { return fileKey; }

    public void setFileKey(byte[] fileKey) { this.fileKey = fileKey; }

    public String getFileCreator() { return fileCreator; }

    public void setFileCreator(String fileCreator) { this.fileCreator = fileCreator; }


}
