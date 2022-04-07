#!/bin/bash
ITERS=100000


# start
run(){
    ./generate_data $ITERS
    ./check_deps
    ./services_helper --start
    ./cassandra_helper clean
    ./cassandra_helper prepare
    ./kafka_helper prepare
    ./kafka_helper upload
    mvn package
    java -jar target/LABA_2-1.0-SNAPSHOT-jar-with-dependencies.jar ./vocab.txt
    echo "Enter: ./cassandra_helper show"
    exit 0
}

# clean
clean(){
    mvn clean
    ./kafka_helper clean
    ./cassandra_helper clean
    ./services_helper --stop
    rm -r LABA_2_input
    exit 0
}

$1
