@echo off
title Project X account manager console
@java -Djava.util.logging.config.file=config/console.cfg -cp ./libs/*; com.px.accountmanager.SQLAccountManager
@pause
