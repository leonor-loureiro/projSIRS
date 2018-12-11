package client;

import client.exception.BadArgument;
import client.exception.InvalidUser;
import client.exception.TokenInvalid;
import client.exception.UserAlreadyExists;
import client.localFileHandler.FileWrapper;
import client.security.EncryptedFileWrapper;
import client.security.SecurityHandler;

import crypto.Crypto;
import crypto.exception.CryptoException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.*;


public class Communication {
    private String loginToken;
    private String serverUrl = "https://localhost:8080/operations";
    private String authServerUrl = "https://localhost:8081/auth";
    public void ping(){

    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String  loginToken) {
        this.loginToken = loginToken;
    }

    public boolean register(User user) throws UserAlreadyExists, BadArgument {

        RestTemplate restTemplate = restTemplate();

        //create the params
        Map<String, String> msg = new HashMap<String, String>();
        msg.put("username", user.getUsername());
        msg.put("password", String.valueOf(user.getPassword()));
        String kpub = Crypto.toString(user.getPublicKey().getEncoded());
        msg.put("Kpub", kpub);

        //set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //set entity to send
        HttpEntity request = new HttpEntity(msg,headers);

        // send
        ResponseEntity<String> response = restTemplate.postForEntity(authServerUrl + "/register", request, String.class);

        if(response.getStatusCode() == HttpStatus.OK){
            loginToken = response.getBody();
            return true;
        }
        //User already exists
        if(response.getStatusCode() == HttpStatus.CONFLICT)
            throw new UserAlreadyExists(response.getBody());

        //Invalid arguments
        if(response.getStatusCode() == HttpStatus.BAD_REQUEST)
            throw new BadArgument(response.getBody());

        return false;
    }

    public boolean login(User user) throws InvalidUser, BadArgument {

        RestTemplate restTemplate = restTemplate();

        //create the params
        Map<String, String> msg = new HashMap<String, String>();
        msg.put("username", user.getUsername());
        msg.put("password", String.valueOf(user.getPassword()));

        //set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //set entity to send
        HttpEntity request = new HttpEntity(msg,headers);

        // send
        ResponseEntity<String> response = restTemplate.postForEntity(authServerUrl + "/login", request, String.class);

        System.out.println("Status: " + response.getStatusCode().toString());


        if(response.getStatusCode() == HttpStatus.OK){
            loginToken =  response.getBody();
            System.out.println("Token: " + loginToken);
            System.out.println("Login successful");
            return true;
        }

        //User does not exist
        if(response.getStatusCode() == HttpStatus.CONFLICT)
            throw new InvalidUser(response.getBody());

        //Invalid arguments
        if(response.getStatusCode() == HttpStatus.BAD_REQUEST)
            throw new BadArgument(response.getBody());

        return false;

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

        for (EncryptedFileWrapper file : files) {
            System.out.println("got this file " + file.getFileName());
        }

        return SecurityHandler.decryptFileWrappers(Arrays.asList(files));
    }

    public PublicKey getUserKey(String username1, String username2) throws CryptoException, BadArgument, TokenInvalid {
        RestTemplate restTemplate = restTemplate();

        //create the params
        Map<String, String> msg = new HashMap<String, String>();
        msg.put("username1", username1);
        msg.put("username2", username2);
        msg.put("token", loginToken);

        //set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //set entity to send
        HttpEntity request = new HttpEntity(msg, headers);

        // send
        ResponseEntity<String> response = restTemplate.postForEntity(authServerUrl + "/getPublicKey", request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            byte[] encoded = Crypto.toByteArray((response.getBody()));
            return Crypto.recoverPublicKey(encoded);
        }

        if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
            throw new BadArgument(response.getBody());
        }

        if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
            throw new TokenInvalid(response.getBody());
        }

        return null;
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

        FileSystemMessage m = new FileSystemMessage();
        m.setFiles(list);
        restTemp.postForObject(serverUrl+"/upload", m,  ResponseEntity.class);

    }

    public FileWrapper getBackup(User user, String fileName){

        throw new UnsupportedOperationException();
    }

    public void shareFile(User user, EncryptedFileWrapper file, String destUser){
        //TODO: share file
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
