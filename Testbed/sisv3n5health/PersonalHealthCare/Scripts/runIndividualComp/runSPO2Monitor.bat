@echo off
title SPO2Monitor

javac -sourcepath ../../Components/SPO2Monitor ../../Components/SPO2Monitor/*.java
start "SPO2Monitor" /D"../../Components/SPO2Monitor" java CreateSPO2Monitor
