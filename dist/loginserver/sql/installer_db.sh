#!/bin/sh
# Author: ReynalDev

DBHOST=127.0.0.1
PORT=3306
USER=root
PASS=
DBNAME=frozen

mysql --host=$DBHOST --port=$PORT --user=$USER --password=$PASS -e "CREATE DATABASE IF NOT EXISTS $DBNAME;"

for sqlfile in install/*.sql
do
	filename=$(basename "$sqlfile")
	echo Installing table $filename to database " $DBNAME " ...
	mysql --host=$DBHOST --user=$USER --password=$PASS $DBNAME < $sqlfile
done
