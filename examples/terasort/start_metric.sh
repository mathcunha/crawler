export ELASTIC_PORT=http://$1:9200 
source ~/.profile
nohup go run /home/ubuntu/go/src/github.com/mathcunha/go-probe/examples/hadoop.go -jvmmetrics /usr/local/hadoop-metrics/nodemanager-jvm-metrics.out > &  /dev/null
exit 0