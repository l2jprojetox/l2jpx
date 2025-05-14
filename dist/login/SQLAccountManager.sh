#!/bin/sh
java -Djava.util.logging.config.file=console.cfg -cp ./../libs/*: org.l2jpx.tools.accountmanager.SQLAccountManager
