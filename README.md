# qudiniApp-configuration-data-generator

## Project motivation:

This project is heavily based on the code already produced to populate the qudini app, the main changes are refactor of 
possible deprecated methods, addiction on basic logging instead of STDOUT and possible runner customizations.   

The primary intend of this project is decoupling this code from the test project and this way to act has a standalone
with the possibility of allowing further config customization and extension. 

Being a standalone allows the most generic QudiniApp configurations to be run only once and not by test suite, which 
increases the test batteries run time.  

It can also be used by any development team to populate at least the basic data on qudini app.

## Prerequisites:
- Java 8
- Maven 3.5+

## Commands:
- To be added in the future


