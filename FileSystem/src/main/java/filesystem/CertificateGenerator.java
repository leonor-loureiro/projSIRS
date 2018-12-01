package filesystem;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

public class CertificateGenerator {

    public static void generateCertificate(String Name) throws Exception {
        Crypto.init();
        X509Certificate c = createCaCertificate();

        X509Certificate newC = createSignedCertificate(c);

        FileOutputStream os = new FileOutputStream("./" + "\\src\\main\\resources\\" + "ca"+ Name);
        os.write("-----BEGIN CERTIFICATE-----\n".getBytes("US-ASCII"));
        os.write(Base64.encodeBase64(c.getEncoded(), true));
        os.write("-----END CERTIFICATE-----\n".getBytes("US-ASCII"));
        os.close();

        FileOutputStream os2 = new FileOutputStream("./" + "\\src\\main\\resources\\" + Name);
        os2.write("-----BEGIN CERTIFICATE-----\n".getBytes("US-ASCII"));
        os2.write(Base64.encodeBase64(newC.getEncoded(), true));
        os2.write("-----END CERTIFICATE-----\n".getBytes("US-ASCII"));
        os2.close();
    }

    public static X509Certificate createCaCertificate() throws Exception
    {
        Provider bcProvider = new BouncyCastleProvider();
        Security.addProvider(bcProvider);
        String keystoreFile = "./" + "\\src\\main\\resources\\cakeystore.jks";
        PrivateKey privatekey = (PrivateKey)Crypto.getPrivateKey(keystoreFile,"password","server-keypair","password");
        PublicKey publickey = Crypto.getPublicKey(keystoreFile,"password","server-keypair");

        long now = System.currentTimeMillis();
        Date startDate = new Date(now);

        X500Name dnName = new X500Name("CN=CAserver");
        BigInteger certSerialNumber = new BigInteger(Long.toString(now)); // <-- Using the current timestamp as the certificate serial number

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, 1); // <-- 1 Yr validity

        Date endDate = calendar.getTime();

        String signatureAlgorithm = "SHA256WithRSA"; // <-- Use appropriate signature algorithm based on your keyPair algorithm.

        ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).build(privatekey);
        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(dnName, certSerialNumber, startDate, endDate, dnName, publickey);

        // Extensions --------------------------


        // Basic Constraints
        BasicConstraints basicConstraints = new BasicConstraints(true); // <-- true for CA, false for EndEntity

        certBuilder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, basicConstraints); // Basic Constraints is usually marked as critical.;
        // -------------------------------------

        return new JcaX509CertificateConverter().setProvider(bcProvider).getCertificate(certBuilder.build(contentSigner));
    }

    public static X509Certificate createSignedCertificate(X509Certificate c) throws Exception
    {
        String keystoreFile = "./" + "\\src\\main\\resources\\serverkeystore.jks";
        PrivateKey privatekey = (PrivateKey)Crypto.getPrivateKey(keystoreFile,"password","server-keypair","password");
        PublicKey publickey = Crypto.getPublicKey(keystoreFile,"password","server-keypair");
        Provider bcProvider = new BouncyCastleProvider();
        Security.addProvider(bcProvider);

        long now = System.currentTimeMillis();
        Date startDate = new Date(now);

        X500Name dnName = new X500Name("CN=server,O=codenotfound.com");
        BigInteger certSerialNumber = new BigInteger(Long.toString(now)); // <-- Using the current timestamp as the certificate serial number

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, 1); // <-- 1 Yr validity

        Date endDate = calendar.getTime();

        String signatureAlgorithm = "SHA256WithRSA"; // <-- Use appropriate signature algorithm based on your keyPair algorithm.

        ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).build(privatekey);
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(dnName, certSerialNumber, startDate, endDate, dnName, publickey);

        // Extensions --------------------------


        // Basic Constraints
        BasicConstraints basicConstraints = new BasicConstraints(false); // <-- true for CA, false for EndEntity

        certBuilder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, basicConstraints); // Basic Constraints is usually marked as critical.

        JcaX509ExtensionUtils u = new JcaX509ExtensionUtils();
        AuthorityKeyIdentifier k = u.createAuthorityKeyIdentifier(c);
        certBuilder.addExtension(new ASN1ObjectIdentifier("2.5.29.35"),true,k.toASN1Primitive())   ;
        // -------------------------------------

        return new JcaX509CertificateConverter().setProvider( "BC" )
                .getCertificate( certBuilder.build(contentSigner) );
    }
}
