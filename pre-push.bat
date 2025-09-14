@echo off
echo ====================================
echo     PRE-PUSH VERIFICATION
echo ====================================
echo.

echo [1/4] Limpiando proyecto...
call ./gradlew clean --console=plain --no-daemon
if %ERRORLEVEL% neq 0 (
    echo.
    echo ❌ ERROR: Falló la limpieza del proyecto
    pause
    exit /b 1
)

echo [2/4] Ejecutando todos los tests...
call ./gradlew test --console=plain --no-daemon
if %ERRORLEVEL% neq 0 (
    echo.
    echo ❌ ERROR: Algunos tests fallaron
    echo Arregla todos los tests antes de hacer push
    pause
    exit /b 1
)

echo [3/4] Generando reporte de coverage...
call ./gradlew jacocoTestReport --console=plain --no-daemon
if %ERRORLEVEL% neq 0 (
    echo.
    echo ❌ ERROR: Falló la generación del reporte de coverage
    pause
    exit /b 1
)

echo [4/4] Verificando coverage mínimo...
call ./gradlew jacocoTestCoverageVerification --console=plain --no-daemon
if %ERRORLEVEL% neq 0 (
    echo.
    echo ⚠️  WARNING: El coverage no cumple con el mínimo requerido (80%)
    echo Revisa el reporte en: build\reports\jacoco\test\html\index.html
    echo.
    choice /C YN /M "¿Continuar con el push a pesar del coverage bajo? (Y/N)"
    if errorlevel 2 (
        echo Push cancelado por el usuario
        exit /b 1
    )
)

echo.
echo ✅ Todos los checks de pre-push completados!
echo 📊 Reporte de coverage disponible en: build\reports\jacoco\test\html\index.html
echo Puedes proceder con el push.
echo.