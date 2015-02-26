@echo off
title ResultUploader

javac -sourcepath ../../Components/ResultUploader ../../Components/ResultUploader/*.java
start "ResultUploader" /D"../../Components/ResultUploader" java CreateResultUploader
