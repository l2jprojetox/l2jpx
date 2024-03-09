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
java -Dfile.encoding=UTF8 -Xms128m -Xmx128m -cp ./lib/*;l2jpx-core.jar net.l2jpx.loginserver.L2LoginServer
SET exit_status=%ERRORLEVEL%
IF %exit_status% == 1 GOTO error
IF %exit_status% == 2 GOTO restart
GOTO end

:error
pause
exit

:restart
echo.
echo Admin Restarted ...
echo.
goto start

:end
echo.
echo LoginServer terminated
echo.
