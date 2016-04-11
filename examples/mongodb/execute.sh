./bin/ycsb load mongodb -s -P workloads/workloada -p recordcount=10000000 -threads 4 >> insert.log
./bin/ycsb run mongodb -s -P workloads/workloada -p operationcount=10000000 -threads 4 >> workload.log
