package home.lab.jfrog;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

public interface Transformer {

    String getType();

    ElementMatcher<MethodDescription> getMatcher();
}
