package com.realtime_monitoring.device_management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;


@Configuration
public class MqttSslConfig {

    @Value("${mqtt.ca-cert}")
    private String caCertPath;

    @Value("${mqtt.client-cert}")
    private String clientCertPath;

    @Value("${mqtt.client-key}")
    private String clientKeyPath;

    @Bean
    public SSLContext mqttSslContext() throws Exception {

        // ==========================================
        // CA CERTIFICATE
        // ==========================================

        CertificateFactory certificateFactory =
                CertificateFactory.getInstance("X.509");

        Certificate caCertificate;

        try (FileInputStream input =
                     new FileInputStream(caCertPath)) {

            caCertificate =
                    certificateFactory.generateCertificate(input);
        }

        KeyStore trustStore =
                KeyStore.getInstance(KeyStore.getDefaultType());

        trustStore.load(null, null);

        trustStore.setCertificateEntry(
                "ca",
                caCertificate
        );

        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm()
                );

        trustManagerFactory.init(trustStore);

        // ==========================================
        // CLIENT CERTIFICATE
        // ==========================================

        Certificate clientCertificate;

        try (FileInputStream input =
                     new FileInputStream(clientCertPath)) {

            clientCertificate =
                    certificateFactory.generateCertificate(input);
        }

        // ==========================================
        // CLIENT PRIVATE KEY
        // ==========================================

        String pem = Files.readString(
                Path.of(clientKeyPath)
        );

        pem = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes =
                Base64.getDecoder().decode(pem);

        PKCS8EncodedKeySpec keySpec =
                new PKCS8EncodedKeySpec(keyBytes);

        PrivateKey privateKey =
                KeyFactory
                        .getInstance("RSA")
                        .generatePrivate(keySpec);

        // ==========================================
        // CLIENT KEYSTORE
        // ==========================================

        KeyStore clientKeyStore =
                KeyStore.getInstance(
                        KeyStore.getDefaultType()
                );

        clientKeyStore.load(null, null);

        clientKeyStore.setKeyEntry(
                "client",
                privateKey,
                new char[0],
                new Certificate[]{clientCertificate}
        );

        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance(
                        KeyManagerFactory.getDefaultAlgorithm()
                );

        keyManagerFactory.init(
                clientKeyStore,
                new char[0]
        );

        // ==========================================
        // SSL CONTEXT
        // ==========================================

        SSLContext sslContext =
                SSLContext.getInstance("TLS");

        sslContext.init(
                keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers(),
                null
        );

        return sslContext;
    }
}