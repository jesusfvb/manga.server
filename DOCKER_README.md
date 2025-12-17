# Docker Compose - Manga Server

Este documento describe cómo desplegar el servidor Manga usando Docker Compose.

## Requisitos

- Docker 20.10 o superior
- Docker Compose 2.0 o superior

## Estructura

El `docker-compose.yml` incluye:

- **MongoDB 7.0**: Base de datos MongoDB
- **Server**: Aplicación Spring Boot con Java 21

## Configuración

### MongoDB

- **Puerto**: 27017 (solo accesible desde la red interna de Docker)
- **Usuario**: admin
- **Contraseña**: admin123
- **Base de datos**: manga_db
- **Volúmenes**: Los datos se persisten en volúmenes Docker
- **Seguridad**: El puerto NO está expuesto al host por seguridad. Solo es accesible desde otros contenedores en la red `manga_network`

### Servidor

- **Puerto**: 8080
- **Java**: 21 (Eclipse Temurin)
- **Healthcheck**: `/actuator/health`
- **Swagger UI**: `/api/docs`
- **Perfil activo**: `docker`
- **Playwright**: Incluido para scraping web

## Entornos

Este proyecto incluye dos configuraciones Docker:

- **Producción**: `docker-compose.yml` y `Dockerfile` (optimizado, sin hot reload)
- **Desarrollo**: `docker-compose.dev.yml` y `Dockerfile.dev` (con hot reload y debugging)

## Uso

### Entorno de Producción

#### Construir y levantar los servicios

```bash
# Con archivo .env.prod explícito
docker-compose --env-file .env.prod up -d --build

# O si usas .env por defecto
docker-compose up -d --build
```

### Entorno de Desarrollo

El entorno de desarrollo incluye:
- ✅ **Hot Reload**: Los cambios en el código se reflejan automáticamente
- ✅ **MongoDB expuesto**: Puerto 27017 disponible para herramientas de desarrollo
- ✅ **Debugging remoto**: Puerto 5005 para conectar el debugger de tu IDE
- ✅ **Logging detallado**: Nivel DEBUG activado
- ✅ **Volúmenes montados**: Código fuente montado para cambios en tiempo real

#### Construir y levantar los servicios de desarrollo

```bash
# Con archivo .env.dev explícito (recomendado)
docker-compose -f docker-compose.dev.yml --env-file .env.dev up -d --build

# O si usas .env por defecto
docker-compose -f docker-compose.dev.yml up -d --build
```

#### Ver logs en desarrollo

```bash
# Todos los servicios
docker-compose -f docker-compose.dev.yml logs -f

# Solo el servidor (útil para ver hot reload)
docker-compose -f docker-compose.dev.yml logs -f server
```

#### Detener servicios de desarrollo

```bash
docker-compose -f docker-compose.dev.yml down
```

#### Configurar debugging remoto en tu IDE

1. El puerto 5005 está expuesto para debugging remoto
2. Configura tu IDE para conectarse a `localhost:5005`
3. Ejemplo para IntelliJ IDEA:
   - Run → Edit Configurations
   - Add New → Remote JVM Debug
   - Host: `localhost`, Port: `5005`
   - Debug mode: `Attach`

#### Hot Reload

Con Spring Boot DevTools activado:
- Los cambios en clases Java se recargan automáticamente
- Los cambios en `application.properties` requieren reinicio manual
- Para reiniciar manualmente: `docker-compose -f docker-compose.dev.yml restart server`

### Ver logs

```bash
# Todos los servicios
docker-compose logs -f

# Solo el servidor
docker-compose logs -f server

# Solo MongoDB
docker-compose logs -f mongodb
```

### Detener los servicios

```bash
docker-compose down
```

### Detener y eliminar volúmenes (⚠️ Esto elimina los datos de MongoDB)

```bash
docker-compose down -v
```

### Reconstruir solo el servidor

```bash
docker-compose up -d --build server
```

## Variables de Entorno

### Archivos .env

Para mejorar la seguridad, todas las configuraciones sensibles se gestionan a través de archivos `.env` separados por entorno.

