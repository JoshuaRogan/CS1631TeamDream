@echo off
title Enumerator

javac -sourcepath ../Components/Cycle1/ICA -cp ../lib/javabuilder.jar;../lib/ICA_I.jar; ../Components/Cycle1/ICA/*.java
javac -sourcepath ../Components/Cycle1/TON -cp ../lib/javabuilder.jar;../lib/TON_I.jar; ../Components/Cycle1/TON/*.java
javac -sourcepath ../Components/Cycle2/PCA -cp ../lib/javabuilder.jar;../lib/PCA_I.jar; ../Components/Cycle2/PCA/*.java

cd ../System/Enumerator
javac *.java
java Enumerator
