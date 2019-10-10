REM
REM  Make All.
REM

cd .\contrib
call build.bat build
cd ..

call tools\ant.bat

pause
