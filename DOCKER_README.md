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

## Uso

### Construir y levantar los servicios

```bash
docker-compose up -d --build
```

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

Puedes personalizar la configuración modificando las variables de entorno en `docker-compose.yml`:

- `SPRING_DATA_MONGODB_URI`: URI de conexión a MongoDB
- `SPRING_APPLICATION_NAME`: Nombre de la aplicación
- `SPRINGDOC_SWAGGER_UI_PATH`: Ruta de Swagger UI
- `SPRING_PROFILES_ACTIVE`: Perfil de Spring activo (por defecto: `docker`)

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

- Cambiar las credenciales por defecto de MongoDB
- Usar variables de entorno para credenciales (no hardcodear en docker-compose.yml)
- Considerar usar secrets de Docker Compose para credenciales
- Implementar HTTPS/TLS para la API
- Configurar firewall para limitar acceso al puerto 8080

## Arquitectura del Dockerfile

El Dockerfile utiliza un build multi-stage:

1. **Stage 1 (Build)**: 
   - Imagen base: `maven:3.9.6-eclipse-temurin-21`
   - Compila el proyecto y genera el JAR
   - Optimización: cachea dependencias antes de copiar el código fuente

2. **Stage 2 (Runtime)**:
   - Imagen base: `mcr.microsoft.com/playwright/java:v1.45.0-jammy`
   - Incluye Java 21 JRE y Playwright para scraping web
   - Ejecuta la aplicación como usuario no-root (`spring`)

## Acceso

- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/api/docs
- **Health Check**: http://localhost:8080/actuator/health
- **MongoDB**: Solo accesible desde dentro de la red Docker (no expuesto por seguridad)

> **Nota de Seguridad**: MongoDB no está expuesto al host para proteger la base de datos. Si necesitas acceder a MongoDB para desarrollo/debugging, puedes usar:
> ```bash
> docker-compose exec mongodb mongosh -u admin -p admin123 --authenticationDatabase admin
> ```

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
