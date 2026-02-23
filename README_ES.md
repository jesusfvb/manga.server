# Manga Cómoda - Servidor


**Versión en inglés**: Ver [README.md](README.md) para la descripción del proyecto en inglés.

---


Servidor backend desarrollado con Spring Boot que gestiona la información de mangas, rastrea nuevos capítulos automáticamente y proporciona una API REST para consultar datos de mangas, capítulos e imágenes.

## Descripción

Este proyecto es el componente servidor de **Manga Cómoda**, una aplicación que permite gestionar y rastrear mangas desde diferentes fuentes. El servidor utiliza técnicas de web scraping para obtener información actualizada de mangas y almacena los datos en MongoDB.

### Características principales

- **Rastreo automático de nuevos capítulos**: El sistema verifica periódicamente si hay nuevos capítulos disponibles para los mangas registrados
- **Web scraping**: Extrae información de mangas desde sitios web especializados (actualmente soporta LeerCapitulo)
- **API REST**: Endpoints para buscar mangas, obtener capítulos y acceder a imágenes
- **Búsqueda inteligente**: Busca mangas tanto en la base de datos local como en sitios web externos
- **Almacenamiento persistente**: Utiliza MongoDB para almacenar información de mangas, capítulos e imágenes
- **Procesamiento asíncrono**: Utiliza ejecución asíncrona para tareas de scraping y actualización

### Tecnologías utilizadas

- **Spring Boot 3.5.4**: Framework principal
- **Java 21**: Lenguaje de programación
- **MongoDB**: Base de datos NoSQL
- **Playwright**: Para automatización de navegadores y scraping avanzado
- **JSoup**: Para parsing de HTML
- **MapStruct**: Para mapeo de objetos DTO
- **Lombok**: Para reducir código boilerplate

### Estructura del proyecto

```
src/main/java/com/manga/server/
├── core/                    # Configuraciones y componentes principales
│   ├── browser/            # Gestión de navegadores (Playwright)
│   ├── config/             # Configuraciones de Spring
│   └── filtres/            # Filtros de aplicación
├── features/
│   ├── chapter/            # Gestión de capítulos e imágenes
│   ├── manga/              # Gestión de mangas
│   └── scrapper/           # Servicios de web scraping
└── ServerApplication.java  # Clase principal
```

### Endpoints principales

- `GET /` - Obtiene la lista de mangas con nuevos capítulos
- `GET /search?query={busqueda}` - Busca mangas por nombre
- `GET /ids?ids={id1,id2,...}` - Obtiene mangas por sus IDs
- `GET /chapter?mangaId={id}` - Obtiene los capítulos de un manga
- `GET /chapter/img?chapterId={id}` - Obtiene las imágenes de un capítulo

### Requisitos

- Java 21 o superior
- Maven 3.6 o superior
- MongoDB (en ejecución)

### Configuración

1. Clona el repositorio
2. Configura MongoDB en `application.properties`
3. Ejecuta `mvn clean install`
4. Inicia la aplicación con `mvn spring-boot:run`

### Desarrollo

El proyecto utiliza:
- JUnit para pruebas unitarias
- MapStruct para mapeo automático de DTOs
- Lombok para reducir código repetitivo
