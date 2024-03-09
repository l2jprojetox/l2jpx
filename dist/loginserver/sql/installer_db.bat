@echo off

set PATH=%PATH%;C:\Program Files\MariaDB 11.4\bin\

set DBHOST=127.0.0.1
set PORT=3306
set USER=root
set PASS=852793
set DBNAME=l2jpx
set COMMAND="CREATE DATABASE IF NOT EXISTS "

mysql --host=%DBHOST% --port=%port% --user=%USER% --password=%PASS% -e "%COMMAND:"=%%DBNAME:"=%;"

for /r install %%f in (*.sql) do ( 
                echo Installing table %%~nf ...
		mysql -h %DBHOST% -u %USER% --password=%PASS% -D %DBNAME% < %%f
	)

pause
