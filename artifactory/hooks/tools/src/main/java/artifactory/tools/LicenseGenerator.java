package artifactory.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jfrog.license.api.Product;
import org.jfrog.license.multiplatform.SignedLicense;
import org.jfrog.security.util.BCProviderFactory;
import org.jfrog.license.multiplatform.License;
import org.jfrog.license.multiplatform.SignedProduct;

public class LicenseGenerator {

    public static final Logger logger = Logger.getLogger(LicenseGenerator.class.getSimpleName());

    public static void main(String[] args) {
        if (args.length != 1) {
            logger.warning("License key won't be generated. Provide path of future license file as an argument.");
            return;
        }
        try {
            final String PRIVATE_KEY = "MIIJQQIBADANBgkqhkiG9w0BAQEFAASCCSswggknAgEAAoICAQC+jycur7/rbqjO9Q+SG1N1kERtqd6gP18h/+7UXV537A7G8F9oiGYwdkJVSh288XwI1wsqTBLlCbla7SxitCsmTq7NjdAbSRI4zNtNj8VjzuksBmCmX4qP1TBYh8UNRMAzDs0UCbKqmx49ztf48MROIO51N7vifbvQFmGXwBmAbHaFNhPXacT9JjB9XemUzrcoTofRfn4ziLmsAx0D7QH+ICtMGiojdc32kpmg4Ag4Q79UTF1tOADixtlO+MV19zRIsXiUhj9mNcOQKFASWwfGcJ07VM50y5XXWyhQknKmOMS+e8SetpdyVMAdTeGeH6ReA6iULFb5d9ci50J5jjmnlpJ/15aN2cNRcgmhYlfWL0W21AWXcZDdFss1FzqMK6ar2WcnFHLZlpOJsE5ssLOqFLEsgyh4i1OizLbDbX6yJ1NF9xiIOZRHhmt0qMLtgObpPnPnrY7nvD8rLqPXApdH944eE9EdE8c1hFveTJUa4WKpJxpYu8SsQ8lrn9OFPBcWmhQReCkV0sG0BQHMF5D1N5aqd18/6eUeERqqQDjNvgqzp6mQsCDGVTSVOXEyA/lpBSrwZW2YAbAJXo0FzLo88j3wFOCayBwIRdM6TEdv+fbnGK/i6mvaMpLOBj+C7sGIo1csM2ohCZI3Nr+xNgzB/5UbyOE44Hku/IdgopXY9QIDAQABAoICAAvnQ1C33iqY7TPB74J2V7gWfL/HU0aTtTlmR4ckFSkzXLG+no1+eVITp3PoC+pOjcUh/oRaeV+tqmw5a0/Es2FSm7nvxUBYrl1P828DFeOn/COQFNKvt/PhVejINyq4aoBSyPeBRGDcQBjAsysAvZja/V18tfoHvDpBlUBDQEfBmFG2UNz6GKA7ld6iH1vFSEzc3I0Qje+U93KWqzK6LlxI3b631hc3xHFGAES7wEFLfuGHXg3bG4Soj51Op7I4ilKNvqhNQbT8kyyN6BqNbY0blPOTPTMWHu3Z6uKSYCKgyMfUhJhP2mJkIUmmhy5qb/w51eD6BODTRJdDw012ire6XIFCpSZUVibBYwjhN1X5fljKcdGdDwwb3TjoZgoGCQhA+IrVf+9LvvobWmUxKsCx5kx85LTnsWZqevoFPbMN3t9oEpyebgJIODjIHuBKC0CDf2w+Y8+lekJTjXsyIvChEeGWFWyrY9AILi9VyJCWDWS2mYZkJZJiBkFBdjDybfVAg0U5aEVqhqiY+79bnVoWNNd73lQTGHwMxJbFXKujLWb/N9lL8E9Zm8Y19yQA2bQx/R41JHwDwd6yTeYJa/vWnN00MzSd+HWYBKThXsgVucRCkM+HW3/Ao8TJRix95TxLSlgAuaAaNIVtXwH/UY0AXXRbjwcGXnxC/a1P/SctAoIBAQD/yL9bqtcu/rUKqGgy4ZVhwwzrB+LayTzu8v11/YzU2u7ZggqTiK4L44RWMqCWseYCJ3aeVjAhbBENh33AKXaWY64AXpQmcSxO5iecg93wGGxgpA80XHr9d7HcacDWoFZSgacOrw7lQJU3G7pkb3Tx12b1nruSLA+BoLtp+YBlU2X54bYgunQ38gt6B4cOzK2HLvWxhC/aN8f3u7SntmoDD3ZANz/So68p7RKzIiFSvwwK4F7/U9KUPLC+vTmxVgKNhakLspuarFhC5icbVDgDPGYWuZpUwxtKEk8QmoRRY2u+k4n6qCnsgriKbI2VO8drSM+hevAz3uGgcO3eDxPLAoIBAQC+uFDwlTGT418pjBRpl5i8VMI/rKu3hMEUMDdg3phnc4kL5tMPvk8uS8BRvDY8Z2ENr4tL474KFYMfFi1pttEPki0VtFHVw9K55ZCpqi4jFr3hJ7ixbHqMDs/d8GAPcNlbJvApjUp5yUK25MvmJvnOAJLf4vuFbDr7LLkVNMeUaOLV98qyTMyT9x8xBS3fybBpEj36KHU2zPgezxX9v6XDS79I+jG2NGIm5OsPP3imELaFHwt5Hh0cCf+tmCvNBex23FVTuBhK30/uEzOoYqZnZfjQUYbR7WNjTD3CZPzCZ5GPl1EJSvy28ROShW4/dGvu1KaKBBUIwgJTv+ICxK4/AoIBAH4dQ8R1un1QVoE3wZB8y0OVgTQNAlwvZpzGMBRR/HMisyuJQ5+0f2QbPK+nbKiAdFDRllnBUx/XgZzzFhx+FRtXcH58I33ExTztm9A+8FmmISGRInIFuxpoPsjXV547FfS+OlkyFR0Rt8ChKkjE75siQoCOUBtlIAwg7Ob2fsj20sve7wa6B/1hmSkEtyGj51dE3x00eMeWQ0ExSoR6m1CA+iP1WcGItR/t5Q9vn5CF4/Ek6ZHoiWZPejHBaMW4C7Atm8jJupELssThZ5zA7JEj4Y4I20A5pqXh2xvro5vhcklXw2tUKL+VQFn35eMTw2JxVvZegDqDBSovJAkd0/sCggEAYnawOvUbqnlpyP5sD3PXw+uSeeNAqDeeozidbqKdVlOcvotGDku3f3RWbxxXd+n7fmBR7zU+CODAWE1P1tMurfZO1nTJBy7ZXWY6wI1+0ljMYTDrtpYF0sdW5ANU5MPjQ58fwKQUfjm8+sW2yzT2JTfAnFwIdQLClKirSVp9a46XBdavTExAFmgwL1O4MmBjdmuXmc5Hbap2sxlBBkdqYiB6OkgJYEM9JU1sWevv8vSP9rxnZPl6F97FpK76dhgH0/m/B8D0sbsG/ifltJpxxNItmgdtLnZ7qGlVPUZ8BDYKPXLmwIB5Zk2iSDQhDhuiGaSp8nlXDrVzFB/tpHY8awKCAQAVGcdBj+2eN3Xq1yHuC50BAmg1VCXM6HdiGtjhKLt1c6UafKwpmseJjNqoHqmqpabJPB+xGnDxqhc4MXpSQYaOM8DvRkK8QQn7lQ7mMvH6//pDknVWpWeAfzQj9yVob28oxAUty4KB6fX5dKzLxgkJKZVDPTclqF3iV0oUNZkeZCa4P/UQy0D6lhFnH/6eW7rZa460fGGCSTp2ox0mQm7GTfjGqVP2Dhb5kOZu/4OlwkaDgEsIFYJ7DZvLu22mDGIL+4x3D9Q5xk4Zu3Umkx0JSKDUxurjI4fmS/eQu7YNFmEM0Dbw4LCnjok+ONlhwDsskgxUJr669IOfs4gZN7+z";
            PrivateKey privateKey = KeyFactory.getInstance("RSA", BCProviderFactory.getProvider())
                    .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(PRIVATE_KEY)));
            Signature signature = Signature.getInstance("SHA256withRSA", BCProviderFactory.getProvider());

