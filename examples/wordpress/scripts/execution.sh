#!/bin/sh
# script executed to generate the load

sed -i 's/baseURL.*"/baseURL(http:\/\/'$1'\//g' ~/gatling-charts-highcharts-1.5.3/user-files/simulations/crawler/SimplePost.scala
sed -i 's/users([0-9]*/users('$2'/g' ~/gatling-charts-highcharts-1.5.3/user-files/simulations/crawler/SimplePost.scala
~/gatling-charts-highcharts-1.5.3/bin/gatling.sh -s crawler.SimplePost.scala

