# AudioCollector

A Java application to record spoken dialogue turns in a two-person conversation. The application can recorded up to four microphones simultaneously.

This application was used to collect the data reported in the SLPAT'19 workshop paper "Investigating Speech Recognition for Improving Predictive AAC":
https://keithv.com/pub/speechaac/

If you use this software in your reserach, please cite this [paper](https://www.aclweb.org/anthology/W19-1706):

```
@inproceedings{adhikary_speech,
  author       = {Jiban Adhikary and Robbie Watling and Crystal Fletcher and Alex Stanage and Keith Vertanen},
  title        = {Investigating Speech Recognition for Improving Predictive AAC},
  booktitle    = {SLPAT '19: Proceedings of the Workshop on Speech and Language Processing for Assistive Technologies},
  location     = {Minneapolis, MN},
  month        = {June},
  year         = {2019},
  pages        = {37--43},
}
```

## Installation and setup

1. Download [AudioCollector.zip](master.zip).
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
8. The files with dialogues follow a certain format. The AudioCollector will highlight those dialogue turns that are surrounded by the tags: '<h>' and '</h>'   

## Acknowledgements
This material is based upon work supported by the National Science Foundation under Grant No. (1750193). Any opinions, findings, and conclusions or recommendations expressed in this material are those of the author(s) and do not necessarily reflect the views of the National Science Foundation.

