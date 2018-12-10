package client;

import client.localFileHandler.FileWrapper;
import client.security.EncryptedFileWrapper;
import client.security.SecurityHandler;
import client.security.Token;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Communication {
    private Token loginToken;
    private String serverUrl = "https://localhost:8080/operations";
    public void ping(){

    }

    public Token getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(Token loginToken) {
        this.loginToken = loginToken;
    }

    public boolean register(User user){


        return true;
    }

    public void login(User user) {
        System.out.println("Pretending to login...");

    }

    public List<FileWrapper> getFiles(User user){
        RestTemplate restTemplate = restTemplate();

        //make the object
        FileSystemMessage obj = new FileSystemMessage();

        obj.setName(user.getUsername());


        //set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //set entity to send
        HttpEntity entity = new HttpEntity(obj,headers);

        // send
        ResponseEntity<FileSystemMessage> out = restTemplate.exchange(serverUrl+"/download",HttpMethod.POST, entity
                , FileSystemMessage.class);


        EncryptedFileWrapper[] files = out.getBody().getFiles();

        for(int i = 0;i < files.length;i++){
            System.out.println("got this file " + files[i].getFileName());
        }

        return SecurityHandler.decryptFileWrappers(Arrays.asList(files));
    }

    /**
     * Sends the staged files to the server
     * @param user the user sending the files
     * @param files the list of files to be sent
     */
    public void putFiles(User user, List<FileWrapper> files){
        RestTemplate restTemp = restTemplate();
        EncryptedFileWrapper[] list;
        list = new EncryptedFileWrapper[files.size()];
        EncryptedFileWrapper enc;
        System.out.println(files.size());
        for(int i = 0;i< files.size();i++){
            System.out.println(files.get(i).getFileName());
            //list.add(SecurityHandler.encryptFileWrapper(f, user.getPublicKey()));
            enc = new EncryptedFileWrapper();
            enc.setFileCreator(files.get(i).getFileCreator());
            enc.setFileName(files.get(i).getFileName());
            enc.setFileKey("Key".getBytes());
            list[i] = enc;
        }

        //TODO : Replace with FileSystemMessage
        FileSystemMessage m = new FileSystemMessage();
        m.setFiles(list);
        restTemp.postForObject(serverUrl+"/upload", m,  ResponseEntity.class);

    }

    public void shareFile(User user, FileWrapper file){
        throw new UnsupportedOperationException();
    }

    /**
     * Generates a rest template for https
     * @return https rest template
     */
    private RestTemplate restTemplate()
    {

        File trustStore = new File("./" + "\\src\\main\\resources\\clienttruststore.jks");

        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();

        try {
            sslContextBuilder.loadTrustMaterial(trustStore, "password".toCharArray());
        } catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException e) {
            e.printStackTrace();
        }

        SSLContext sslContext = null;
        try {
            sslContext = sslContextBuilder.build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }


        SSLConnectionSocketFactory socketFactory = null;
        if (sslContext != null) {
            socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        }

        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();


        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory);

    }
}
