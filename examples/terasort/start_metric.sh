#!/bin/nash
source ~/.profile
export ELASTIC_PORT=http://$1:9200
nohup go run /home/ubuntu/go/src/github.com/mathcunha/go-probe/examples/hadoop.go -workload $3 -scenario $2 -jvmmetrics /usr/local/hadoop-metrics/nodemanager-jvm-metrics.out </dev/null &>/dev/null &
exit 0
