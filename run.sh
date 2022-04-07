#!/bin/bash
ITERS=100000


# start
run(){
    ./generate_data $ITERS              | tee full.log
    ./check_deps 2>&1                   | tee -a full.log
    ./services_helper --start 2>&1      | tee -a full.log
    ./cassandra_helper clean   2>&1     | tee cassandra.log
    ./cassandra_helper prepare 2>&1     | tee -a cassandra.log
    ./kafka_helper prepare
    ./kafka_helper upload
    mvn package 2>&1 | tee build.log
    java -jar target/LABA_2-1.0-SNAPSHOT-jar-with-dependencies.jar ./vocab.txt 2>&1 \
                                        | tee job.log
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
