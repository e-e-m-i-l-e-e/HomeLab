FROM jetbrains/teamcity-server:2025.11.3

COPY agents /tmp/agents

USER root
WORKDIR /tmp
RUN cd agents && \
    ./gradlew shadowJar && \
    mkdir ${TEAMCITY_DIST}/patch && \
    cp build/output/teamcity-agent.jar ${TEAMCITY_DIST}/patch/agent.jar && \
    echo 'export CATALINA_OPTS="-javaagent:${TEAMCITY_DIST}/patch/agent.jar ${CATALINA_OPTS}"' >> ${TEAMCITY_DIST}/bin/setenv.sh && \
    rm -rf /tmp/agents