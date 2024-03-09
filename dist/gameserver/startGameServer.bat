@echo off
CHCP 65001
SET title_text=ProjectX 1.0 Login Server Console
SET revision_number=none
IF EXIST %file% (
	FOR /F "tokens=1* delims==" %%A IN (%file%) DO (
    	IF "%%A"=="revision" (
    		SET revision_number=%%B
    	)
	)
)

IF NOT %revision_number% == none (
	SET title_text=%title_text% revision %revision_number%
)

title %title_text%
color 0B

:start
java -Dfile.encoding=UTF8 -server -Xms1024m -Xmx1024m -XX:MetaspaceSize=256M -XX:+UseStringDeduplication -cp ./lib/*;l2jpx-core.jar net.l2jpx.gameserver.GameServer
SET exit_status=%ERRORLEVEL%
IF %exit_status% == 1 GOTO error
IF %exit_status% == 2 GOTO restart
IF %exit_status% == 4 GOTO taskdown
IF %exit_status% == 5 GOTO taskrestart
GOTO end

:error
pause
exit

:restart
echo.
echo Admin Restart ...
echo.
goto start

:taskdown
echo .
echo Server terminated (Auto task)
echo .

:taskrestart
echo.
echo Auto Task Restart ...
echo.
goto start

:end
echo.
echo server terminated
echo.
exit