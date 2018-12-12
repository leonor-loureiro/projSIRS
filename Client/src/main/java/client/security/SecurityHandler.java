package client.security;

import client.exception.FileCorrupted;
import client.localFileHandler.FileWrapper;
import crypto.Crypto;
import crypto.exception.CryptoException;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public interface SecurityHandler {

    /**
     * turns a encrypted fileWrapper into a filewrapper by decrypting it
     * @param enc encrypted File Wrapper
     * @param privateKey the private key used to get the key to decrypt the filewrapper
     * @return the decrypted file
     * @throws FileCorrupted if the file was corrupted or its' integrity compromised
     */
    static FileWrapper decryptFileWrapper(EncryptedFileWrapper enc, Key privateKey) throws FileCorrupted {
        FileWrapper file = new FileWrapper();

        // Set file
        file.setFileName(enc.getFileName());
        // Set file creator
        file.setFileCreator(enc.getFileCreator());

        // Decipher file key
        try {
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
     * Decrypts a list of File Wrappers
     * @param encryptedFiles encrypted
     * @param privateKey private key to access key to decipher file wrapper
     * @return list of decrypted file wrappers
     */
    static List<FileWrapper> decryptFileWrappers(List<EncryptedFileWrapper> encryptedFiles, PrivateKey privateKey){

        List<FileWrapper> list = new ArrayList<>();

        for(EncryptedFileWrapper enc : encryptedFiles){
            try {
                list.add(decryptFileWrapper(enc, privateKey));
            } catch (FileCorrupted fileCorrupted) {
                System.out.println("File " + enc.getFileName() + " is corrupted.");
            }
        }
        return list;
    }

    /**
     * Encrypts a file with it's fileKey.
     * Turns a FileWrapper into a encrypted file wrapper
     * @param file file to be encrypted
     * @param publicKey key to encrypt file encrypting key
     * @return the encrypted file wrapper
     */
    static EncryptedFileWrapper encryptFileWrapper(FileWrapper file, PublicKey publicKey){
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
        byte[] metadata = (file.getFileName() + file.getFileCreator()).getBytes();

        byte [] data = new byte[metadata.length + fileData.length + fileKey.length ];
        System.arraycopy(metadata, 0, data, 0, metadata.length);
        System.arraycopy(fileData, 0, data, metadata.length, fileData.length);
        System.arraycopy(fileKey, 0, data, fileData.length, fileKey.length);

        return data;
    }

    /**
     * Encrypts a list of files
     * @param files list of wrappers to be encrypted
     * @return encrypted files
     */
    static List<EncryptedFileWrapper> encryptFileWrappers(List<FileWrapper> files, PublicKey publicKey){

        List<EncryptedFileWrapper> encryptedFiles = new ArrayList<>();

        for(FileWrapper file : files){
            encryptedFiles.add(encryptFileWrapper(file, publicKey));
        }
        return encryptedFiles;
    }


}
