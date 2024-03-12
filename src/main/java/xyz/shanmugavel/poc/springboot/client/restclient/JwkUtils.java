package xyz.shanmugavel.poc.springboot.client.restclient;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.SneakyThrows;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

public final class JwkUtils {

    private JwkUtils() {
    }

    public static RSAKey generateRsa() {
        KeyPair keyPair = JwkUtils.generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .keyUse(KeyUse.SIGNATURE)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    public static ECKey generateEc() {
        KeyPair keyPair = JwkUtils.generateEcKey();
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        Curve curve = Curve.forECParameterSpec(publicKey.getParams());

        return new ECKey.Builder(curve, publicKey)
                .keyUse(KeyUse.SIGNATURE)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    @SneakyThrows
    private static KeyPair generateRsaKey() {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    @SneakyThrows
    private static KeyPair generateEcKey() {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        return keyPairGenerator.generateKeyPair();
    }

}

