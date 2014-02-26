#!/bin/sh
# script executed to generate the load

sed -i 's/baseURL.*"/baseURL("http:\/\/'$1'\/"/g' ~/gatling-charts-highcharts-1.5.3/user-files/simulations/crawler/SimplePost.scala
sed -i 's/users([0-9]*/users('$2'/g' ~/gatling-charts-highcharts-1.5.3/user-files/simulations/crawler/SimplePost.scala
~/gatling-charts-highcharts-1.5.3/bin/gatling.sh -s crawler.SimplePost
file="/home/ubuntu/gatling-charts-highcharts-1.5.3/results/$(ls -1t /home/ubuntu/gatling-charts-highcharts-1.5.3/results | head -1)/simulation.log"
echo $file
echo "" > ~/logstash_metrics/simulation.log
sleep 10
cp $file ~/logstash_metrics/simulation.log

exit 0