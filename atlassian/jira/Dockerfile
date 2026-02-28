FROM atlassian/jira-software:11.3.2

ENV JIRA_SETUP_LICENSE=true

COPY agents /tmp/agents

WORKDIR /tmp
RUN cd agents && \
    ./gradlew build && \
    mkdir ${JIRA_INSTALL_DIR}/patch && \
    cp build/output/jira.jar ${JIRA_INSTALL_DIR}/patch/agent.jar && \
    echo 'export CATALINA_OPTS="-javaagent:${JIRA_INSTALL_DIR}/patch/agent.jar ${CATALINA_OPTS}"' >> ${JIRA_INSTALL_DIR}/bin/setenv.sh && \
    rm -rf /tmp/hooks && \
#   Install trusted store certificates \
    cd ${JIRA_HOME} && \
    mkdir upmconfig && \
    cd upmconfig && \
    wget https://confluence.atlassian.com/upm/files/1489470540/1489470539/1/1736436578282/atlassian_ca_bundle-v1.zip && \
    wget https://confluence.atlassian.com/upm/files/1489470540/1489470539/1/1736436578282/atlassian_ca_bundle-v2.zip && \
    mkdir truststore && \
    apt update && \
    apt install unzip && \
    unzip -o atlassian_ca_bundle-v1.zip -d truststore && \
    unzip -o atlassian_ca_bundle-v2.zip -d truststore