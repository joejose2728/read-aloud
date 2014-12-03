#! /usr/bin/env bash

# mongod binary home
export MONGODB_HOME="/Users/bdeo/sjsu/dev/mongodb-osx-x86_64-2.6.5/bin"
# mongo database storage path
export MONGODB_DATA="/opt/data/mongodb/cmpe226-project2"

cd ${MONGODB_HOME}
echo "starting database located at ${MONGODB_DATA}"
echo "--------------------------------------------"
./mongod --dbpath ${MONGODB_DATA} --port 27017
