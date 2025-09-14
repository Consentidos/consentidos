#!/bin/bash
echo "===================================="
echo "     PRE-PUSH VERIFICATION"
echo "===================================="
echo

echo "[1/4] Limpiando proyecto..."
./gradlew clean --console=plain --no-daemon
if [ $? -ne 0 ]; then
    echo
    echo "❌ ERROR: Falló la limpieza del proyecto"
    exit 1
fi

echo "[2/4] Ejecutando todos los tests..."
./gradlew test --console=plain --no-daemon
if [ $? -ne 0 ]; then
    echo
    echo "❌ ERROR: Algunos tests fallaron"
    echo "Arregla todos los tests antes de hacer push"
    exit 1
fi

echo "[3/4] Generando reporte de coverage..."
./gradlew jacocoTestReport --console=plain --no-daemon
if [ $? -ne 0 ]; then
    echo
    echo "❌ ERROR: Falló la generación del reporte de coverage"
    exit 1
fi

echo "[4/4] Verificando coverage mínimo..."
./gradlew jacocoTestCoverageVerification --console=plain --no-daemon
if [ $? -ne 0 ]; then
    echo
    echo "⚠️  WARNING: El coverage no cumple con el mínimo requerido (80%)"
    echo "Revisa el reporte en: build/reports/jacoco/test/html/index.html"
    echo
    read -p "¿Continuar con el push a pesar del coverage bajo? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Push cancelado por el usuario"
        exit 1
    fi
fi

echo
echo "✅ Todos los checks de pre-push completados!"
echo "📊 Reporte de coverage disponible en: build/reports/jacoco/test/html/index.html"
echo "Puedes proceder con el push."
echo