set "client=%cd%\Client"
set "filesystem=%cd%\FileSystems"
set "auth=%cd%\AuthServer"

rem  -- installs project

REM start cmd /c "title INSTALLING & cd .. & mvn clean install"
REM timeout /t 15
rem -- Start Auth Server
start cmd /c "title AuthServer & cd .. & cd AuthServer & mvn spring-boot:run"

rem -- Start FileSystem
start cmd /c "title FileSystem & cd .. & cd FileSystem & mvn spring-boot:run"
