# Scripts de soporte para probar auth localmente

## 1) Generar llaves RSA para JWT
Actualiza `AUTH_TOKEN_PRIVATE_KEY_PEM` y `AUTH_TOKEN_PUBLIC_KEY_PEM` dentro de `.env`.

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\generate-rsa-keys.ps1
```

> Requiere un JDK 17 accesible por `java`/`javac` o por `JAVA_HOME`.

## 2) Generar y servir HTML de Google Identity
Crea `scripts/.generated/google-login.html` usando `AUTH_GOOGLE_CLIENT_IDS` del `.env` y lo sirve con Python.

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\serve-google-identity.ps1 -Port 5500
```

Luego abre:
- `http://localhost:5500/google-login.html`

Antes de eso, agrega en Google Cloud Console este origin autorizado:
- `http://localhost:5500`

## 3) Registrar el usuario de prueba en `.env`
Una vez que copies el `id_token` desde el navegador, ejecuta:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\register-google-user.ps1 -IdToken 'PEGA_AQUI_EL_ID_TOKEN'
```

Esto completa automáticamente:
- `AUTH_USER_0_GOOGLE_SUB`
- `AUTH_USER_0_EMAIL`
- `AUTH_USER_0_DISPLAY_NAME`
- `AUTH_USER_0_ACTIVE=true`

## 4) Levantar el backend en IntelliJ
En la configuración de `ConsentidosApplication`:
- Usa perfil `local`
- Carga las variables del `.env` manualmente o con plugin tipo EnvFile
- Presiona `Debug`

## 5) Probar login
```bash
curl --location 'http://localhost:8080/api/auth/login' \
--header 'Content-Type: application/json' \
--data '{
  "idToken": "PEGA_AQUI_EL_ID_TOKEN"
}'
```

