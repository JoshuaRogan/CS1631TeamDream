@echo off
title SIS Server

javac -sourcepath ../../Components/g9b ../../Components/g9b/*.java
java -cp ../../Components/g9b g9bNAME 127.0.0.1

