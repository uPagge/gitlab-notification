package dev.struchkov.bot.gitlab.core.utils;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static dev.struchkov.haiti.utils.Exceptions.utilityClass;

public class OkHttpUtil {

    private static final Logger log = LoggerFactory.getLogger(OkHttpUtil.class);

    public OkHttpUtil() {
        utilityClass();
    }

    public static void ignoreCertificate(OkHttpClient.Builder builder) {
        log.info("Initialising httpUtil with default configuration");
        configureToIgnoreCertificate(builder);
    }

    //Setting testMode configuration. If set as testMode, the connection will skip certification check
    private static void configureToIgnoreCertificate(OkHttpClient.Builder builder) {
        log.warn("Ignore Ssl Certificate");
        try {

            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            log.warn("Exception while configuring IgnoreSslCertificate" + e, e);
        }
    }

}
