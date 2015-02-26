@echo off
title g7

javac -sourcepath ../../Components/g7 ../../Components/g7/*.java
start "g7ekg" /D"../../Components/g7" java g7ekg 127.0.0.1
ping -n 2 127.1>nul
start "g7kb" /D"../../Components/g7" java g7kb 127.0.0.1
