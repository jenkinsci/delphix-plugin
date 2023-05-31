package io.jenkins.plugins.util;

import com.delphix.dct.ApiClient;
import com.delphix.dct.ApiException;
import okhttp3.OkHttpClient;

import javax.net.ssl.*;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class ApiClientInit {
    public static ApiClient init()
            throws ApiException, NoSuchAlgorithmException, KeyManagementException {
        SSLSocketFactory sslSocketFactory;
        BlindTrustManager trustManager = new BlindTrustManager();
        SSLContext sslContext = SSLContext.getInstance("TLS");

        sslContext.init(null, new TrustManager[] { trustManager }, null);
        sslSocketFactory = sslContext.getSocketFactory();

        var apiClient = new ApiClient(new OkHttpClient(
                new OkHttpClient.Builder().hostnameVerifier(NoopHostnameVerifier.INSTANCE)
                        .sslSocketFactory(sslSocketFactory, trustManager)));
        apiClient.setUserAgent(Constant.USER_AGENT);
        apiClient.addDefaultHeader(Constant.CLIENT_NAME_HEADER, Constant.CLIENT_NAME);
        return apiClient;
    }

    private static class NoopHostnameVerifier implements HostnameVerifier {

        public static final NoopHostnameVerifier INSTANCE = new NoopHostnameVerifier();

        @Override
        public boolean verify(final String s, final SSLSession sslSession) {
            return true;
        }

        @Override
        public final String toString() {
            return "NO_OP";
        }

    }

    private static class BlindTrustManager extends X509ExtendedTrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {

        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {

        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
