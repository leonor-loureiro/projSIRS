package client.security;

import client.localFileHandler.FileWrapper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

public interface SecurityHandler {

    static List<Key> generateKeyPair(){
        throw new UnsupportedOperationException();
    }

    static Key generateSyncKey(){
        throw new UnsupportedOperationException();
    }

    static Object cipherObject (Object object, Key key){
        throw new UnsupportedOperationException();
    }
    static Object decipherObject (Object objet, Key key){
        throw new UnsupportedOperationException();
    }

    static Key loadPrivateKey(){
        throw new UnsupportedOperationException();
    }

    static void storePrivateKey(Key key){
        throw new UnsupportedOperationException();
    }


    static  byte[] generateMAC( byte[] content, Key key){

        throw new UnsupportedOperationException();
    }

    static FileWrapper decryptFileWrapper(EncryptedFileWrapper enc){
        FileWrapper file = new FileWrapper();

        file.setFileName(enc.getFileName());
        file.setFileCreator(enc.getFileCreator());
        file.setFileContent(enc.getFile());

        // TODO: Get FileKEY file.setFileKey(enc.getFileKey());

        // TODO: Compare MAC!!

        // TODO: Actually decrypt file

        return file;
    }

    static List<FileWrapper> decryptFileWrappers(List<EncryptedFileWrapper> encriptedFiles){

        List<FileWrapper> list = new ArrayList<>();

        for(EncryptedFileWrapper enc : encriptedFiles){
            list.add(decryptFileWrapper(enc));
        }
        return list;
    }
}
