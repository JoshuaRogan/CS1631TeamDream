@echo off
title SISInitial
cd C:/Users/admin.sis/Desktop/Jerry's workspace/ConnectPaceTech/System/SISServer
javac *.java
start "SISServer" /D"C:/Users/admin.sis/Desktop/Jerry's workspace/ConnectPaceTech/System/SISServer" java SISServer


cd C:/Users/admin.sis/Desktop/Jerry's workspace/ConnectPaceTech/PersonalHealthcare2014/Components/GUI
javac *.java
start "GUI" /D"C:/Users/admin.sis/Desktop/Jerry's workspace/ConnectPaceTech/PersonalHealthcare2014/Components/GUI" java CreateGUI

cd C:/Users/admin.sis/Desktop/Jerry's workspace/ConnectPaceTech/PersonalHealthcare2014/Components/SPO2
javac *.java
start "SPO2" /D"C:/Users/admin.sis/Desktop/Jerry's workspace/ConnectPaceTech/PersonalHealthcare2014/Components/SPO2" java CreateSPO2

cd C:/Users/admin.sis/Desktop/Jerry's workspace/ConnectPaceTech/PersonalHealthcare2014/Components/UpLoader
javac *.java
start "UpLoader" /D"C:/Users/admin.sis/Desktop/Jerry's workspace/ConnectPaceTech/PersonalHealthcare2014/Components/UpLoader" java CreateUpLoader



