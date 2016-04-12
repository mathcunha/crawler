#!/bin/bash
folder=$(date +%s%3N)
ins="/data/insert_$1_$2_$folder.log"
wkl="/data/wkl_$1_$2_$folder.log"
~/ycsb-0.7.0/bin/ycsb load mongodb -s -P ~/ycsb-0.7.0/workloads/workloada -p recordcount=$2 -threads 16 >> $ins
before=$(date +%s%3N)
~/ycsb-0.7.0/bin/ycsb run mongodb -s -P ~/ycsb-0.7.0/workloads/workloada -p operationcount=$2 -threads 60 >> $wkl
after=$(date +%s%3N)
result=$(mongo ycsb --eval "db.usertable.count()")
diff=`expr $after - $before`
echo "$result, $1, $2, $before, $after, $diff, RESULT_CRAWLER"
exit 0
