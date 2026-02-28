/*
 * Decompiled with CFR 0.152.
 */
package io.zhile.crack.atlassian.keygen;

import atlassian.LicenseManager;
import io.zhile.crack.atlassian.utils.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;

public class Encoder {
    private static final String PRIVATE_KEY_STR = LicenseManager.privateKeyBase64;
    private static final PrivateKey PRIVATE_KEY;

    public static String encode(String licenseText) throws Exception {
        byte[] licenseData = Encoder.zipText(licenseText.getBytes());
        byte[] text = new byte[licenseData.length + 5];
        text[0] = 13;
        text[1] = 14;
        text[2] = 12;
        text[3] = 10;
        text[4] = 15;
        System.arraycopy(licenseData, 0, text, 5, licenseData.length);
        return Encoder.packLicense(text, Encoder.sign(text));
    }

    public static byte[] sign(byte[] bytes) throws Exception {
        Signature signature = Signature.getInstance("SHA1withDSA");
        signature.initSign(PRIVATE_KEY);
        signature.update(bytes);
        return signature.sign();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static byte[] zipText(byte[] licenseText) throws IOException {
        byte[] buff = new byte[64];
        ByteArrayInputStream in = new ByteArrayInputStream(licenseText);
        DeflaterInputStream deflater = new DeflaterInputStream(in, new Deflater());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int len;
            while ((len = deflater.read(buff)) > 0) {
                out.write(buff, 0, len);
            }
            byte[] byArray = out.toByteArray();
            return byArray;
        }
        finally {
            out.close();
            deflater.close();
            in.close();
        }
    }

    private static String packLicense(byte[] text, byte[] hash) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dOut = new DataOutputStream(out);
        dOut.writeInt(text.length);
        dOut.write(text);
        dOut.write(hash);
        String result = Base64.encode(out.toByteArray()).trim();
        return Encoder.split(result + "X02" + Integer.toString(result.length(), 31));
    }

    private static String split(String licenseData) {
        if (licenseData == null || licenseData.length() <= 0) {
            return licenseData;
        }
        char[] chars = licenseData.toCharArray();
        StringBuilder buf = new StringBuilder(chars.length + chars.length / 76);
        for (int i = 0; i < chars.length; ++i) {
            buf.append(chars[i]);
            if (i <= 0 || i % 76 != 0) continue;
            buf.append('\n');
        }
        return buf.toString();
    }

    static {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("DSA");
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decode(PRIVATE_KEY_STR));
            PRIVATE_KEY = keyFactory.generatePrivate(privateKeySpec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}

