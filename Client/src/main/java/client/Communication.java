package client;

import client.localFileHandler.FileWrapper;
import client.security.Token;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    /**
     * Sends the staged files to the server
     * @param user the user sending the files
     * @param files the list of files to be sent
     */
    public void putFiles(User user, List<FileWrapper> files){
        RestTemplate restTemp = restTemplate();
        //ResponseEntity<String> res = Rest.getForEntity("https://localhost:8080/operations/upload", String.class);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();

        for(FileWrapper f: files){
            body.add("files", f.getMap());
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);

        String serverUrl = "https://localhost:8080/operations/upload";

        ResponseEntity<String> res = restTemp
                .postForEntity(serverUrl, requestEntity, String.class);



        System.out.println(res.getStatusCode());
//        ResponseEntity<String> suptoauth = Rest.getForEntity("https://localhost:8081/auth/test",String.class);
//        System.out.println(suptoauth.getStatusCode());

    }

    public void shareFile(User user, FileWrapper file){
        throw new UnsupportedOperationException();
    }

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
