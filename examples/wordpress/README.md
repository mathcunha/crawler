example - wordpress
=======

Sample benchmark of a wordpress instalation


## Deployment

Use chef role named wordpress-role

./chef/role

## Load

The load generator is the tool Gatling. There are two classes, one to set the wordpress admin user and other to generate the load

./gatling/

## Metric Collector

Here we used logstash to collect the metrics, there are three files and their names describe their functions

./logstash

## Script

work in progress
