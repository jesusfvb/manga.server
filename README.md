# Manga Cómoda - Server


**Spanish version**: See [README_ES.md](README_ES.md) for the project description in Spanish.

---


Backend server developed with Spring Boot that manages manga information, automatically tracks new chapters, and provides a REST API to query manga, chapter, and image data.

## Description

This project is the server component of **Manga Cómoda**, an application that allows managing and tracking mangas from different sources. The server uses web scraping techniques to obtain updated manga information and stores data in MongoDB.

### Main Features

- **Automatic new chapter tracking**: The system periodically checks for new chapters available for registered mangas
- **Web scraping**: Extracts manga information from specialized websites (currently supports LeerCapitulo)
- **REST API**: Endpoints to search mangas, get chapters, and access images
- **Smart search**: Searches mangas both in the local database and external websites
- **Persistent storage**: Uses MongoDB to store manga, chapter, and image information
- **Asynchronous processing**: Uses asynchronous execution for scraping and update tasks

### Technologies Used

- **Spring Boot 3.5.4**: Main framework
- **Java 21**: Programming language
- **MongoDB**: NoSQL database
- **Playwright**: For browser automation and advanced scraping
- **JSoup**: For HTML parsing
- **MapStruct**: For DTO object mapping
- **Lombok**: To reduce boilerplate code

### Project Structure

```
src/main/java/com/manga/server/
├── core/                    # Main configurations and components
│   ├── browser/            # Browser management (Playwright)
│   ├── config/             # Spring configurations
│   └── filtres/            # Application filters
├── features/
│   ├── chapter/            # Chapter and image management
│   ├── manga/              # Manga management
│   └── scrapper/           # Web scraping services
└── ServerApplication.java  # Main class
```

### Main Endpoints

- `GET /` - Gets the list of mangas with new chapters
- `GET /search?query={search}` - Searches mangas by name
- `GET /ids?ids={id1,id2,...}` - Gets mangas by their IDs
- `GET /chapter?mangaId={id}` - Gets chapters of a manga
- `GET /chapter/img?chapterId={id}` - Gets images of a chapter

### Requirements

- Java 21 or higher
- Maven 3.6 or higher
- MongoDB (running)

### Configuration

1. Clone the repository
2. Configure MongoDB in `application.properties`
3. Run `mvn clean install`
4. Start the application with `mvn spring-boot:run`

### Development

The project uses:
- JUnit for unit testing
- MapStruct for automatic DTO mapping
- Lombok to reduce repetitive code

