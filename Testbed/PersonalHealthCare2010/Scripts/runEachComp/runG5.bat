@echo off
title g5

javac -sourcepath ../../Components/g5 ../../Components/g5/*.java
start "g5bloodsugar" /D"../../Components/g5" java g5bloodsugar 127.0.0.1
start "g5kb" /D"../../Components/g5" java g5kb 127.0.0.1
