#!/usr/bin/env bash



if pgrep -f appName=pasterApp  > /dev/null
then
    echo "running Paster process found, killing"
    pgrep -f appName=pasterApp | xargs kill -9

else
    echo "Paster is already stopped"
fi


