package com.realtime_monitoring.device_management.security;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;

import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;

import java.math.BigInteger;

import java.nio.file.Files;
import java.nio.file.Path;

import java.security.PrivateKey;
import java.security.SecureRandom;

import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.Base64;
import java.util.Date;

@Service
public class CertificateService {
        @Value("${pki.ca-cert}")
        private String caCertPath;
        @Value("${pki.ca-key}")
        private String caKeyPath;

       // private static final String CA_CERT_PATH = "C:/mqtt/backend/ca/ca.crt";

       // private static final String CA_KEY_PATH = "C:/mqtt/backend/ca/ca.key";

        public String signCsr(
                        String csrPem,
                        String deviceId) throws Exception {

                System.out.println("========================================");

                System.out.println("SIGNING DEVICE CSR");

                System.out.println("Device ID: " + deviceId);

                X509Certificate caCertificate = loadCertificate(caCertPath);

                System.out.println("CA certificate loaded.");

                System.out.println("CA subject: " + caCertificate.getSubjectX500Principal());

                PrivateKey caPrivateKey = loadPrivateKey(caKeyPath);

                System.out.println("CA private key loaded.");

                PKCS10CertificationRequest csr = loadCsr(csrPem);

                System.out.println("CSR parsed successfully.");

                // verify CSR sighnature

                JcaPKCS10CertificationRequest jcaCsr = new JcaPKCS10CertificationRequest(csr);

                boolean validCsr = jcaCsr.isSignatureValid(
                                new org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder()
                                                .build(jcaCsr.getPublicKey()));

                if (!validCsr) {
                        throw new SecurityException("CSR signature is invalid");
                }

                System.out.println("CSR signature verified.");

                var devicePublicKey = jcaCsr.getPublicKey();

                SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(devicePublicKey.getEncoded());

                X509CertificateHolder caHolder = new X509CertificateHolder(caCertificate.getEncoded());

                Instant now = Instant.now();

                Date notBefore = Date.from(now.minus(1, ChronoUnit.MINUTES));

                Date notAfter = Date.from(now.plus(730, ChronoUnit.DAYS));

                BigInteger serialNumber = new BigInteger(128, new SecureRandom()).abs();

                X500Name issuer = caHolder.getSubject();

                X500Name subject = new X500Name("C=TN," + "O=Realtime Monitoring," + "CN=" + deviceId);

                X509v3CertificateBuilder builder = new X509v3CertificateBuilder(issuer, serialNumber, notBefore,
                                notAfter,
                                subject,
                                publicKeyInfo);

                JcaX509ExtensionUtils extensionUtils = new JcaX509ExtensionUtils();

                builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));

                builder.addExtension(Extension.keyUsage, true,
                                new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));

                builder.addExtension(
                                Extension.extendedKeyUsage,
                                false,
                                new ExtendedKeyUsage(
                                                KeyPurposeId.id_kp_clientAuth));

                builder.addExtension(
                                Extension.subjectKeyIdentifier,
                                false,
                                extensionUtils.createSubjectKeyIdentifier(
                                                publicKeyInfo));

                builder.addExtension(
                                Extension.authorityKeyIdentifier,
                                false,
                                extensionUtils.createAuthorityKeyIdentifier(
                                                caHolder));

                ContentSigner signer = new JcaContentSignerBuilder(
                                "SHA256withRSA")
                                .build(caPrivateKey);

                X509CertificateHolder holder = builder.build(signer);

                X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(holder);

                certificate.verify(caCertificate.getPublicKey());

                System.out.println("Certificate signature verified.");

                certificate.checkValidity();

                System.out.println("Certificate validity verified.");

                System.out.println("Certificate subject: " + certificate.getSubjectX500Principal());

                System.out.println("Certificate issuer: " + certificate.getIssuerX500Principal());

                System.out.println("Certificate serial: " + certificate.getSerialNumber());

                System.out.println("Certificate valid from: " + certificate.getNotBefore());

                System.out.println("Certificate valid until: " + certificate.getNotAfter());

                System.out.println("Extended Key Usage: " + certificate.getExtendedKeyUsage());

                try {
                        certificate.verify(caCertificate.getPublicKey());
                        System.out.println("CA signature verification: OK");
                } catch (Exception e) {
                        System.err.println("CA signature verification FAILED");
                        throw e;
                }
                System.out.println("========================================");
                System.out.println("DEVICE CERTIFICATE SIGNED SUCCESSFULLY");
                System.out.println("========================================");
                return certificateToPem(certificate);
        }

        private X509Certificate loadCertificate(String path) throws Exception {

                try (
                                FileReader reader = new FileReader(path);
                                PemReader pemReader = new PemReader(reader)) {
                        PemObject pem = pemReader.readPemObject();
                        if (pem == null) {
                                throw new IllegalArgumentException("Invalid CA certificate PEM");
                        }
                        CertificateFactory factory = CertificateFactory.getInstance("X.509");
                        try (
                                        ByteArrayInputStream input = new ByteArrayInputStream(pem.getContent())) {
                                return (X509Certificate) factory.generateCertificate(input);
                        }
                }
        }

        private PKCS10CertificationRequest loadCsr(String csrPem) throws Exception {
                try (
                                PemReader reader = new PemReader(new StringReader(csrPem))) {
                        PemObject pem = reader.readPemObject();
                        if (pem == null) {
                                throw new IllegalArgumentException("Invalid CSR PEM");
                        }
                        return new PKCS10CertificationRequest(pem.getContent());
                }
        }

        private PrivateKey loadPrivateKey(String path) throws Exception {
                String pem = Files.readString(Path.of(path));
                String key = pem.replace("-----BEGIN PRIVATE KEY-----",
                                "")
                                .replace("-----END PRIVATE KEY-----",
                                                "")
                                .replaceAll("\\s+",
                                                "");

                byte[] decoded = Base64.getDecoder().decode(key);
                java.security.spec.PKCS8EncodedKeySpec spec = new java.security.spec.PKCS8EncodedKeySpec(decoded);
                return java.security.KeyFactory.getInstance("RSA").generatePrivate(spec);
        }

        private String certificateToPem(X509Certificate certificate) throws Exception {
                StringWriter writer = new StringWriter();
                try (PemWriter pemWriter = new PemWriter(writer)) {
                        pemWriter.writeObject(new PemObject("CERTIFICATE", certificate.getEncoded()));
                }
                return writer.toString();
        }
}