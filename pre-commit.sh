#!/bin/bash
echo "===================================="
echo "     PRE-COMMIT CHECKS"
echo "===================================="
echo

echo "[1/3] Compilando código fuente..."
./gradlew compileJava --console=plain --no-daemon
if [ $? -ne 0 ]; then
    echo
    echo "❌ ERROR: Falló la compilación del código fuente"
    echo "Arregla los errores de compilación antes de hacer commit"
    exit 1
fi

echo "[2/3] Compilando tests..."
./gradlew compileTestJava --console=plain --no-daemon
if [ $? -ne 0 ]; then
    echo
    echo "❌ ERROR: Falló la compilación de los tests"
    echo "Arregla los errores en los tests antes de hacer commit"
    exit 1
fi

echo "[3/3] Ejecutando tests rápidos..."
./gradlew test --console=plain --no-daemon -x jacocoTestReport
if [ $? -ne 0 ]; then
    echo
    echo "❌ ERROR: Algunos tests fallaron"
    echo "Arregla los tests antes de hacer commit"
    exit 1
fi

echo
echo "✅ Todos los checks de pre-commit pasaron exitosamente!"
echo "Puedes proceder con el commit."
echo