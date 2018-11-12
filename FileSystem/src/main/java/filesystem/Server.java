package filesystem;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;


public class Server {

    public static void main(String[] args) {
        Server s = new Server();

        s.CreateServer();
    }

    public void CreateServer()
    {
        HttpsServer httpsServer = null;

        InetSocketAddress address = null;
        SSLContext sslContext = null;

        KeyManagerFactory kmf = null;

        TrustManagerFactory tmf = null;

        try {
            address = new InetSocketAddress( InetAddress.getLocalHost (),8080);
            httpsServer = HttpsServer.create(address, 0);
            sslContext = SSLContext.getInstance ( "TLS" );

            char[] password = "simulator".toCharArray ();
            KeyStore ks = null;
            ks = KeyStore.getInstance ( "JKS" );
            FileInputStream fis = null;

            ClassLoader classLoader = getClass().getClassLoader();

            File file = new File(classLoader.getResource("keystore").getFile());

            fis = new FileInputStream( "lig.keystore" );
            ks.load ( fis, password );

            kmf = KeyManagerFactory.getInstance ( "SunX509" );
            kmf.init ( ks, password );

            tmf = TrustManagerFactory.getInstance ( "SunX509" );
            tmf.init ( ks );

        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            sslContext.init ( kmf.getKeyManagers (), tmf.getTrustManagers (), null );
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        httpsServer.setHttpsConfigurator ( new HttpsConfigurator( sslContext )
        {
            public void configure ( HttpsParameters params )
            {
                try
                {
                    // initialise the SSL context
                    SSLContext c = SSLContext.getDefault ();
                    SSLEngine engine = c.createSSLEngine ();
                    params.setNeedClientAuth ( false );
                    params.setCipherSuites ( engine.getEnabledCipherSuites () );
                    params.setProtocols ( engine.getEnabledProtocols () );

                    // get the default parameters
                    SSLParameters defaultSSLParameters = c.getDefaultSSLParameters ();
                    params.setSSLParameters ( defaultSSLParameters );
                }
                catch ( Exception ex )
                {
                    ex.printStackTrace();
                }
            }
        } );

    }

}
