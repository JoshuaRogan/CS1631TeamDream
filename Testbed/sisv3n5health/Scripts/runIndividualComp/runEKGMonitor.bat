@echo off
title EKGMonitor

javac -sourcepath ../../Components/EKGMonitor ../../Components/EKGMonitor/*.java
start "EKGMonitor" /D"../../Components/EKGMonitor" java CreateEKGMonitor
