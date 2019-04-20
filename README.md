# AudioCollector

A Java application to record spoken dialogue turns. 

## Installation and setup

1. Download AudioCollector zip direcotry from https://github.com/kdv123/AudioCollector/archive/master.zip .
2. Unzip the zipped directory in your local machine.
3. Make sure you have Java installed in your machine and bin path configured in your environment variables.
4. Open terminal and move to the unzipped application directory.
5. Compile all the java files using the following command:
```
javac *.java
```
6. The Java files should compile without any error. Then type the following to run the application:
```
java AudioCollector
```
7. A screen will pop up and you have to provide a participant ID, a session number, a file name having the text dialogues, a condition, and check available microphones.
8. The files with dialogues follow a certain format. The AudioCollector will highlight those dialogues turns that are surrounded by the tags: '<h>' and '</h>'   
