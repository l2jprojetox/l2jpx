#!/bin/bash
# Author: ReynalDev

exit_status=1
until [ $exit_status == 0 ]; 
do
	java -Xms128m -Xmx128m -cp lib/*:l2jpx-core.jar net.l2jpx.loginserver.L2LoginServer > log/stdout.log 2>&1
	exit_status=$?
	
	if [ $exit_status == 1 ] # Exit status sent by Java System.exit(1);
	then
		break
	elif [ $exit_status == 143 ] # Exit status sent by command pkill -TERM 
	then
		break
	fi

done
