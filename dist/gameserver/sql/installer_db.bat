@echo off

set PATH=%PATH%;C:\xampp\mysql\bin

set DBHOST=127.0.0.1
set PORT=3306
set USER=root
set PASS=
set DBNAME=frozen
set COMMAND="CREATE DATABASE IF NOT EXISTS "

mysql --host=%DBHOST% --port=%PORT% --user=%USER% --password=%PASS% -e "%COMMAND:"=%%DBNAME:"=%;"

for /r install/ %%f in (*.sql) do ( 
        echo Installing table %%~nf ...
		mysql -h %DBHOST% -u %USER% --password=%PASS% -D %DBNAME% < %%f
	)

pause
