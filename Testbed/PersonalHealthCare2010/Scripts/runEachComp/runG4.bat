@echo off
title g4

javac -sourcepath ../../Components/g4 ../../Components/g4/*.java
start "g4bloodpressure" /D"../../Components/g4" java g4bloodpressure 127.0.0.1
start "g4kb" /D"../../Components/g4" java g4kb 127.0.0.1
