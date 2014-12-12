#! /usr/bin/env bash

echo "COMPILING....."
echo "--------------"

cd ..
mvn compile

echo "DONE.........."
echo "--------------"

# run the app
echo "loading files into the database (GridFS implementation)"
echo "-------------------------------"
mvn exec:java -Dexec.mainClass="app.MultipleFileLoaderGridFSApp"
