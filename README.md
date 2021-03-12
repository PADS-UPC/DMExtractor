# DMExtractor

This tool automatically extracts the Decision Model Notation (DMN) from a natural language text.

## Prerequisites

* To parse text we have used [Freeling](http://nlp.lsi.upc.edu/freeling/) (v4.1)
* To develop the project, [Eclipse IDE](https://www.eclipse.org/downloads/) as a development environment was used. 
* As Runtime Environment we have used [Java JRE 1.8](https://java.com/en/download/help/index_installing.xml). 
* The proyect was set up as a [Maven Project](https://maven.apache.org/install.html) (v.3.6.0).

```
Freeling: http://nlp.lsi.upc.edu/freeling/
Eclipse: https://www.eclipse.org/downloads/
Java JRE: https://java.com/en/download/help/index_installing.xml
Maven: https://maven.apache.org/install.html
```

## Running project
The main file to run is App.java.

The input parameter should be  the path of the file to be parsed (e.g.: ``input/text/myprocesstext.txt``). If the path is not indicated, the program will automatically parse all the files that are in the folder: 

``` 
input/texts/ 
```

The results will always be stored with the same input name (.dmn) in the following path: 

```
output/dmn/
```

To change all the default paths:

```
src/main/java/edu/upc/parserutils/FoldersUrl.java
```