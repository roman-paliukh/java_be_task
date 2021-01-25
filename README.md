
As there are only 259 items, a simple list could be used as cache,
used SQLite to make it seem more advanced. Used java 11 built-in http library to
reduce dependencies.

# Dependencies
Included in jar file

jackson - Json parser

SQLite - storage

# Build
With java 11 and maven installed, run 

`mvn package`


# Execute
Maven will create a jar file with all dependencies inside.
To run execute, run in command line with java 11 installed

`java -jar target/java_be_task-jar-with-dependencies.jar`