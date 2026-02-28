package atlassian;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.*;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Map;
import java.util.Date;
import java.util.Base64;
import java.util.HashMap;
import java.util.TreeSet;

import java.time.Instant;
import java.text.SimpleDateFormat;
import java.nio.charset.StandardCharsets;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;

public class LicenseManager {

    public static final String publicKeyBase64;
    public static final String privateKeyBase64;

    static {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
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
        return Encoder.encode(new License().toString());
    }

    private static class License {
        private final Date date = new Date();
        private final Map<String, String> data = new HashMap<>();
//        @Override
//        public String getProductName() {
//            return "jira.product.jira-software";
//        }
//
//        @Override
//        public void setActive(boolean active) {
//            super.setActive(active);
//            this.data.put("greenhopper.active", String.valueOf(active));
//            this.data.put("jira.active", String.valueOf(active));
//        }
//
//        @Override
//        public void setNumberOfUsers(int numberOfUsers) {
//            super.setNumberOfUsers(numberOfUsers);
//            this.data.put("jira.NumberOfUsers", String.valueOf(numberOfUsers));
//            this.data.put("NumberOfUsers", String.valueOf(numberOfUsers));
//        }
//
//        @Override
//        public void setLicenseType(LicenseType licenseType) {
//            this.data.put("LicenseTypeName", licenseType.toString());
//            this.data.put("greenhopper.LicenseTypeName", licenseType.toString());
//            this.data.put("jira.LicenseTypeName", licenseType.toString());
//        }
//
//        @Override
//        public void setDataCenter(boolean dataCenter) {
//            super.setDataCenter(dataCenter);
//            if (dataCenter) {
//                this.data.put("jira.DataCenter", "true");
//            } else {
//                this.data.remove("jira.DataCenter");
//            }
//        }
//
//        public void setLicenseEdition(LicenseEdition licenseEdition) {
//            this.data.put("greenhopper.LicenseEdition", licenseEdition.toString());
//            this.data.put("jira.LicenseEdition", licenseEdition.toString());
//        }
//
//        public void setEnterprise(boolean enterprise) {
//            this.data.put("greenhopper.enterprise", String.valueOf(enterprise));

//        public void init() {
//            Date expiryDate = new Date(3771590399000L);
//            String licenseId = "L" + System.currentTimeMillis();
//            this.setActive(true);
//            this.setPurchaseDate(this.date);
//            this.setLicenseExpiryDate(expiryDate);
//            this.setMaintenanceExpiryDate(expiryDate);
//            this.setNumberOfUsers(-1);
//            this.setStarter(false);
//            this.setSEN("SEN-" + licenseId);
//            this.setLicenseID("LIDSEN-" + licenseId);
//            this.setCreationDate(this.date);
//            this.setLicenseType(LicenseType.COMMERCIAL);
//            this.setDescription("Unlimited license by https://zhile.io");
//            this.setEvaluation(false);
//            this.setContactName(this.contactName);
//            this.setContactEMail(this.contactEMail);
//            this.setServerID(this.serverID);
//            this.setOrganisation(this.organisation);
//            this.setDataCenter(this.dataCenter);
//            this.setLicenseVersion("2");
//            this.setKeyVersion("1600708331");
//        }

        public String getProductName() {
    return "jira.product.jira-software";
}
        protected String productProperty(String property) {
            return this.getProductName() + "." + property;
        }


        private void setLicenseHash() throws Exception {
            this.data.put("greenhopper.active", "true");
            this.data.put("jira.active", "true");
            this.data.put("jira.NumberOfUsers", "-1");
            this.data.put("jira.LicenseTypeName", "COMMERCIAL");
            StringBuilder sb = new StringBuilder();
            this.data.put("keyVersion", "1600708331");
            this.data.put("Organisation", "HomeLab");
            this.data.put("ContactEMail", "test@gmail.com");
            this.data.put("ContactName", "test");
            this.data.put("Description", "test");
            this.data.put("jira.DataCenter", "true");
            this.data.put(this.productProperty("active"), "true");
            this.data.put("CreationDate", new SimpleDateFormat("yyyy-MM-dd").format(Date.from(Instant.now())));
            this.data.put("PurchaseDate", new SimpleDateFormat("yyyy-MM-dd").format(Date.from(Instant.now())));
            this.data.put("LicenseExpiryDate", new SimpleDateFormat("yyyy-MM-dd").format(Date.from(Instant.now().plusSeconds(1000000))));
            this.data.put(this.productProperty("NumberOfUsers"), String.valueOf(-1));
            this.data.put("SEN", "SEN-" + "L" + System.currentTimeMillis());
            this.data.put(this.productProperty("LicenseTypeName"), "COMMERCIAL");
            this.data.put(this.productProperty("Starter"), "false");
            data.put("Product", "JIRA");
            data.put("SupportEntitlementNumber", "SEN-12345678");
            data.put("ServerID", "Default");
            data.put("NumberOfUsers", "-1");   // unlimited
            data.put("Edition", "ENTERPRISE");
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            data.put("CreationDate", today);
            data.put("StartDate", today);
            data.put("PurchaseDate", today);
            data.put("MaintenanceExpiryDate", "2099-12-31");
            data.put("DataCenter", "true");
            data.put("Evaluation", "false");
            data.put("LicenseID", "LID-" + System.currentTimeMillis());
            this.data.put(this.productProperty("DataCenter"), "true");
            this.data.put("Subscription", "true");

            for (String key : new TreeSet<>(this.data.keySet())) {
                String val = this.data.get(key);
                if (val == null) continue;
                sb.append(this.escape(key, true)).append("=").append(this.escape(val, false)).append("\n");
            }
            this.data.put("licenseHash", Encoder.encode(
                    Encoder.sign(sb.toString().getBytes(StandardCharsets.UTF_8)))
            );
        }

        private String escape(String str, boolean isKey) {
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

        public final String toString() {
            StringBuilder sb = new StringBuilder("#");
            sb.append(this.date);
            try {
                this.setLicenseHash();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            for (Map.Entry<String, String> entry : this.data.entrySet()) {
                if (entry.getValue() == null) continue;
                sb.append("\n");
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(entry.getValue());
            }
            return sb.toString();
        }
    }

    private static class Encoder {
        private static final String PRIVATE_KEY_STR = LicenseManager.privateKeyBase64;
        private static final PrivateKey PRIVATE_KEY;

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
            signature.initSign(PRIVATE_KEY);
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

        static {
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("DSA");
                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(Encoder.PRIVATE_KEY_STR));
                PRIVATE_KEY = keyFactory.generatePrivate(privateKeySpec);
            }
            catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
