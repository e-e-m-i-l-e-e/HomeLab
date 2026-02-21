FROM releases-docker.jfrog.io/jfrog/artifactory-pro:7.133.10

COPY agents /tmp/agents
COPY system.yaml ${JF_PRODUCT_HOME}/var/etc/system.yaml

ENV JAVA_HOME=${JF_PRODUCT_HOME}/app/third-party/java
ENV PATH="$JAVA_HOME/bin:$PATH"

# Build and install agents
USER root
WORKDIR /tmp
RUN cd agents && \
    ./gradlew shadowJar && \
    mkdir ${JF_PRODUCT_HOME}/patch && \
    cp build/output/* ${JF_PRODUCT_HOME}/patch && \
        echo 'CATALINA_OPTS="$CATALINA_OPTS -javaagent:$JF_PRODUCT_HOME/patch/access-agent.jar"' >> ${JF_PRODUCT_HOME}/app/access/tomcat/bin/setenv.sh && \
    echo 'CATALINA_OPTS="$CATALINA_OPTS -javaagent:$JF_PRODUCT_HOME/patch/artifactory-agent.jar"' >> ${JF_PRODUCT_HOME}/app/artifactory/tomcat/bin/setenv.sh && \
    rm -rf /tmp