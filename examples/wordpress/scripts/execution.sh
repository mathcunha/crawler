#!/bin/sh
# script executed to generate the load
sed -i 's;"workload",.*"sc;"workload", '$3', "sc;g' ~/logstash_metrics/metric_colector_application.conf
sed -i 's;"scenario", ".*"];"scenario", "'$2'"];g' ~/logstash_metrics/metric_colector_application.conf

sudo service logstash-metric-application stop
sleep 10
sudo service logstash-metric-application start
sleep 20

sed -i 's/baseURL.*"/baseURL("http:\/\/'$1'\/"/g' ~/gatling-charts-highcharts-1.5.3/user-files/simulations/crawler/SimplePost.scala
sed -i 's/users([0-9]*/users('$3'/g' ~/gatling-charts-highcharts-1.5.3/user-files/simulations/crawler/SimplePost.scala


~/gatling-charts-highcharts-1.5.3/bin/gatling.sh -s crawler.SimplePost
file="/home/ubuntu/gatling-charts-highcharts-1.5.3/results/$(ls -1t /home/ubuntu/gatling-charts-highcharts-1.5.3/results | head -1)/simulation.log"
echo $file
echo "" > ~/logstash_metrics/simulation.log
sleep 10
cp $file ~/logstash_metrics/simulation.log

exit 0