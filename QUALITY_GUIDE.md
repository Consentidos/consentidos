# Guía de Calidad de Código - Proyecto Veterinaria Consentidos

## 🚀 Alternativas para Ejecutar Tests y Coverage antes de Push

Ahora que removimos los hooks automáticos problemáticos, tienes varias opciones para mantener la calidad del código:

### 1. 📜 Scripts Manuales (Recomendado)

#### Para Windows:

```bash
# Antes de hacer commit
./pre-commit.bat

# Antes de hacer push
./pre-push.bat
```

#### Para Linux/macOS:

```bash
# Antes de hacer commit
./pre-commit.sh

# Antes de hacer push
./pre-push.sh
```

### 2. 🎯 Tareas de VS Code

Abre la paleta de comandos (`Ctrl+Shift+P`) y ejecuta:

- **`Tasks: Run Task`** → `pre-commit-check` - Verificaciones rápidas antes de commit
- **`Tasks: Run Task`** → `pre-push-check` - Verificación completa antes de push
- **`Tasks: Run Task`** → `coverage-report` - Generar reporte de coverage
- **`Tasks: Run Task`** → `open-coverage-report` - Generar y abrir reporte de coverage

### 3. 🔧 Alias de Git (Automático)

Configuramos estos alias para ti:

```bash
# Commit con verificaciones automáticas
git safe-commit -m "tu mensaje de commit"

# Push con verificaciones automáticas
git safe-push

# Generar y abrir reporte de coverage
git coverage
```

### 4. 🤖 CI/CD Automático

El pipeline de GitHub Actions se ejecuta automáticamente en:

- ✅ Cada push a ramas `main`, `develop`, `fix/*`, `feature/*`
- ✅ Cada Pull Request a `main` o `develop`
- ✅ Ejecuta tests, genera coverage, verifica calidad
- ✅ Integración con SonarCloud y Codecov

## 📊 Verificaciones que se Ejecutan

### Pre-commit (Rápido ~30 segundos):

1. ✅ Compilación del código fuente
2. ✅ Compilación de tests
3. ✅ Ejecución de tests (sin coverage)

### Pre-push (Completo ~2-3 minutos):

1. 🧹 Limpieza del proyecto
2. ✅ Ejecución completa de tests
3. 📊 Generación de reporte de coverage
4. 🎯 Verificación de coverage mínimo (80%)
5. ⚠️ Opción de continuar si coverage es bajo

## 📈 Ver Reportes de Coverage

Los reportes se generan en: `build/reports/jacoco/test/html/index.html`

### Opciones para abrirlo:

1. **Automáticamente**: `git coverage` o tarea `open-coverage-report`
2. **Manualmente**: Abrir el archivo HTML en tu navegador
3. **VS Code**: Usar la extensión "Live Server" en el archivo

## 🔗 Flujo de Trabajo Recomendado

### Desarrollo diario:

```bash
# 1. Haz tus cambios
# 2. Antes de commit
./pre-commit.bat         # Windows
# o ./pre-commit.sh      # Linux/macOS

# 3. Si todo pasa, haz commit normal
git add .
git commit -m "feat: nueva funcionalidad"

# 4. Antes de push
./pre-push.bat          # Windows
# o ./pre-push.sh       # Linux/macOS

# 5. Si todo pasa, haz push normal
git push
```

### Flujo alternativo con alias:

```bash
# 1. Haz tus cambios
git add .

# 2. Commit con verificaciones automáticas
git safe-commit -m "feat: nueva funcionalidad"

# 3. Push con verificaciones automáticas
git safe-push
```

## ⚙️ Configuración Personalizada

### Cambiar Coverage Mínimo:

Edita `build.gradle` → `jacocoTestCoverageVerification` → `minimum`

### Personalizar Scripts:

Los scripts están en la raíz del proyecto y puedes modificarlos según tus necesidades.

### Deshabilitar Verificaciones:

Si necesitas hacer un push urgente, usa los comandos git normales:

```bash
git commit -m "mensaje"
git push
```

## 🆘 Solución de Problemas

### Si los scripts no se ejecutan:

```bash
# Windows: Verificar permisos de ejecución de PowerShell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Linux/macOS: Dar permisos de ejecución
chmod +x pre-commit.sh pre-push.sh
```

### Si fallan los tests:

1. Ejecuta `./gradlew test` para ver detalles
2. Revisa los logs en la terminal
3. Corrige los tests fallidos

### Si falla el coverage:

1. Ejecuta `git coverage` para ver el reporte
2. Agrega más tests a las áreas con bajo coverage
3. O temporalmente puedes continuar eligiendo "Y" en el prompt

## 📞 Ayuda

Si tienes problemas con estas configuraciones, revisa:

1. Los logs en la terminal
2. El estado de los tests: `./gradlew test`
3. La configuración de Gradle: `./gradlew tasks`
