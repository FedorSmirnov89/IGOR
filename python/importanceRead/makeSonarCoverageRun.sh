#!/bin/bash

/scratch-local/smirnov/.local/bin/coverage erase
/scratch-local/smirnov/.local/bin/coverage run -m unittest discover
/scratch-local/smirnov/.local/bin/coverage xml -i
/scratch-local/sonarScanner/sonar-scanner-3.3.0.1492-linux/bin/sonar-scanner
