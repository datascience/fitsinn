#!/bin/bash
source /home/artur/rnd/git/fitsinn/.venv/bin/activate
./mvnw clean install -DskipTests

./mvnw spring-boot:run -f main/pom.xml &

proc_id=$!
echo proc_id is $proc_id

sleep 10

python fileupload.py http://localhost:8080/multipleupload ~/rnd/data/subset_govdocs/ 300 10


#time curl -X 'POST' 'http://localhost:8080/propertyvalues?property=FORMAT'  -H 'accept: */*' -d ''
ab -n 50 -c 5 -p auto.post -T 'text/plain' 'http://localhost:8080/propertyvalues?property=FORMAT'

echo killing proc_id
kill $proc_id