@echo off
title Project X loginserver console
:start
java -Xmx32m -cp ./libs/*; com.px.loginserver.LoginServer
if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Admin have restarted, please wait.
echo.
goto start
:error
echo.
echo Server have terminated abnormaly.
echo.
:end
echo.
echo Server terminated.
echo.
pause
