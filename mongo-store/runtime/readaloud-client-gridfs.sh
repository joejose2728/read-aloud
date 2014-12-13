
#! /usr/bin/env bash

# mongo-store project path
export PROJECT_PATH="/Users/bdeo/sjsu/dev/cmpe226-project2/read-aloud/mongo-store"

cd ${PROJECT_PATH}
mvn exec:java -Dexec.mainClass="app.ReadAloudGridFSPrintClient"
