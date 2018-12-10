import crypto.Crypto;
import crypto.TokenManager;
import crypto.exception.CryptoException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Random;

public class TokenTests {

    String subject = "username";
    String issuer = "issuer";
    String id;
    KeyPair keyPair;

    @Before
    public void setUp() throws CryptoException {
        id = new Random().nextInt() + "";
        keyPair = Crypto.generateRSAKeys();
    }
    @Test
    public void success(){
        String token = TokenManager.createJTW(id, issuer, subject, 50000, keyPair.getPrivate());
        Assert.assertTrue(TokenManager.validateJTW(token, issuer, subject, keyPair.getPublic()));
    }

    @Test
    public void invalidSubject(){
        String token = TokenManager.createJTW(id, issuer, subject, 50000, keyPair.getPrivate());
        Assert.assertFalse(TokenManager.validateJTW(token, issuer, "username2", keyPair.getPublic()));
    }

    @Test
    public void invalidIssuer(){
        String token = TokenManager.createJTW(id, issuer, subject, 50000, keyPair.getPrivate());
        Assert.assertFalse(TokenManager.validateJTW(token, "issuer2", subject, keyPair.getPublic()));
    }

    @Test
    public void invalidKey() throws CryptoException {
        String token = TokenManager.createJTW(id, issuer, subject, 50000, keyPair.getPrivate());
        PublicKey publicKey = Crypto.generateRSAKeys().getPublic();
        Assert.assertFalse(TokenManager.validateJTW(token, issuer, subject, publicKey));
    }

    @Test
    public void timeout() throws CryptoException {
        String token = TokenManager.createJTW(id, issuer, subject, 0, keyPair.getPrivate());
        Assert.assertFalse(TokenManager.validateJTW(token, issuer, subject, keyPair.getPublic()));
    }

}