**⚠️ IMPORTANTE**: Los archivos `.env.dev` y `.env.prod` contienen credenciales y no deben subirse al repositorio. Están incluidos en `.gitignore`.

#### Configuración inicial

**Para Desarrollo:**
```bash
cp .env.example .env.dev
# Edita .env.dev si necesitas cambiar alguna configuración
```

**Para Producción:**
```bash
cp .env.example .env.prod
# ⚠️ IMPORTANTE: Edita .env.prod y cambia las credenciales:
# - MONGO_ROOT_PASSWORD: Usa una contraseña segura
# - SPRING_PROFILES_ACTIVE: Asegúrate de que sea "docker"
# - SPRING_DEVTOOLS_RESTART_ENABLED: Debe ser "false"
```

#### Uso de los archivos .env

**Desarrollo:**
```bash
docker-compose -f docker-compose.dev.yml --env-file .env.dev up
```

**Producción:**
```bash
docker-compose --env-file .env.prod up
# O si usas .env por defecto:
docker-compose up
```

#### Variables disponibles

**MongoDB:**
- `MONGO_ROOT_USERNAME`: Usuario root de MongoDB (default: `admin`)
- `MONGO_ROOT_PASSWORD`: Contraseña root de MongoDB (default: `admin123`)
- `MONGO_DATABASE`: Nombre de la base de datos (default: `manga_db`)
- `MONGO_PORT`: Puerto de MongoDB (default: `27017`)
- `MONGO_SERVICE_NAME`: Nombre del servicio MongoDB en Docker (default: `mongodb`)

**Servidor Spring Boot:**
- `SERVER_PORT`: Puerto del servidor (default: `8080`)
- `SPRING_APPLICATION_NAME`: Nombre de la aplicación (default: `server`)
- `SPRINGDOC_SWAGGER_UI_PATH`: Ruta de Swagger UI (default: `/api/docs`)
- `SPRING_PROFILES_ACTIVE`: Perfil de Spring activo (default: `docker`)

**Desarrollo:**
- `DEBUG_PORT`: Puerto para debugging remoto JDWP (default: `5005`)
- `SPRING_DEVTOOLS_RESTART_ENABLED`: Habilitar hot reload (default: `true`)
- `SPRING_DEVTOOLS_LIVERELOAD_ENABLED`: Habilitar live reload (default: `true`)

#### Diferencias entre .env.dev y .env.prod

| Configuración | Desarrollo (.env.dev) | Producción (.env.prod) |
|--------------|----------------------|------------------------|
| `SPRING_PROFILES_ACTIVE` | `dev` | `docker` |
| `SPRING_DEVTOOLS_RESTART_ENABLED` | `true` | `false` |
| `SPRING_DEVTOOLS_LIVERELOAD_ENABLED` | `true` | `false` |
| `MONGO_ROOT_PASSWORD` | `admin123` (desarrollo) | **Debe cambiarse** |
| MongoDB expuesto | Sí (puerto 27017) | No (solo interno) |

#### Valores por defecto

Si no especificas `--env-file`, Docker Compose buscará un archivo `.env` en el mismo directorio. Si no existe, se usarán los valores por defecto definidos en los archivos docker-compose con la sintaxis `${VARIABLE:-default}`.

## Seguridad

### Puertos Expuestos

- **Puerto 8080 (Server)**: ✅ Expuesto - Necesario para acceder a la API
- **Puerto 27017 (MongoDB)**: ❌ NO expuesto - Solo accesible desde la red interna de Docker

### Medidas de Seguridad Implementadas

1. **MongoDB no expuesto**: La base de datos solo es accesible desde otros contenedores en la red Docker, no desde el host
2. **Usuario no-root**: La aplicación se ejecuta como usuario `spring` (no root) en el contenedor
3. **Red aislada**: Los servicios se comunican a través de una red Docker privada (`manga_network`)
4. **Healthchecks**: Ambos servicios tienen healthchecks configurados para monitoreo

### Recomendaciones para Producción

