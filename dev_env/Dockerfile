FROM debian:buster

ENV PLUGIN_VERSION="1.3.0"
ENV jogetDependencyVersion="7.0-SNAPSHOT"

ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get -qq -y update; \
    apt-get -qq -y install wget tar git zip unzip default-jre maven; \
    mkdir -p /joget/; cd /joget/

WORKDIR /root/

RUN wget "https://dev.joget.org/community/download/attachments/26117256/wflow.zip?version=1&modificationDate=1441685239000&api=v2" -O /root/wflow.zip; unzip wflow.zip

WORKDIR /joget/

RUN wget "https://dev.joget.org/community/download/attachments/26117256/install-libraries.zip?version=1&modificationDate=1458793220000&api=v2" -O install-libraries.zip; \
    unzip install-libraries.zip ; \
    rm -rf __MACOSX ; \
    cd install-libraries; \
    chmod +x install_linux.sh ; \
    ./install_linux.sh

RUN git clone https://github.com/jogetworkflow/jw-community.git --branch ${jogetDependencyVersion} ; \
    cd jw-community/wflow-app; \
    mvn clean install -Dmaven.test.skip=true

RUN chmod +x /joget/jw-community/wflow-plugin-archetype/create-plugin.sh ; \
    cd /; printf "${PLUGIN_VERSION}\ny\n" | /joget/jw-community/wflow-plugin-archetype/create-plugin.sh org.joget.webshell plugin ${jogetDependencyVersion}; \
    echo ""

VOLUME /plugin/

CMD ["/bin/bash"]