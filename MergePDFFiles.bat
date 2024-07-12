@echo off
setlocal

rem Verifica se Java è installato e la versione è corretta
java -version
if ERRORLEVEL 1 (
    echo Java non è installato o non è configurato correttamente nel PATH.
    pause
    exit /b 1
)

rem Avvia il backend Spring Boot (usa il percorso relativo al file JAR)
start cmd /k "java -jar target\pdf-page-merger-1.0.0.jar && echo Backend avviato con successo. || echo Errore nell'avvio del backend. & pause"


rem (assicurati che il backend sia avviato prima)
echo Attendo disponibilita del backend...
:waitLoop
netstat -aon | findstr :8080 | findstr LISTENING > NUL
if errorlevel 1 goto waitLoop

echo Avvio frontend in corso...

rem Apri il frontend nel browser (sostituisci con l'URL del tuo frontend)
start "" "%~dp0fe\page.html"

@echo off
