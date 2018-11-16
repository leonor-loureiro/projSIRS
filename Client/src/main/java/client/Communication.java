package client;

import client.security.Token;
import com.sun.net.httpserver.HttpsServer;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;


public class Communication {
    private Token loginToken;

    public void ping(){
        try {
            HttpsServer server = HttpsServer.create(new InetSocketAddress(8080), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public RestTemplate restTemplate() throws Exception
    {

        File trustStore = new File("./" + "\\src\\main\\resources\\clienttruststore.jks");


        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();

        sslContextBuilder.loadTrustMaterial(trustStore,"password".toCharArray());

        SSLContext sslContext = sslContextBuilder.build();


        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();


        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory);

    }
}
