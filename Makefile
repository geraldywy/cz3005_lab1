run:
	javac -cp "json-simple-1.1.1.jar" common/*.java && java -cp ".:json-simple-1.1.1.jar" tasks/${task}.java
	