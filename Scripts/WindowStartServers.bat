set "client=%cd%\Client"
set "filesystem=%cd%\FileSystems"
set "auth=%cd%\AuthServer"

rem  -- installs project
cd ..
mvn clean install -DskipTests

rem -- Start Auth Server
start cmd /k "title AuthServer & cd %auth% & mvn spring-boot:run"

rem -- Start FileSystem
start cmd /c "title FileSystem cd %filesystem% & mvn spring-boot:run"
