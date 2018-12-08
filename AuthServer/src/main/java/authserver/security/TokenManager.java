package authserver.security;

import crypto.Crypto;
import crypto.exception.CryptoException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.security.PublicKey;
import java.util.Date;

/**
 * This class implements the functions responsible for managing authentication tokens
 * <li>Create a token</li>
 * <li>Validate a token</li>
 */
public class TokenManager {

    private static final String keystoreFile = "./" + "\\src\\main\\resources\\serverkeystore.jks";
    private static final String keystorePwd = "password";
    private static final String keyPwd = "password";
    private static final String myAlias = "server-keypair";

    /**
     * Creates a JTW token
     * @param id token id
     * @param issuer principal that issued the token
     * @param subject subject the token was issued for
     * @param validPeriod how long the token is valid for (milliseconds)
     * @return signed compact token
     */
    public static String createJTW(String id, String issuer, String subject, long validPeriod){

        if(id == null || id.isEmpty() || issuer == null || issuer.isEmpty() ||
                subject == null || subject.isEmpty() || validPeriod < 0)
            return null;

        //The JWT signature algorithm we will be using to sign the token
        //PKCS#1 signature with SHA-256
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;

        long currentTimeMillis = System.currentTimeMillis();
        Date currDate = new Date(currentTimeMillis);
        Date expiredDate = new Date(currentTimeMillis + validPeriod);

        // Get the private key for the signature
        Key signingKey = null;
        try {
            signingKey = Crypto.getPrivateKey(keystoreFile, keystorePwd, myAlias, keyPwd);
        } catch (CryptoException e) {
            return null;
        }

        // Set the JWT Claims
        JwtBuilder tokenBuilder = Jwts.builder().setId(id)     //unique identifier of the token
                .setIssuedAt(currDate)                         //time the token was issued
                .setSubject(subject)                           //the subject the token was issued to
                .setIssuer(issuer)                             //principal that issued the token
                .setExpiration(expiredDate)
                .signWith(signatureAlgorithm, signingKey);     //signature


        //Builds the token and serializes it to a compact, URL safe string
        return tokenBuilder.compact();
    }

    /**
     * Checks if the token is valid and satisfies all claims
     * @param jtw token
     * @param issuer supposed issuer of the token
     * @param subject subject the token was supposedly created for
     * @return true if valid; false otherwise
     */
    public static boolean validateJTW(String jtw, String issuer, String subject){
        PublicKey key = null;
        try {
            key = Crypto.getPublicKey(keystoreFile, keystorePwd, myAlias);
        } catch (CryptoException e) {
            return false;
        }

        // Parse JTW to obtain claims
        try {
            Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jtw).getBody();

            String jtwSubject = claims.getSubject();
            String jtwIssuer = claims.getIssuer();
            Date jtwExpireDate = claims.getExpiration();
            Date jtwIssuedAt = claims.getIssuedAt();
            Date currentDate = new Date(System.currentTimeMillis());

            //Print details
            System.out.println("ID: " + claims.getId());
            System.out.println("Subject: " + claims.getSubject());
            System.out.println("Issuer: " + claims.getIssuer());
            System.out.println("Expiration: " + claims.getExpiration());

            return jtwSubject.equals(subject) && jtwIssuer.equals(issuer) &&
                    jtwIssuedAt.before(currentDate) && jtwExpireDate.after(currentDate);


        }catch (Exception e){
            return false;
        }
    }


}
