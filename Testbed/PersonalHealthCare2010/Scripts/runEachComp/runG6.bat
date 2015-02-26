@echo off
title g6

javac -sourcepath ../../Components/g6 ../../Components/g6/*.java
start "g6bloodsugar" /D"../../Components/g6" java g6bloodsugar 127.0.0.1
start "g6kb" /D"../../Components/g6" java g6kb 127.0.0.1