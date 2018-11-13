package client;

import client.localFileHandler.FileWrapper;
import client.security.Token;

import java.util.List;

public class Communication {
    private Token loginToken;

    public void ping(){

    }

    public Token getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(Token loginToken) {
        this.loginToken = loginToken;
    }

    public void register(User user){
        throw new UnsupportedOperationException();
    }

    public Token login(User user){
        throw new UnsupportedOperationException();
    }

    public List<FileWrapper> getFiles(User user){

        throw new UnsupportedOperationException();
    }

    public void putFiles(User user, List<FileWrapper> files){
        throw new UnsupportedOperationException();
    }

    public void addNewFile(User user, FileWrapper file){
        throw new UnsupportedOperationException();
    }

    public void shareFile(User user, FileWrapper file){
        throw new UnsupportedOperationException();
    }
}
