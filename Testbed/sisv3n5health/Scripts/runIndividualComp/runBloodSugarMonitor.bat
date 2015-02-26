@echo off
title BloodSugarMonitor

javac -sourcepath ../../Components/BloodSugarMonitor ../../Components/BloodSugarMonitor/*.java
start "BloodSugarMonitor" /D"../../Components/BloodSugarMonitor" java CreateBloodSugarMonitor
