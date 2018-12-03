package client.security;

import client.localFileHandler.FileWrapper;
import client.security.exception.FileCorrupted;
import crypto.Crypto;
import crypto.exception.CryptoException;

import java.security.Key;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public interface SecurityHandler {

    String keystoreFile = "./" + "\\src\\main\\resources\\trustkeystore.jks";
    String keystorePwd = "password";
    String keyPwd = "password";
    String myAlias = "server-keypair";

    /**
     * Decrypts a an encrypted file wrapper
     * @param enc
     * @return decrypted file wrapper
     * @throws FileCorrupted
     */
    static FileWrapper decryptFileWrapper(EncryptedFileWrapper enc) throws FileCorrupted {
        FileWrapper file = new FileWrapper();

        // Set file
        file.setFileName(enc.getFileName());
        // Set file creator
        file.setFileCreator(enc.getFileCreator());

        // Decipher file key
        try {
            Key privateKey = Crypto.getPrivateKey(keystoreFile, keystorePwd, myAlias, keyPwd);

            byte[] cipheredFileKey = enc.getFileKey();
            byte[] fileKey = Crypto.decryptRSA(cipheredFileKey, privateKey);

            file.setFileKey(fileKey);

        } catch (CryptoException e) {
            throw new FileCorrupted();
        }

        // Decipher file
        try {
            byte[] cipheredData = enc.getFile();
            byte[] data = Crypto.decryptAES(file.getFileKey(), cipheredData);
            file.setFile(data);

        } catch (CryptoException e) {
            throw new FileCorrupted();
        }


        // Validate mac
        try {
            String mac = enc.getFileMAC();
            byte[] data = extractFileData(file);
            boolean isValid = Crypto.validateMAC(file.getFileKey(), data, mac);

            if(!isValid){
                throw new FileCorrupted();
            }

        } catch (CryptoException e) {
            e.printStackTrace();
        }


        return file;
    }

    /**
     * Decrypts a list of files
     * @param encriptedFiles
     * @return decrypted files
     */
    static List<FileWrapper> decryptFileWrappers(List<EncryptedFileWrapper> encriptedFiles){

        List<FileWrapper> list = new ArrayList<>();

        for(EncryptedFileWrapper enc : encriptedFiles){
            try {
                list.add(decryptFileWrapper(enc));
            } catch (FileCorrupted fileCorrupted) {
                System.out.println("File " + enc.getFileName() + " is corrupted.");
            }
        }
        return list;
    }

    /**
     * Encrypts a file wrapper
     * @param file
     * @return encrypted file wrapper
     */
    static EncryptedFileWrapper encryptFileWrapper(FileWrapper file){
        EncryptedFileWrapper enc = new EncryptedFileWrapper();

        // Set file name

        enc.setFileName(file.getFileName());
        // Set file creator
        enc.setFileCreator(file.getFileCreator());

        // Encrypt the file
        byte[] fileKey = file.getFileKey();
        byte[] fileData = file.getFile();

        try {
            byte[] cipher = Crypto.encryptAES(fileKey, fileData);
            enc.setFile(cipher);

        } catch (CryptoException e) {
            e.printStackTrace();
        }

        byte[] data = extractFileData(file);

        // Compute MAC
        try {
            String mac = Crypto.computeMAC(fileKey, data);
            enc.setFileMAC(mac);

        } catch (CryptoException e) {
            e.printStackTrace();
        }

        // Encrypt file key
        try {
            PublicKey publicKey = Crypto.getPublicKey(keystoreFile, keystorePwd, myAlias);
            byte[] cipheredFileKey = Crypto.encryptRSA(fileKey, publicKey);
            enc.setFileKey(cipheredFileKey);


        } catch (CryptoException e) {
            e.printStackTrace();
        }

        return enc;
    }

    /**
     * Extracts all the file data as a byte array
     * <li>File name</li>
     * <li>File creator</li>
     * <li>File data</li>
     * <li>File key</li>
     * @param file
     * @return all file data
     */
    static byte[] extractFileData(FileWrapper file) {
        byte[] fileKey = file.getFileKey();
        byte[] fileData = file.getFile() ;
        byte[] metadata = Crypto.toByteArray(file.getFileName() + file.getFileCreator());

        byte [] data = new byte[metadata.length + fileData.length + fileKey.length ];
        System.arraycopy(metadata, 0, data, 0, metadata.length);
        System.arraycopy(fileData, 0, data, metadata.length, fileData.length);
        System.arraycopy(fileKey, 0, data, fileData.length, fileKey.length);

        return data;
    }

    /**
     * Encrypts a list of files
     * @param files
     * @return encrypted files
     */
    static List<EncryptedFileWrapper> encryptFileWrappers(List<FileWrapper> files){

        List<EncryptedFileWrapper> encryptedFiles = new ArrayList<>();

        for(FileWrapper file : files){
            encryptedFiles.add(encryptFileWrapper(file));
        }
        return encryptedFiles;
    }
}
