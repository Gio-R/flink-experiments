#!/bin/bash

cd quickstart
mvn clean package
cd ..
docker-compose up -d