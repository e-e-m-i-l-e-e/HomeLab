package home.lab.jfrog;

import java.security.*;

public class License {
    public static String licenseKey = "LICENSE";

    public static PublicKey publicKey;
    public static PrivateKey privateKey;

    public static void generateKeyPair(Provider provider) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", provider);
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

    public static boolean isReady() {
        return publicKey != null;
    }
}
