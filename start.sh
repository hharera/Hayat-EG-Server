#!/bin/bash
docker-compose -f docker-compose.yml down
bash ./core/build.sh
docker-compose -f docker-compose.yml up -d