- ✅ **Usar archivo .env.prod**: Las credenciales ya están externalizadas
- ⚠️ **Cambiar credenciales por defecto**: Edita `.env.prod` y cambia `MONGO_ROOT_PASSWORD` antes de desplegar
- 🔒 **No subir .env al repositorio**: `.env.dev` y `.env.prod` están en `.gitignore`
- 🔐 **Usar secrets en producción**: Para entornos críticos, considera usar Docker Secrets o un gestor de secretos (HashiCorp Vault, AWS Secrets Manager, etc.)
- 🔒 **Implementar HTTPS/TLS**: Para la API en producción (usar reverse proxy como Nginx o Traefik)
- 🛡️ **Configurar firewall**: Limitar acceso al puerto 8080 solo a IPs autorizadas
- 🔑 **Rotar credenciales**: Cambiar contraseñas regularmente
- 📝 **Revisar .env.prod**: Asegúrate de que `SPRING_DEVTOOLS_RESTART_ENABLED=false` en producción

## Arquitectura de los Dockerfiles

### Dockerfile (Producción)

El Dockerfile de producción utiliza un build multi-stage:

1. **Stage 1 (Build)**: 
   - Imagen base: `maven:3.9.6-eclipse-temurin-21`
   - Compila el proyecto y genera el JAR
   - Optimización: cachea dependencias antes de copiar el código fuente

2. **Stage 2 (Runtime)**:
   - Imagen base: `mcr.microsoft.com/playwright/java:v1.45.0-jammy`
   - Incluye Java 21 JRE y Playwright para scraping web
   - Ejecuta la aplicación como usuario no-root (`spring`)
   - Tamaño optimizado: solo incluye el JAR compilado

### Dockerfile.dev (Desarrollo)

El Dockerfile de desarrollo está optimizado para desarrollo activo:

- **Imagen base**: `maven:3.9.6-eclipse-temurin-21` (incluye Maven y JDK completo)
- **Herramientas**: curl, vim para debugging
- **Playwright**: Dependencias del navegador instaladas
- **Hot Reload**: Ejecuta `mvn spring-boot:run` en lugar de JAR precompilado
- **Debugging**: Puerto 5005 expuesto para JDWP
- **Volúmenes**: Código fuente montado para cambios en tiempo real

## Acceso

### Producción

- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/api/docs
- **Health Check**: http://localhost:8080/actuator/health
- **MongoDB**: Solo accesible desde dentro de la red Docker (no expuesto por seguridad)

### Desarrollo

- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/api/docs
- **Health Check**: http://localhost:8080/actuator/health
- **MongoDB**: http://localhost:27017 (expuesto para herramientas de desarrollo)
- **Debugging**: localhost:5005 (JDWP)

> **Nota de Seguridad**: En producción, MongoDB no está expuesto al host para proteger la base de datos. En desarrollo, está expuesto para facilitar el uso de herramientas como MongoDB Compass o Studio 3T.

## Troubleshooting

### El servidor no se conecta a MongoDB

Verifica que MongoDB esté saludable:
```bash
docker-compose ps
```

Revisa los logs:
```bash
docker-compose logs mongodb
docker-compose logs server
```

### Error de compilación: "release version 21 not supported"

Si encuentras este error, verifica que el Dockerfile esté usando la imagen correcta de Maven con Java 21:
- Build stage debe usar: `maven:3.9.6-eclipse-temurin-21`
- Runtime stage debe instalar: `openjdk-21-jre-headless`

### Reconstruir desde cero

```bash
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

### Verificar versión de Java en el contenedor

```bash
docker-compose exec server java -version
```

Debería mostrar Java 21.

### Acceder a MongoDB desde el host (solo para desarrollo/debugging)

Si necesitas acceder a MongoDB desde fuera del contenedor para desarrollo, puedes usar:

```bash
docker-compose exec mongodb mongosh -u admin -p admin123 --authenticationDatabase admin
```

O temporalmente exponer el puerto editando `docker-compose.yml` (no recomendado para producción).
