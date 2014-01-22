crawler
=======

Capacity Planning for Cloud Applications


## Description

<p align="center">
  <img src="https://sites.google.com/site/nabormendonca/_/rsrc/1387726329838/research-projects/cloud-crawler/cloud-crawler-overview.png?height=197&width=400&height=198" alt="Cloud Crawler overview"/>
</p>

This project aims at proposing a new declarative environment, called Cloud Crawler, to support cloud users (i.e., application developers) in automatically evaluating the performance of their cloud applications under varying demand levels and using different virtual machine configurations from multiple IaaS cloud providers. The environment includes a domain-specific language, called Crawl, which allows the description, at a high abstraction level, of a variety of performance evaluation scenarios in the cloud; and an extensible execution engine, called Crawler, which automatically configures the resources, executes and collects the results of the evaluation scenarios described in Crawl.

For more information, please visit this page https://sites.google.com/site/nabormendonca/research-projects/cloud-crawler

## Building

After cloning the project, you will have a folder with the configuration to generate a Vagrant Box (http://www.vagrantup.com/) with all project's dependencies. For this building I choosed Packer (http://www.packer.io/).

Once you have your box ready, it's time to configure your Java environment. So pick up your favorite jdk, set the JAVA_HOME and, inside the root folder of the project, execute the following commands:

mvn package install
cd CloudCrawler; mvn exec:java

## Usage
This new version of the Cloud Crawler exposes a REST Api that is under development. I hope a new version is coming in this quarter
