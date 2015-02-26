@echo off
title g3

javac -sourcepath ../../Components/g3 ../../Components/g3/*.java
start "g3bloodpressure" /D"../../Components/g3" java g3bloodpressure 127.0.0.1
start "g3kb" /D"../../Components/g3" java g3kb 127.0.0.1

pause