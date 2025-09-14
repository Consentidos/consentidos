@echo off
echo ====================================
echo     PRE-COMMIT CHECKS
echo ====================================
echo.

echo [1/3] Compilando código fuente...
call ./gradlew compileJava --console=plain --no-daemon
if %ERRORLEVEL% neq 0 (
    echo.
    echo ❌ ERROR: Falló la compilación del código fuente
    echo Arregla los errores de compilación antes de hacer commit
    pause
    exit /b 1
)

echo [2/3] Compilando tests...
call ./gradlew compileTestJava --console=plain --no-daemon
if %ERRORLEVEL% neq 0 (
    echo.
    echo ❌ ERROR: Falló la compilación de los tests
    echo Arregla los errores en los tests antes de hacer commit
    pause
    exit /b 1
)

echo [3/3] Ejecutando tests rápidos...
call ./gradlew test --console=plain --no-daemon -x jacocoTestReport
if %ERRORLEVEL% neq 0 (
    echo.
    echo ❌ ERROR: Algunos tests fallaron
    echo Arregla los tests antes de hacer commit
    pause
    exit /b 1
)

echo.
echo ✅ Todos los checks de pre-commit pasaron exitosamente!
echo Puedes proceder con el commit.
echo.