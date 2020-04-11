@echo off
setlocal

set TOOLDIR=..\..\..\doc\tools\win32
set PRODUCTXML=../../../web/htdocs/product.xml
set CHANGELOGXSL=ChangeLog.xsl
set CHANGELOGXML=..\resources\ChangeLog.html

if exist %CHANGELOGXML% del /f %CHANGELOGXML%

%TOOLDIR%\xmllint -valid -noout %PRODUCTXML%
if %ERRORLEVEL%==0 goto next1
  echo Error : %PRODUCTXML% - invalid format xml file.
  exit /b 1
:next1

%TOOLDIR%\xsltproc -o %CHANGELOGXML% %CHANGELOGXSL% %PRODUCTXML%
if %ERRORLEVEL%==0 goto done
  echo "Error : CHANGELOGXML - invalid format xml file "
  exit /b 1
:done

endlocal
