#!/usr/bin/env bash
home=`readlink -f $(dirname $0)`
nohup /usr/bin/env java -DappName=pasterApp -Djava.awt.headless=false -Xmx2g -Dserver.port=8083 -Dloader.path=lib -Djava.net.preferIPv4Stack=true -jar `ls -1 $home/paster-run*.jar` </dev/null >$home/console.log 2>&1 &

