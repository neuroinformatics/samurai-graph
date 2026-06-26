@echo off
REM Local CI verification script for samurai-graph (Windows)
REM Runs format check, compile, test, and package in sequence.

setlocal enabledelayedexpansion

set SCRIPT_DIR=%~dp0
set PROJECT_DIR=%SCRIPT_DIR%..

echo ========================================
echo  Samurai Graph - Local CI Verification
echo ========================================
echo.
echo Project: %PROJECT_DIR%
echo Started: %date% %time%
echo.

cd /d "%PROJECT_DIR%"

set FAILED=0
set FAILED_STEP=

:step_clean
echo [STEP] Clean
call mvn clean >nul 2>&1
if errorlevel 1 (
    echo [FAIL] Clean
    echo Aborting due to failure in: Clean
    exit /b 1
)
echo [PASS] Clean
echo.

:step_spotless
echo [STEP] Format Check (spotless:check)
call mvn spotless:check >nul 2>&1
if errorlevel 1 (
    echo [FAIL] Format Check (spotless:check)
    echo Aborting due to failure in: Format Check
    exit /b 1
)
echo [PASS] Format Check (spotless:check)
echo.

:step_compile
echo [STEP] Compile (with lint)
call mvn compile >nul 2>&1
if errorlevel 1 (
    echo [FAIL] Compile (with lint)
    echo Aborting due to failure in: Compile
    exit /b 1
)
echo [PASS] Compile (with lint)
echo.

:step_test
echo [STEP] Unit Tests
call mvn test >nul 2>&1
if errorlevel 1 (
    echo [FAIL] Unit Tests
    echo Aborting due to failure in: Unit Tests
    exit /b 1
)
echo [PASS] Unit Tests
echo.

:step_package
echo [STEP] Package (fat JAR)
call mvn package -DskipTests >nul 2>&1
if errorlevel 1 (
    echo [FAIL] Package (fat JAR)
    echo Aborting due to failure in: Package
    exit /b 1
)
echo [PASS] Package (fat JAR)
echo.

echo ========================================
echo All steps passed!
echo ========================================
exit /b 0
