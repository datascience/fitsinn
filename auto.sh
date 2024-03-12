#!/bin/bash
source /home/artur/rnd/git/fitsinn/.venv/bin/activate
./mvnw clean install -DskipTests

./mvnw spring-boot:run -f main/pom.xml &

proc_id=$!
echo proc_id is $proc_id

sleep 10

python fileupload.py http://localhost:8080/multipleupload ~/rnd/data/subset_govdocs/ 1000 5


time curl -X 'POST' 'http://localhost:8080/propertyvalues?property=FORMAT'  -H 'accept: */*' -d ''

echo killing proc_id
kill $proc_id