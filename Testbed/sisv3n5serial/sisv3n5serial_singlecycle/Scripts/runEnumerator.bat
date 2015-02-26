@echo off
title Enumerator

javac -sourcepath ../Components/Cycle1/S1 ../Components/Cycle1/S1/*.java
javac -sourcepath ../Components/Cycle1/S2 ../Components/Cycle1/S2/*.java
javac -sourcepath ../Components/Cycle1/S3 ../Components/Cycle1/S3/*.java

cd ../System/Enumerator
javac *.java
java Enumerator
