@echo off
setlocal enabledelayedexpansion

set "scriptPath=%~dp0"
cd !scriptPath!

set "binFolderPath=.\bin"

:: Check if the folder exists
if not exist "%binFolderPath%" (
    :: If the folder doesn't exist, create it
    mkdir "%binFolderPath%"
) else (
    rmdir /s /q "%binFolderPath%"
)

:: Find Java source files (*.java) and JAR files (*.jar)
set "javaFiles="
set "jarFiles="

:: Enable usebackq for proper handling of file paths with spaces
for /r %%i in (*.java) do (
    set "javaFiles=!javaFiles! %%i"
)

for /r %%i in (*.jar) do (
    set "jarFiles=!jarFiles!;%%i"
)

:: Build the classpath for any JAR files found in the source directory and its subdirectories
set "classpath=!jarFiles:~1!"

:: Compile the Java source files
set "javaFilesString=!javaFiles!"
javac -d !binFolderPath! -cp "!classpath!" !javaFilesString!

:: Run the Java program
set "javaExecutable=java"
set "modulePath=.\lib"
set "modules=javafx.controls,javafx.fxml"
set "fullClasspath=!classpath!;.\resources;!binFolderPath!"
set "mainClass=dev.alabbad.DataAnalyticsHub.Main"
start /wait "" !javaExecutable! --module-path "!modulePath!" --add-modules !modules! -cp "!fullClasspath!" !mainClass!

if exist "%binFolderPath%" (
    rmdir /s /q "%binFolderPath%"
    echo Folder deleted successfully.
)

endlocal
