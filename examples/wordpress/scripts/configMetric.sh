#!/bin/sh
sed -i 's;"workload",.*"sc;"workload", '$2', "sc;g' ~/logstash_metrics/indexer.conf
sed -i 's;"scenario", ".*"];"scenario", "'$1'"];g' ~/logstash_metrics/indexer.conf

sudo service logstash-indexer start