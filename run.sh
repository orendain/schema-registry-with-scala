#!/usr/bin/env bash

projectDir="$(cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd)"
cd $projectDir

# Install SBT if not already installed
echo "Checking for SBT, installing if missing"
curl https://bintray.com/sbt/rpm/rpm | sudo tee /etc/yum.repos.d/bintray-sbt-rpm.repo
sudo yum -y install sbt

# Run the application (main located in src/main/scala/com/orendainx/trucking/schemaregistry/SchemaRegistrar.scala).
# SBT will handle auto-downloading all dependencies and compile the code before execution
sbt run
