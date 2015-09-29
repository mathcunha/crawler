#!/bin/bash
fSlave="/usr/local/hadoop/etc/hadoop/slaves"
fHosts="/etc/hosts"

sed -i '/^.*adoo.*$/d' $fSlave
sed -i '/^.*adoopsl.*$/d' $fHosts
i=1
for slave in "$@"
do
        echo "hadoopslave$i" >> $fSlave
	echo "$slave  hadoopslave$i" >> $fHosts
        i=$((i+1))
done
exit 0
