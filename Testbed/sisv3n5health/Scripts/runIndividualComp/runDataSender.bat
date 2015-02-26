@echo off
title DataSender

javac -sourcepath ../../Components/DataSender ../../Components/DataSender/*.java
start "DataSender" /D"../../Components/DataSender" java CreateDataSender
