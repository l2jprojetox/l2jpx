#!/bin/sh
java -Djava.util.logging.config.file=config/console.cfg -cp ./libs/*:l2jserver.jar:mariadb-java-client-3.1.4 com.px.accountmanager.SQLAccountManager
