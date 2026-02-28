package home.lab.atlassian.license;

import home.lab.atlassian.License;
import home.lab.atlassian.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.logging.Logger;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class Key implements Transformer {
    public static final Logger logger = Logger.getLogger(Key.class.getName());

    public static final String type = "com.atlassian.extras.keymanager.Key";

    public static ElementMatcher.Junction<MethodDescription> matcher = isConstructor().and(takesArguments(3));

    @Advice.OnMethodEnter
    public static void onEnter(
            @Advice.Argument(value = 0, readOnly = false) String key,
            @Advice.Argument(1) Object version,
            @Advice.Argument(2) Object type
    ) {
        if (type.toString().equals("PUBLIC")) {
            logger.info("Injecting public key " + version);
            key = License.publicKeyBase64;
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
