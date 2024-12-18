#!/bin/bash

COUNTER=0

PARALLEL_TASKS=30

for i in $(find $2 -type f -print)
do
    (( COUNTER++ ))
    (( t=t%PARALLEL_TASKS )); ((t++==0)) && wait
    curl --silent --output /dev/null --show-error -X "POST" \
      "${1}/upload?datasetName=${3}" \
      -H "accept: */*" \
      -H "Content-Type: multipart/form-data" \
      -F "file=@${i}" &

    if ! (( $COUNTER % $PARALLEL_TASKS )) ; then
          printf "%d files uploaded\n" $COUNTER
    fi

done
wait
