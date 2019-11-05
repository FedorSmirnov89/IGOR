#!/usr/bin/env bash

##cd $1
echo "Starting Python Server"
export PYTHONPATH=$PYTHONPATH:`pwd`
python ./content/infrastructure/comm_socket.py &
echo "Python Server started"
#cd ${cwd_var}