            Product product = new Product();
            product.setId("artifactory");
            product.setExpires(Date.from(Instant.now().plus(100 * 365, ChronoUnit.DAYS)));
            product.setTrial(false);
            product.setOwner("admin");
            product.setValidFrom(Date.from(Instant.now()));
            product.setType(Product.Type.ENTERPRISE_PLUS);

            Map<String, SignedProduct> products = Map.of(product.getId(), new SignedProduct(product, privateKey, signature));

            License license = new License();
            license.setVersion(2);
            license.setValidateOnline(false);
            license.setProducts(products);

            SignedLicense signedLicense = new SignedLicense(license, privateKey, signature);

            Path licensePath = Path.of(args[0]);
            Files.writeString(licensePath, chunkString(createFinalLicense(signedLicense)), StandardCharsets.UTF_8);
            logger.info("License has been generated and saved to: " + licensePath.toAbsolutePath());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Fail to generate license key. " + e.getClass().getSimpleName() + " : " + e.getMessage());
        }
    }

    public static String createFinalLicense(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            JsonFactory factory = new JsonFactory();
            JsonGenerator generator = factory.createGenerator(bos, JsonEncoding.UTF8);

            ObjectMapper mapper = new ObjectMapper(factory);

            generator.setCodec(mapper);
            generator.writeObject(obj);
            generator.close();

        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize license", e);
        }

        return Base64.getEncoder().encodeToString(bos.toByteArray());
    }

    public static String chunkString(String str, int chunkLength) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < str.length(); i += chunkLength) {
            int end = Math.min(str.length(), i + chunkLength);
            result.append(str, i, end);
            if (end < str.length())
                result.append('\n');
        }

        return result.toString();
    }

    public static String chunkString(String str) {
        return chunkString(str, 76);
    }
}
