# ProjSIRS
Secure remote cloud-based application for document store and access

Step to execute:

Installation:

-Install Java 8
-Install MySQL
-Install maven

Step by Step:

- Create database with the script provided at AuthServer/src/main/database/create-database.sql

- Go to the AuthServer/src/main/resources/config.example, change the name to config.properties and change the credetials to match the ones in the MySQL installation.

-Go to the folder Scripts and run the WindowsStartInstall.
  -if in linux, go to the project root directory:
    -And execute mvn clean install.
    -Open another terminal and go to AuthServer/ and run mvn spring-boot:run
    -Open another terminal and go to FileSystem/ and run mvn spring-boot:run
    -Open another terminal and go to Client/ and run mvn exec:java
    

-Insert the password(="password") in the AuthServer and FileSystem consoles.

-Use the client at your will.




