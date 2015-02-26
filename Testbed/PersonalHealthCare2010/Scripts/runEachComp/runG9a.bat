@echo off
title g9a

javac -sourcepath ../../Components/g9a ../../Components/g9a/*.java
start "g9aSPO2" /D"../../Components/g9a" java g9aSPO2 127.0.0.1
