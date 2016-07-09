#!/usr/bin/env bash

# example of the run script for running the rolling_median calculation with a python file, 
# but could be replaced with similar files from any major language
javac -cp ./src/java-json.jar ./src/median_degree.java ./src/edge.java
java -cp ./src/java-json.jar:./src median_degree
# I'll execute my programs, with the input directory venmo_input and output the files in the directory venmo_output




