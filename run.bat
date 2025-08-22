@echo off
echo Stopping any running Spring Boot applications...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
    echo Found process on port 8080 with PID %%a
    taskkill /F /PID %%a
    echo Killed process with PID %%a
)

echo Starting the application...
call gradlew bootRun
