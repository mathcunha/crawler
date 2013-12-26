#self-generated script
echo "running execution to workload " $1
~/executeSpec.sh $1 $(date +%s%N | cut -b1-13) > ~/resultMetricEval.yaml
