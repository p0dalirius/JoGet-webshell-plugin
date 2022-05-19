#!/usr/bin/env bash

log()  { echo -e "\x1b[1m[\x1b[93mLOG\x1b[0m\x1b[1m]\x1b[0m ${@}";  }
info() { echo -e "\x1b[1m[\x1b[92mINFO\x1b[0m\x1b[1m]\x1b[0m ${@}"; }
warn() { echo -e "\x1b[1m[\x1b[91mWARN\x1b[0m\x1b[1m]\x1b[0m ${@}"; }

VERSION="1.3.0"
PROJECTNAME="webshell"

#=======================================================================================================================

info "Building for Apache Tomcat 9.x and lower"

# Prepare configuration
log "Setting version = '${VERSION}' in pom.xml ..."
sed -i "s!^    <version>.*</version>!    <version>${VERSION}</version>!g" /plugin/pom.xml

log "Setting version = '${VERSION}' in src/main/java/podalirius/WebShell.java ..."
sed -i "s!public String getVersion() { return \".*\"; }!public String getVersion() { return \"${VERSION}\"; }!g" /plugin/src/main/java/podalirius/WebShell.java

log "Starting build ..."

cd /plugin/
mvn clean install -Dmaven.test.skip=true

# Cleanup
log "Cleanup build environnement ..."
if [[ -d /plugin/target/classes/ ]]; then
  rm -rf /plugin/target/classes/;
fi