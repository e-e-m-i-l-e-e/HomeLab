FROM atlassian/confluence:10.2.6

COPY agents /tmp/agents

WORKDIR /tmp
RUN cd agents && \
    ./gradlew build && \
    mkdir ${CONFLUENCE_INSTALL_DIR}/patch && \
    cp build/output/confluence.jar ${CONFLUENCE_INSTALL_DIR}/patch/agent.jar && \
    echo 'export CATALINA_OPTS="-javaagent:${CONFLUENCE_INSTALL_DIR}/patch/agent.jar ${CATALINA_OPTS}"' >> ${CONFLUENCE_INSTALL_DIR}/bin/setenv.sh && \
    rm -rf /tmp/agents && \
#   Install trusted store certificates \
    cd ${CONFLUENCE_HOME} && \
    mkdir upmconfig && \
    cd upmconfig && \
    wget https://confluence.atlassian.com/upm/files/1489470540/1489470539/1/1736436578282/atlassian_ca_bundle-v1.zip && \
    wget https://confluence.atlassian.com/upm/files/1489470540/1489470539/1/1736436578282/atlassian_ca_bundle-v2.zip && \
    mkdir truststore && \
    apt update && \
    apt install unzip && \
    unzip -o atlassian_ca_bundle-v1.zip -d truststore && \
    unzip -o atlassian_ca_bundle-v2.zip -d truststore