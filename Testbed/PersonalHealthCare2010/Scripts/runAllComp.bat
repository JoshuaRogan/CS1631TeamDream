@echo off
title g1

javac -sourcepath ../Components/g1 ../Components/g1/*.java
start "g1GUI" /D"../Components/g1" java g1GUI 127.0.0.1
start "g1SPO2" /D"../Components/g1" java g1SPO2Monitor 127.0.0.1

javac -sourcepath ../Components/g2 ../Components/g2/*.java
start "g2GUI" /D"../Components/g2" java g2GUI 127.0.0.1
start "g2SPO2" /D"../Components/g2" java g2SPO2Monitor 127.0.0.1

javac -sourcepath ../Components/g3 ../Components/g3/*.java
start "g3bloodpressure" /D"../Components/g3" java g3bloodpressure 127.0.0.1
start "g3kb" /D"../Components/g3" java g3kb 127.0.0.1

javac -sourcepath ../Components/g4 ../Components/g4/*.java
start "g4bloodpressure" /D"../Components/g4" java g4bloodpressure 127.0.0.1
start "g4kb" /D"../Components/g4" java g4kb 127.0.0.1

javac -sourcepath ../Components/g5 ../Components/g5/*.java
start "g5bloodsugar" /D"../Components/g5" java g5bloodsugar 127.0.0.1
start "g5kb" /D"../Components/g5" java g5kb 127.0.0.1

javac -sourcepath ../Components/g6 ../Components/g6/*.java
start "g6bloodsugar" /D"../Components/g6" java g6bloodsugar 127.0.0.1
start "g6kb" /D"../Components/g6" java g6kb 127.0.0.1

javac -sourcepath ../Components/g7 ../Components/g7/*.java
start "g7ekg" /D"../Components/g7" java g7ekg 127.0.0.1
ping -n 2 127.1>nul
start "g7kb" /D"../Components/g7" java g7kb 127.0.0.1

javac -sourcepath ../Components/g8 ../Components/g8/*.java
start "g8ekg" /D"../Components/g8" java g8ekg 127.0.0.1
ping -n 2 127.1>nul
start "g8kb" /D"../Components/g8" java g8kb 127.0.0.1

javac -sourcepath ../Components/g9a ../Components/g9a/*.java
start "g9aSPO2" /D"../Components/g9a" java g9aSPO2 127.0.0.1
