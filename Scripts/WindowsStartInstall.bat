set "client=%cd%\Client"
set "filesystem=%cd%\FileSystems"
set "auth=%cd%\AuthServer"

rem  -- installs project

start cmd /c "title INSTALLING & cd .. & mvn clean install -DskipTests "
timeout /t 50

rem -- Start Auth Server
start cmd /c "title AuthServer & cd .. & cd AuthServer & mvn clean install -DskipTests spring-boot:run"

rem -- Start FileSystem
start cmd /c "title FileSystem & cd .. & cd FileSystem & mvn clean install -DskipTests spring-boot:run"

rem -- Start FileSystem
start cmd /k "title Client & cd .. & cd Client & mvn clean install -DskipTests exec:java"

