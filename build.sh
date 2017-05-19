#!/bin/bash
docker rmi -f axibase/atsd_package_validation
docker build -t "axibase/atsd_package_validation" --no-cache .
