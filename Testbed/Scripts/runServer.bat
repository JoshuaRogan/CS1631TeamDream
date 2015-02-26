@echo off
title SIS Server

javac -sourcepath ../System/SISServer ../System/SISServer/*.java
java -cp ../System/SISServer SISServer

pause