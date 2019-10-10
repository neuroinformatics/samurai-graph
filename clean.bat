REM
REM  Make All.
REM

cd .\contrib
call build.bat clean
cd ..

call tools\ant.bat clean

pause
