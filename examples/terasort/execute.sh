#!/bin/bash
folder=$(date +%s%3N)
in="input_$folder"
out="output_$folder"
/usr/local/hadoop/bin/hadoop jar /usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.6.0.jar teragen $2 $in
before=$(date +%s%3N)
/usr/local/hadoop/bin/hadoop jar /usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.6.0.jar terasort $in $out
after=$(date +%s%3N)
echo "scenario:$1, workload:$2, timestamp:$before, ini:$before, fim:$after"
/usr/local/hadoop/bin/hdfs dfs -rm -r $in
/usr/local/hadoop/bin/hdfs dfs -rm -r $out
exit 0