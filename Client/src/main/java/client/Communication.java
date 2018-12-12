package client;

import client.exception.BadArgument;
import client.exception.InvalidUser;
import client.exception.TokenInvalid;
import client.exception.UserAlreadyExists;
import client.localFileHandler.FileWrapper;
import client.security.EncryptedFileWrapper;

import crypto.Crypto;
import crypto.exception.CryptoException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.xml.ws.http.HTTPException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.*;


public class Communication {
    /**
     * Token used for communication with FileSystem, given by Auth
     */
    private String loginToken;
    private String serverUrl = "https://localhost:8080/operations";
    private String authServerUrl = "https://localhost:8081/auth";


    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String  loginToken) {
        this.loginToken = loginToken;
    }


    /* ******************************************************************************
     *
     *                      Auth Server Communication
     *
     * ******************************************************************************/

    /**
     * Register Client in the system
     * @param user the logged in user info
     * @return true if the register succeeded
     * @throws UserAlreadyExists if the user already exists
     * @throws BadArgument if the information provided doesn't follow the correct format
     */
    public boolean register(User user) throws UserAlreadyExists, BadArgument {
        RestTemplate restTemplate = restTemplate();

        //create the parameters
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

        try {
            // send
            ResponseEntity<String> response = restTemplate.postForEntity(authServerUrl + "/register", request, String.class);

            if(response.getStatusCode() == HttpStatus.OK){
                loginToken = response.getBody();
                return true;
            }

        }catch (HttpStatusCodeException e){
            //User already exists
            if(e.getStatusCode() == HttpStatus.CONFLICT) {
                throw new UserAlreadyExists("Already exits user " + user.getUsername());
            }

            //Invalid arguments
            if(e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new BadArgument("Invalid username or password");
            }
        }
        return false;
    }

    /**
     * Logs in the user to the server
     * Provides the user with a temporary token that allows for a limited time
     * the communication with FileSystem
     * @param user the user to be logged in
     * @return true if log in was successful
     * @throws InvalidUser if no username was found or password doesn't match
     * @throws BadArgument if the information didn't follow expected format
     */
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

        try {
            // send
            ResponseEntity<String> response = restTemplate.postForEntity(authServerUrl + "/login", request, String.class);

            if(response.getStatusCode() == HttpStatus.OK){
                loginToken =  response.getBody();
                System.out.println("Login successful");
                return true;
            }


        }catch (HttpStatusCodeException e){

            //User does not exist
            if(e.getStatusCode() == HttpStatus.CONFLICT) {
                throw new InvalidUser("Invalid username or password");
            }

            //Invalid arguments
            if(e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new BadArgument("Invalid username or password");
            }
        }

        return false;

    }

    /**
     * Requests the public key of a certain user
     * @param username1 the user requesting username2's key
     * @param username2 the user who's key is being requested
     * @return the key of username2's user
     * @throws CryptoException When the interpretation of public key fails
     * @throws BadArgument When the request didn't follow proper format
     * @throws TokenInvalid When the Token is invalid (bad token or timeout)
     */
    public PublicKey getUserKey(String username1, String username2) throws CryptoException, BadArgument, TokenInvalid, InvalidUser {
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

        try {
            // send
            ResponseEntity<String> response = restTemplate.postForEntity(authServerUrl + "/getPublicKey", request, String.class);

            // Process response (Key)
            if (response.getStatusCode() == HttpStatus.OK) {
                byte[] encoded = Crypto.toByteArray((response.getBody()));
                return Crypto.recoverPublicKey(encoded);
            }

        }catch (HttpStatusCodeException e){
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new BadArgument("Invalid username");
            }

            if (e.getStatusCode() == HttpStatus.PRECONDITION_FAILED) {
                throw new TokenInvalid("Session expired. Login again.");
            }

            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                throw new InvalidUser("User " + username2 + " is not registered");
            }
        }
        return null;
    }



    /* ******************************************************************************
     *
     *                      File System Communication
     *
     * ******************************************************************************/

    /**
     * Gets all the remote files of the given user
     * @param user user to which the files belong to
     * @return the list of user's files
     * @throws BadArgument if the request arguments didn't match the defined format
     */
    public EncryptedFileWrapper[] getFiles(User user) throws BadArgument {
        RestTemplate restTemplate = restTemplate();

        //make the object
        FileSystemMessage obj = new FileSystemMessage();

        obj.setUserName(user.getUsername());
        obj.setToken(loginToken);

        //set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //set entity to send
        HttpEntity entity = new HttpEntity(obj,headers);
        try {
            // send
            ResponseEntity<FileSystemMessage> out = restTemplate.exchange(serverUrl + "/download", HttpMethod.POST, entity
                    , FileSystemMessage.class);


            // get Files
            EncryptedFileWrapper[] files = out.getBody().getFiles();

            System.out.println("Downloaded files:");
            for (EncryptedFileWrapper file : files) {
                System.out.println("- " + file.getFileName());
            }

            return files;
        }catch (HttpStatusCodeException e){
            //Invalid arguments
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new BadArgument("Bad input. Check filenames for special characters.");
            }
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new BadArgument("No file has been added");
            }

        }
        return null;
    }


    /**
     * Sends the staged files to the server
     * @param user the user sending the files
     * @param files the list of files to be sent
     */
    public void putFiles(User user, EncryptedFileWrapper[] files){
        RestTemplate restTemp = restTemplate();


        FileSystemMessage message = new FileSystemMessage();
        message.setFiles(files);
        message.setUserName(user.getUsername());
        message.setToken(loginToken);
        restTemp.postForObject(serverUrl+"/upload", message,  ResponseEntity.class);

    }

    /**
     * Shares a file from logged in user to another user
     * @param user logged in user
     * @param file file to be shared
     * @param destUser target user to receive file
     */
    public void shareFile(User user, EncryptedFileWrapper file, String destUser){
        RestTemplate restTemp = restTemplate();

        FileSystemMessage message = new FileSystemMessage();
        message.setUserName(user.getUsername());
        message.setUserToShareWith(destUser);
        message.setToken(loginToken);

        message.setFiles(new EncryptedFileWrapper[]{ file });
        restTemp.postForObject(serverUrl+"/share", message,  ResponseEntity.class);
    }

    /**
     * Requests an older version of the file and resets remote's current head to that file
     * @param user logged in user
     * @param filename name of the file to be reset to an older version
     * @return older version of the file with given name
     * @throws BadArgument if the request format wasn't valid
     */
    public EncryptedFileWrapper[] getOldVersion(User user,String filename) throws BadArgument {
        RestTemplate restTemp = restTemplate();

        FileSystemMessage message = new FileSystemMessage();

        message.setUserName(user.getUsername());
        message.setBackUpFileName(filename);
        message.setToken(loginToken);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //set entity to send
        HttpEntity entity = new HttpEntity(message,headers);

       try {
           // send
           ResponseEntity<FileSystemMessage> out = restTemp.exchange(serverUrl + "/getoldversion", HttpMethod.POST, entity
                   , FileSystemMessage.class);


           EncryptedFileWrapper[] files = out.getBody().getFiles();

           for (EncryptedFileWrapper file : files) {
               System.out.println("Retrieved file: " + file.getFileName());
           }

           return files;

       }catch (HttpStatusCodeException e){

           if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
               throw new BadArgument("Invalid parameters");
           }
           if (e.getStatusCode() == HttpStatus.CONFLICT) {
               throw new BadArgument("Backup doesn't exist");
           }

       }
       return null;

    }


    /* ******************************************************************************
     *
     *                      Auxiliary Communication Function
     *
     * ******************************************************************************/


    /**
     * Generates a rest template for https
     * @return https rest template
     */
    private RestTemplate restTemplate()
    {


        Properties properties = new Properties();
        InputStream input = null;

        try{
            input = new FileInputStream("./" + "\\src\\main\\resources\\config.properties");
            properties.load(input);

        }catch (IOException e){
            e.printStackTrace();
        }


        File trustStore = new File("./" + "\\src\\main\\resources\\clienttruststore.jks");

        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();

        try {
            sslContextBuilder.loadTrustMaterial(trustStore, properties.getProperty("trustpw").toCharArray());
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
