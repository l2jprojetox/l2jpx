#!/bin/sh
java -Djava.util.logging.config.file=config/console.cfg -cp ./libs/*:l2jserver.jar:mariadb-java-client-2.5.2 com.l2jpx.accountmanager.SQLAccountManager
