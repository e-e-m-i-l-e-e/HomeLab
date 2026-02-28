package home.lab.atlassian;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;

public class License {

    private static final String algorithm = "DSA";

    public static final String publicKeyBase64;
    public static final String privateKeyBase64;

    public static Supplier<Map<String, String>> customizer = HashMap::new;

    static {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);
            keyGen.initialize(1024, new SecureRandom());

            KeyPair pair = keyGen.generateKeyPair();
            PublicKey publicKey = pair.getPublic();
            PrivateKey privateKey = pair.getPrivate();

            publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateLicense() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        final Map<String, String> data = new HashMap<>();
        try {
            StringBuilder sb = new StringBuilder();
            data.put("keyVersion", "1600708331");
            data.put("Organisation", "Home Lab");
            data.put("CreationDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            data.putAll(customizer.get());

            for (String key : new TreeSet<>(data.keySet())) {
                String val = data.get(key);
                if (val == null) continue;
                sb.append(escape(key, true)).append("=").append(escape(val, false)).append("\n");
            }
            data.put("licenseHash", Encoder.encode(
                    Encoder.sign(sb.toString().getBytes(StandardCharsets.UTF_8)))
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        StringBuilder sb = new StringBuilder("#");
        sb.append(new Date());
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (entry.getValue() == null) continue;
            sb.append("\n");
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
        }
        String license = sb.toString();
        return Encoder.encode(license);
    }

    private static String escape(String str, boolean isKey) {
        int len = str.length();
        StringBuilder sb = new StringBuilder(len * 2);
        block8: for (int index = 0; index < len; ++index) {
            char c = str.charAt(index);
            switch (c) {
                case '\t': {
                    sb.append("\\t");
                    continue block8;
                }
                case '\n': {
                    sb.append("\\n");
                    continue block8;
                }
                case '\f': {
                    sb.append("\\f");
                    continue block8;
                }
                case '\r': {
                    sb.append("\\r");
                    continue block8;
                }
                case ' ': {
                    if (index == 0 || isKey) {
                        sb.append('\\');
                    }
                    sb.append(' ');
                    continue block8;
                }
                case '\\': {
                    sb.append("\\\\");
                    continue block8;
                }
                default: {
                    if ("=: \t\r\n\f#!".indexOf(c) != -1) {
                        sb.append('\\');
                    }
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    private static class Encoder {

        private static final PrivateKey privateKey;

        static {
            try {
                KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyBase64));
                privateKey = keyFactory.generatePrivate(privateKeySpec);
            }
            catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        }

        private static String encode(byte[] data) {
            return java.util.Base64.getEncoder().encodeToString(data);
        }

        private static String encode(String licenseText) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
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

        private static byte[] sign(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
            Signature signature = Signature.getInstance("SHA1withDSA");
            signature.initSign(privateKey);
            signature.update(bytes);
            return signature.sign();
        }

        private static byte[] zipText(byte[] licenseText) throws IOException {
            byte[] buff = new byte[64];
            ByteArrayInputStream in = new ByteArrayInputStream(licenseText);
            try (in;
                 ByteArrayOutputStream out = new ByteArrayOutputStream();
                 DeflaterInputStream deflater = new DeflaterInputStream(in, new Deflater())
            ) {
                int len;
                while ((len = deflater.read(buff)) > 0) {
                    out.write(buff, 0, len);
                }
                return out.toByteArray();
            }
        }

        private static String packLicense(byte[] text, byte[] hash) throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream dOut = new DataOutputStream(out);
            dOut.writeInt(text.length);
            dOut.write(text);
            dOut.write(hash);
            String result = encode(out.toByteArray()).trim();
            return Encoder.split(result + "X02" + Integer.toString(result.length(), 31));
        }

        private static String split(String licenseData) {
            if (licenseData == null || licenseData.isEmpty()) {
                return licenseData;
            }
            char[] chars = licenseData.toCharArray();
            StringBuilder buf = new StringBuilder(chars.length + chars.length / 76);
            for (int i = 0; i < chars.length; ++i) {
                buf.append(chars[i]);
                if (i == 0 || i % 76 != 0) continue;
                buf.append('\n');
            }
            return buf.toString();
        }
    }
}
