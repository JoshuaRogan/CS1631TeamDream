@echo off
title g1

javac -sourcepath ../../Components/g1 ../../Components/g1/*.java
start "g1GUI" /D"../../Components/g1" java g1GUI 127.0.0.1
start "g1SPO2" /D"../../Components/g1" java g1SPO2Monitor 127.0.0.1

pause