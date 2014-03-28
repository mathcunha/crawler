#!/bin/sh
mvn compile install
cd CloudCrawlerView/
mvn package
cd ../
cd CloudCrawler/
mvn exec:java -Dexec.mainClass="br.mia.unifor.crawlerenvironment.Main" -Djava.util.logging.config.file=./target/classes/logging.properties > /dev/null 2>&1 &
