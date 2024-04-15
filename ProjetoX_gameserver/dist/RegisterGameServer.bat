@echo off
title Project X gameserver registration console
@java -Djava.util.logging.config.file=config/console.cfg -cp ./libs/*; com.px.gsregistering.GameServerRegister
@pause