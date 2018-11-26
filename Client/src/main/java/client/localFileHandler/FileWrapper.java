package client.localFileHandler;

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
    private File file;

    /**
     * Key used to encrypt this file
     */
    private Key fileKey;

    /**
     * Creator of the file
     */
    private String fileCreator;

    /**
     * MAC to ensure File's integrity
     */
    private byte[] fileMAC;


    // TODO: add version

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
    public FileWrapper(String fileName, File file, Key fileKey, String fileCreator) {
        this.fileName = fileName;
        this.file = file;
        this.fileKey = fileKey;
        this.fileCreator = fileCreator;
    }

    /**
     * FileWrapper constructor Overload
     * @param fileName file name and extension of the wrapped file
     * @param file file to be wrapped
     * @param fileKey key used to encrypt this file when not stored locally
     * @param fileCreator original creator of this file
     * @param fileMAC Message Authentication Code to keep integrity
     */
    public FileWrapper(String fileName, File file, Key fileKey, String fileCreator, byte[] fileMAC) {
        this.fileName = fileName;
        this.file = file;
        this.fileKey = fileKey;
        this.fileCreator = fileCreator;
        this.fileMAC = fileMAC;
    }

    /**
     * Get the string content of a file
     * @param file the FileWrapper which content u want the string from
     * @return the string content of the file
     * @throws IOException if any error occur with file opening
     */
    static String getFileContentString(FileWrapper file) throws IOException {

        try(BufferedReader br = new BufferedReader(new FileReader(file.getFile()))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        }
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

    public File getFile() { return file; }

    public void setFile(File file) { this.file = file; }

    public Key getFileKey() { return fileKey; }

    public void setFileKey(Key fileKey) { this.fileKey = fileKey; }

    public String getFileCreator() { return fileCreator; }

    public void setFileCreator(String fileCreator) { this.fileCreator = fileCreator; }

    public byte[] getFileMAC() { return fileMAC; }

    public void setFileMAC(byte[] fileMAC) { this.fileMAC = fileMAC; }
}
