#!/bin/bash

cd .
rm -r target
mvn clean install -DskipTests