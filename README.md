# ProjSIRS - Group 11
Secure cloud-based application for remote document access

Steps to execute:

Installation:

- Install JDK 8
- Install MySQL
- Install Maven

Step by Step:

- Create database with the script provided at AuthServer/src/main/database/create-database.sql

- Go to the AuthServer/src/main/resources/, rename the file config.example to config.properties and change the credetials to match the ones in the MySQL installation.

- On Windows: go to the folder Scripts and run the WindowsStartInstall.

- On Linux, go to the project root directory:
    * Execute mvn clean install.
    * Open another terminal and go to AuthServer/ and run mvn spring-boot:run
    * Open another terminal and go to FileSystem/ and run mvn spring-boot:run
    * Open another terminal and go to Client/ and run mvn exec:java
    

- Insert the password(="password") in the AuthServer and FileSystem consoles.

- Use the client at your will.




