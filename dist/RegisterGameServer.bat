@echo off
title aCis gameserver registration console
@java -Djava.util.logging.config.file=config/console.cfg -cp ./libs/*; com.px.gsregistering.GameServerRegister
@pause