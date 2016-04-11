#!/bin/bash
folder=$(date +%s%3N)
ins="insert_$1_$2_$folder.log"
wkl="wkl_$1_$2_$folder.log"
~/ycsb-0.7.0/bin/ycsb load mongodb -s -P ~/ycsb-0.7.0/workloads/workloada -p recordcount=$2 -threads 4 >> $ins
~/ycsb-0.7.0/bin/ycsb run mongodb -s -P ~/ycsb-0.7.0/workloads/workloada -p operationcount=$2 -threads 4 >> $wkl
exit 0
