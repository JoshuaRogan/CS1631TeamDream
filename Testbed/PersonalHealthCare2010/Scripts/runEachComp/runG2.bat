@echo off
title g2

javac -sourcepath ../../Components/g2 ../../Components/g2/*.java
start "g2GUI" /D"../../Components/g2" java g2GUI 127.0.0.1
start "g2SPO2" /D"../../Components/g2" java g2SPO2Monitor 127.0.0.1


