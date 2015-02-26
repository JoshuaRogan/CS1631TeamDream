@echo off
title StartAllComps

javac -sourcepath ../Components/SPO2Monitor ../Components/SPO2Monitor/*.java
start "SPO2Monitor" /D"../Components/SPO2Monitor" java CreateSPO2Monitor

javac -sourcepath ../Components/BloodSugarMonitor ../Components/BloodSugarMonitor/*.java
start "BloodSugarMonitor" /D"../Components/BloodSugarMonitor" java CreateBloodSugarMonitor

javac -sourcepath ../Components/BloodPressureMonitor ../Components/BloodPressureMonitor/*.java
start "BloodPressureMonitor" /D"../Components/BloodPressureMonitor" java CreateBloodPressureMonitor

javac -sourcepath ../Components/EKGMonitor ../Components/EKGMonitor/*.java
start "EKGMonitor" /D"../Components/EKGMonitor" java CreateEKGMonitor

javac -sourcepath ../Components/DataSender ../Components/DataSender/*.java
start "DataSender" /D"../Components/DataSender" java CreateDataSender

javac -sourcepath ../Components/ResultUploader ../Components/ResultUploader/*.java
start "ResultUploader" /D"../Components/ResultUploader" java CreateResultUploader

