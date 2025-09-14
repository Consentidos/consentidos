# 🚀 Guía de Debugging con VS Code y Docker - ¡FUNCIONANDO!

## ✅ **Estado: CONFIGURADO Y FUNCIONANDO**

La configuración de debugging con Docker está completa y funcionando correctamente.

## 🎯 **Configuraciones Disponibles**

### 1. **🐛 Attach to Docker Container** ⭐ (RECOMENDADO)

- Se conecta a la aplicación ejecutándose en Docker
- Incluye PostgreSQL y Adminer
- **Puerto de debugging**: 5005
- **Uso**: Para debugging en entorno similar a producción

### 2. **🚀 Start Full Development Environment** ⭐ (AUTOMÁTICO)

- Configuración compuesta que inicia todo automáticamente
- Levanta Docker + PostgreSQL + Debugging
- **Uso**: Un solo clic para entorno completo

### 3. **💻 Debug Spring Boot App (Local)**

- Ejecuta localmente sin Docker
- Utiliza H2 en memoria
- **Uso**: Para debugging rápido sin dependencias

## 🎮 **Cómo usar el debugging**

### **Opción A: Entorno Completo Automático (RECOMENDADO)**

1. En VS Code: `Ctrl+Shift+D` → **"Start Full Development Environment"** → `F5`
2. ¡Listo! Todo se configura automáticamente

### **Opción B: Manual (si quieres más control)**

1. `Ctrl+Shift+P` → "Tasks: Run Task" → **"docker-debug-start"**
2. Esperar que arranque (unos 30 segundos)
3. En VS Code: `Ctrl+Shift+D` → **"Attach to Docker Container"** → `F5`

### **Opción C: Local rápido**

1. En VS Code: `Ctrl+Shift+D` → **"Debug Spring Boot App (Local)"** → `F5`

Este documento describe cómo usar VS Code para debuggear la aplicación Spring Boot ejecutándose en Docker.

## Configuraciones Disponibles

### 1. **Debug Spring Boot App (Local)**

- Ejecuta la aplicación localmente sin Docker
- Utiliza el perfil `dev`
- Carga variables del archivo `.env`
- **Uso**: Para debugging rápido sin dependencias externas

### 2. **Debug Spring Boot App (Test Profile)**

- Ejecuta la aplicación localmente con perfil de pruebas
- Utiliza base de datos H2 en memoria
- **Uso**: Para pruebas unitarias y de integración

### 3. **Attach to Docker Container** ⭐

- Se conecta a la aplicación ejecutándose en Docker
- Incluye base de datos PostgreSQL y Adminer
- **Uso**: Para debugging en entorno similar a producción

### 4. **Start Full Development Environment** ⭐

- Configuración compuesta que inicia todo el entorno
- Levanta automáticamente Docker con debugging habilitado
- **Uso**: Entorno completo de desarrollo

## Instrucciones de Uso

### Preparación Inicial

1. **Copiar variables de entorno**:

   ```bash
   cp .env.example .env
   ```

2. **Editar el archivo `.env`** con tus configuraciones:
   ```bash
   POSTGRES_DB=consentidos
   POSTGRES_USER=tu_usuario
   POSTGRES_PASSWORD=tu_password_segura
   # ... otros valores
   ```

### Debugging con Docker (Recomendado)

1. **Opción A**: Usar la configuración compuesta

   - Ir a `Run and Debug` (Ctrl+Shift+D)
   - Seleccionar "Start Full Development Environment"
   - Presionar F5

2. **Opción B**: Manualmente
   - Ejecutar tarea: `docker-compose-up` (Ctrl+Shift+P > Tasks: Run Task)
   - Ejecutar tarea: `docker-debug-start`
   - Ir a `Run and Debug`
   - Seleccionar "Attach to Docker Container"
   - Presionar F5

### Debugging Local

1. **Para desarrollo rápido**:

   - Seleccionar "Debug Spring Boot App (Local)"
   - Presionar F5

2. **Para pruebas**:
   - Seleccionar "Debug Spring Boot App (Test Profile)"
   - Presionar F5

## Puertos y Servicios

| Servicio           | Puerto | URL                      |
| ------------------ | ------ | ------------------------ |
| Spring Boot App    | 8080   | http://localhost:8080    |
| PostgreSQL         | 5432   | localhost:5432           |
| Adminer (DB Admin) | 8081   | http://localhost:8081    |
| Debug Port         | 5005   | Para attach del debugger |

## Tareas Disponibles

- `build`: Compila el proyecto con Gradle
- `test`: Ejecuta las pruebas
- `docker-compose-up`: Levanta BD y Adminer
- `docker-debug-start`: Levanta app con debugging
- `docker-compose-debug-down`: Detiene todo el entorno de debug

## Breakpoints y Debugging

1. **Colocar breakpoints**: Click en el margen izquierdo del editor
2. **Variables**: Panel izquierdo durante debugging
3. **Watch**: Agregar expresiones para monitorear
4. **Call Stack**: Ver la pila de llamadas
5. **Debug Console**: Evaluar expresiones

## Comandos Útiles

```bash
# Ver logs del contenedor
docker-compose -f docker-compose.debug.yml logs -f app-debug

# Detener todos los servicios
docker-compose -f docker-compose.debug.yml down

# Reconstruir imagen de debug
docker-compose -f docker-compose.debug.yml build app-debug

# Ver contenedores activos
docker ps
```

## Troubleshooting

### El debugger no se conecta

- Verificar que el puerto 5005 esté libre
- Comprobar que el contenedor esté ejecutándose: `docker ps`
- Revisar logs: `docker-compose -f docker-compose.debug.yml logs app-debug`

### Base de datos no conecta

- Verificar archivo `.env`
- Comprobar que PostgreSQL esté ejecutándose
- Usar Adminer para probar conexión: http://localhost:8081

### Cambios de código no se reflejan

- Reconstruir imagen: `docker-compose -f docker-compose.debug.yml build app-debug`
- Reiniciar contenedor: `docker-compose -f docker-compose.debug.yml restart app-debug`

## Archivos de Configuración

- `.vscode/launch.json`: Configuraciones de debugging
- `.vscode/tasks.json`: Tareas de build y Docker
- `Dockerfile.debug`: Imagen Docker con debugging habilitado
- `docker-compose.debug.yml`: Servicios para debugging
- `.env`: Variables de entorno (no incluido en Git)
