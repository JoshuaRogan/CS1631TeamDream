@echo off
title g8

javac -sourcepath ../../Components/g8 ../../Components/g8/*.java
start "g8ekg" /D"../../Components/g8" java g8ekg 127.0.0.1
ping -n 2 127.1>nul
start "g8kb" /D"../../Components/g8" java g8kb 127.0.0.1
