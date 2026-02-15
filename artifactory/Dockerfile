FROM releases-docker.jfrog.io/jfrog/artifactory-pro:latest

COPY hooks /tmp/hooks
COPY system.yaml ${JF_PRODUCT_HOME}/var/etc/system.yaml

ENV JAVA_HOME=${JF_PRODUCT_HOME}/app/third-party/java
ENV PATH="$JAVA_HOME/bin:$PATH"

# Prepare agent and setup license
USER root
WORKDIR /tmp
RUN cd hooks && \
    ./gradlew build -Plib=${JF_PRODUCT_HOME}/app/artifactory/tomcat/webapps/artifactory/WEB-INF/lib && \
    java -jar build/tools/libs/tools.jar ${JF_PRODUCT_DATA_INTERNAL}/etc/artifactory/artifactory.lic && \
    mkdir ${JF_PRODUCT_HOME}/patch && \
    cp build/agents/libs/agents.jar ${JF_PRODUCT_HOME}/patch/agents.jar && \
    set -eux; \
        f="../setenv.sh"; \
        : > "$f"; \
        printf '%s\n' '#!/bin/sh' >> "$f"; \
        printf '%s\n' 'CATALINA_OPTS="$CATALINA_OPTS -javaagent:$JF_PRODUCT_HOME/patch/agents.jar"' >> "$f"; \
        printf '%s\n' 'CATALINA_OPTS="$CATALINA_OPTS -Djf.product.home=$JF_PRODUCT_HOME"' >> "$f"; \
        chmod +x "$f" && \
    cp ../setenv.sh ${JF_PRODUCT_HOME}/app/access/tomcat/bin/setenv.sh && \
    cp ../setenv.sh ${JF_PRODUCT_HOME}/app/artifactory/tomcat/bin/setenv.sh && \
    rm -rf /tmp