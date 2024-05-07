#!/bin/bash
source ${PWD}/../.venv/bin/activate
./mvnw -pl -web -DskipTests clean install

./mvnw spring-boot:run -f main/pom.xml &

proc_id=$!
echo proc_id is $proc_id

sleep 10

python fileupload.py http://localhost:8080/multipleupload ~/rnd/data/subset_govdocs/ 100 2


#time curl -X 'POST' 'http://localhost:8080/propertyvalues?property=FORMAT'  -H 'accept: */*' -d ''
ab -n 50 -c 1 -p auto.post -T 'text/plain' 'http://localhost:8080/propertyvalues?property=FORMAT'

echo killing proc_id
kill $proc_id