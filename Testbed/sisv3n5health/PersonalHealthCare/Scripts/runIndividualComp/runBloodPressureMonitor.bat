@echo off
title BloodPressureMonitor

javac -sourcepath ../../Components/BloodPressureMonitor ../../Components/BloodPressureMonitor/*.java
start "BloodPressureMonitor" /D"../../Components/BloodPressureMonitor" java CreateBloodPressureMonitor
