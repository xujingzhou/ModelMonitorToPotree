@echo off

color 06

for %%i in (%0) do set aa=%%~dpi  
cd /d %aa%

echo ---------------------------------------------------------------------------
echo  Start script for the PotreeConverter Monitor Daemon
echo ---------------------------------------------------------------------------
echo=                     

call java -jar potreeConverterMonitor-1.0.jar

pause

