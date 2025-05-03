# README

Converts:

* Swagger 3 YML and JSON files to HTML
* Converts AsciiDoc files to HTML

Generates:

* Index.html files

## Requirements

Minimal requirements:

* Java 24 or higher
* Maven 3.9.* or higher

## Building the application

Run:

    mvn clean install

## Running the application

After compiling you can run:

    CONFIG_DIR=${PWD}/config mvn -pl :docgen-app exec:java

## More information

See the documentation in the docs directory for more information

## Features

* Converts AsciiDoc files
* Creates an index
* Indexes your documentation files for searching