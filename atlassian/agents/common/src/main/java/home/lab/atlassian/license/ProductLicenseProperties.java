package home.lab.atlassian.license;

import home.lab.atlassian.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.text.SimpleDateFormat;
import java.time.Instant;import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Logger;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class ProductLicenseProperties {
    public static final Logger logger = Logger.getLogger(ProductLicenseProperties.class.getName());

    public static final String type = "com.atlassian.extras.common.util.ProductLicenseProperties";

    public static class getProperty implements Transformer {

        public static ElementMatcher.Junction<MethodDescription> matcher = named(getProperty.class.getSimpleName()).and(takesArguments(1));

        @Advice.OnMethodExit
        public static void onExit(
                @Advice.Argument(value = 0) String key,
                @Advice.Return(readOnly = false) String value) {
            String[] property = key.split("\\.");
            String propertyName = property[property.length - 1];

            switch (propertyName) {
                case "Description",
                     "PartnerName",
                     "Organisation",
                     "ContactEMail",
                     "ContactName",
                     "CreationDate",
                     "GracePeriod",
                     "Evaluation",
                     "SEN",
                     "NumberOfClusterNodes" -> {}
                case "active", "enterprise", "Subscription", "DataCenter" -> value = "true";
                case "LicenseTypeName" -> value = "COMMERCIAL";
                case "LicenseEdition" -> value = "UNLIMITED";
                case "ServerID" -> value = "Default";
                case "LicenseExpiryDate", "MaintenanceExpiryDate" ->
                        value = new SimpleDateFormat("yyyy-MM-dd").format(
                                Date.from(
                                        LocalDate
                                                .now()
                                                .plusYears(100)
                                                .atStartOfDay(ZoneId.systemDefault())
                                                .toInstant()
                                )
                        );
                case "PurchaseDate" ->
                    value = new SimpleDateFormat("yyyy-MM-dd").format(Date.from(Instant.now()));
                case "NumberOfUsers" -> value = String.valueOf(-1);
                default -> logger.warning("Unknown license property: " + key);
            }
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public ElementMatcher<MethodDescription> getMatcher() {
            return matcher;
        }
    }
}
