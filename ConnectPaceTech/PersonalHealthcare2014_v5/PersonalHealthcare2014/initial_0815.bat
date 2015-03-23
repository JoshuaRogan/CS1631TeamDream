 @echo off
title SIS Initial


cd C:/Users/Josh/OneDrive/Git/CS1631TeamDream/ConnectPaceTech/System/SISServer
javac *.java
start "SISServer" /D"C:/Users/Josh/OneDrive/Git/CS1631TeamDream/ConnectPaceTech/System/SISServer" java SISServer


cd C:\Users\Josh\OneDrive\Git\CS1631TeamDream\ConnectPaceTech\PersonalHealthcare2014_v5\PersonalHealthcare2014\Initializer\src
javac *.java
start "Initializer" /D"C:/Users/Josh/OneDrive/Git/CS1631TeamDream/ConnectPaceTech/PersonalHealthcare2014_v5/PersonalHealthcare2014/Initializer/src" java Initializer

cd C:/Users/Josh/OneDrive/Git/CS1631TeamDream/ConnectPaceTech/PersonalHealthcare2014_v5/PersonalHealthcare2014/Components/BloodPressure
del *.class
javac *.java
start "BP" /D"C:/Users/Josh/OneDrive/Git/CS1631TeamDream/ConnectPaceTech/PersonalHealthcare2014_v5/PersonalHealthcare2014/Components/BloodPressure" java CreateBloodPressure

cd C:/Users/Josh/OneDrive/Git/CS1631TeamDream/ConnectPaceTech/PersonalHealthcare2014_v5/PersonalHealthcare2014/Components/SPO2
del *.class
javac *.java
start "SPO2" /D"C:/Users/Josh/OneDrive/Git/CS1631TeamDream/ConnectPaceTech/PersonalHealthcare2014_v5/PersonalHealthcare2014/Components/SPO2" java CreateSPO2

cd C:/Users/Josh/OneDrive/Git/CS1631TeamDream/ConnectPaceTech/PersonalHealthcare2014_v5/PersonalHealthcare2014/Components/EKG
del *.class
javac *.java
start "EKG" /D"C:/Users/Josh/OneDrive/Git/CS1631TeamDream/ConnectPaceTech/PersonalHealthcare2014_v5/PersonalHealthcare2014/Components/EKG" java CreateEKG

cd C:/Users/Josh/OneDrive/Git/CS1631TeamDream/ConnectPaceTech/PersonalHealthcare2014_v5/PersonalHealthcare2014/Components/UpLoader
del *.class
javac *.java
start "UpLoader" /D"C:/Users/Josh/OneDrive/Git/CS1631TeamDream/ConnectPaceTech/PersonalHealthcare2014_v5/PersonalHealthcare2014/Components/UpLoader" java CreateUpLoader

cd C:/Users/Josh/OneDrive/Git/CS1631TeamDream/ConnectPaceTech/PersonalHealthcare2014_v5/PersonalHealthcare2014/Components/KinnectSensor
del *.class
javac *.java
start "Kinnect" /D"C:/Users/Josh/OneDrive/Git/CS1631TeamDream/ConnectPaceTech/PersonalHealthcare2014_v5/PersonalHealthcare2014/Components/KinnectSensor" java CreateKinnectSensor


cd C:/Users/Josh/OneDrive/Git/CS1631TeamDream/ConnectPaceTech/PersonalHealthcare2014_v5/PersonalHealthcare2014/Components/InputProcessor
del *.class
javac *.java
start "InputProcessor" /D"C:/Users/Josh/OneDrive/Git/CS1631TeamDream/ConnectPaceTech/PersonalHealthcare2014_v5/PersonalHealthcare2014/Components/InputProcessor" java CreateInputProcessor


cd C:/Users/Josh/OneDrive/Git/CS1631TeamDream/ConnectPaceTech/PersonalHealthcare2014_v5/PersonalHealthcare2014/Components/Temp
del *.class
javac *.java
start "Temp" /D"C:/Users/Josh/OneDrive/Git/CS1631TeamDream/ConnectPaceTech/PersonalHealthcare2014_v5/PersonalHealthcare2014/Components/Temp" java CreateTemp

cd C:/Users/Josh/OneDrive/Git/CS1631TeamDream/ConnectPaceTech/PersonalHealthcare2014_v5/PersonalHealthcare2014/Components/GUI
del *.class
javac *.java
start "GUI" /D"C:/Users/Josh/OneDrive/Git/CS1631TeamDream/ConnectPaceTech/PersonalHealthcare2014_v5/PersonalHealthcare2014/Components/GUI" java CreateGUI


::javac -sourcepath ../PersonalHealthcare20140813/PersonalHealthcare2014/Initializer/src ../PersonalHealthcare20140813/PersonalHealthcare2014/Initializer/src/*.java
::start "GUI" /D"./PersonalHealthcare20140813/PersonalHealthcare2014/Initializer/src" java Initializer

::javac -sourcepath ../PersonalHealthcare20140813/PersonalHealthcare2014/Components/GUI ../PersonalHealthcare20140813/PersonalHealthcare2014/Components/GUI/*.java
::start "GUI" /D"./PersonalHealthcare20140813/PersonalHealthcare2014/Components/GUI" java CreateGUI

::javac -sourcepath ../PersonalHealthcare20140813/PersonalHealthcare2014/Components/SPO2 ../PersonalHealthcare20140813/PersonalHealthcare2014/Components/SPO2/*.java
::start "SPO2" /D"../PersonalHealthcare20140813/PersonalHealthcare2014/Components/SPO2" java CreateSPO2

::javac -sourcepath ../Components/UpLoader ../PersonalHealthcare20140813/PersonalHealthcare2014/Components/UpLoader/*.java
::start "UpLoader" /D"../PersonalHealthcare20140813/PersonalHealthcare2014/Components/UpLoader" java CreateUpLoader

::pause