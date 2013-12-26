cd ~/SPECjvm2008/
java -jar ./SPECjvm2008.jar -wt 5s -it 5s -bt 4 -ikv $1 >> /tmp/$2
value=$(grep 'Noncompliant composite result' /tmp/$2 | sed -re '/ops/s/^[^0-9]*|[^0-9]*$//g')

sed -re s/WORKLOAD/"$1"/g -e s/VALUE/"$value"/g -e s/TIMESTAMP/"$2"/g ~/result.yaml

exit 0

