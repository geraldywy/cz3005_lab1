# Lab submission for CZ3005 Lab 1

>Contributor: Gerald Yip Wei Yong [U1921851J] <br>
Contact: gyip002@e.ntu.edu.sg

## Prerequisites
1. Any recent version of java, java8 and above. <br>
For reference, running ```java --version``` on my device outputs: <br>
java 16.0.2 2021-07-20 <br>
Java(TM) SE Runtime Environment (build 16.0.2+7-67) <br>
Java HotSpot(TM) 64-Bit Server VM (build 16.0.2+7-67, mixed mode, sharing) <br>

2. An external dependency for json-simple is used for quality of life purposes. If the jar is missing from the project root, download it by running the command below in the project root directory.
```
$ curl https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/json-simple/json-simple-1.1.1.jar \
  --output json-simple-1.1.1.jar
```

## Running the code
Each task is ran separately in its own command, differing only in the folder name specified.

## Using makefile
"task" argument value must be one of TaskOne, TaskOneOptimised, TaskTwo or TaskThree. <br>
Example for task one.
```
$ make run task=TaskOne
```

## Alternatively, compiling and running manually
1. Compile the necessary class files, if not already present:
```
$ javac -cp "json-simple-1.1.1.jar" common/*.java
```

2. Run the appropriate task individually.
  
To run Task One:
```
$ java -cp ".:json-simple-1.1.1.jar" tasks/TaskOne.java
```

To run Task One optimised version (Bidirectional UCS):
```
$ java -cp ".:json-simple-1.1.1.jar" tasks/TaskOneOptimised.java
```

To run Task Two:
```
$ java -cp ".:json-simple-1.1.1.jar" tasks/TaskTwo.java
```

To run Task Three:
```
$ java -cp ".:json-simple-1.1.1.jar" tasks/TaskThree.java
```
