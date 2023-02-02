#!/bin/bash
case "$1" in
    "")
        echo "please specify the job id"
        echo "Usage: compute.sh <job_id>"
        exit 1;;
    [0-9a-z]*)
        echo "Querying data for job_id: $1";;
    *)
        echo "Invalid job_id"
        exit 1;;
esac
data=`curl http://localhost:8081/jobs/$1/vertices/c27dcf7b54ef6bfd6cff02ca8870b681/metrics?get=0.GlobalWindowAggregate[5].numLateRecordsDropped,0.Calc[6].numRecordsIn`
dropped=$(echo "$data" | jq -c ".[0].value | tonumber")
records=$(echo "$data" | jq -c ".[1].value | tonumber")
perc=$((100*dropped/records))
echo "---------------STATS---------------"
echo "     numRecordsIn: $records"
echo "numRecordsDropped: $dropped"
echo "      percDropped: $perc%"
echo "-----------------------------------"