@echo off
title Libris - Library Management System
echo ============================================================
echo            Libris - Library Management System
echo ============================================================
echo.
echo Building and starting application...
echo.

call mvn compile exec:java -Dexec.mainClass=com.libris.view.LoginView

pause