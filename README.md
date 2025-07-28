# Product Service - Microservicio de Productos

## Descripción

Este es un microservicio desarrollado en Spring Boot para la gestión de productos. El servicio proporciona una API REST completa para operaciones CRUD de productos.

## Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.5.4**
- **Spring Data JPA**
- **MySQL 8.0**
- **Docker & Docker Compose**
- **Swagger/OpenAPI 3**
- **Lombok**

## Estructura del Proyecto

```
product-service/
├── src/main/java/com/alejandro/microservices/productservice/
│   ├── controller/
│   │   └── ProductController.java
│   ├── service/
│   │   └── ProductService.java
│   ├── model/
│   │   └── Product.java
│   ├── repository/
│   │   └── ProductRepository.java
│   ├── dto/
│   │   └── ProductDTO.java
│   └── config/
│       └── SwaggerConfig.java
├── src/main/resources/
│   └── application.properties
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## Endpoints de la API

### Productos
- `GET /api/products` - Obtener todos los productos
- `GET /api/products/{id}` - Obtener producto por ID
- `GET /api/products/name/{name}` - Obtener producto por nombre
- `POST /api/products` - Crear nuevo producto
- `PUT /api/products/{id}` - Actualizar producto
- `DELETE /api/products/{id}` - Eliminar producto

### Consultas Especiales
- `GET /api/products/in-stock` - Productos con stock disponible
- `GET /api/products/price-range?minPrice=X&maxPrice=Y` - Productos por rango de precio

### Monitoreo
- `GET /api/products/health` - Health check del servicio

## Instalación y Ejecución

### Prerrequisitos
- Java 17
- Maven
- Docker & Docker Compose

### Ejecución Local

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/Biershoot/Microservicios_de_Productos.git
   cd product-service
   ```

2. **Configurar base de datos MySQL:**
   - Crear base de datos: `product_service_db`
   - Usuario: `root` / Contraseña: `root`

3. **Ejecutar la aplicación:**
   ```bash
   ./mvnw spring-boot:run
   ```

### Ejecución con Docker

1. **Construir y ejecutar con Docker Compose:**
   ```bash
   docker-compose up --build
   ```

2. **Acceder a la aplicación:**
   - API: http://localhost:8081/api/products
   - Swagger UI: http://localhost:8081/swagger-ui.html

## Configuración

### Variables de Entorno

El servicio utiliza las siguientes variables de entorno:

- `SPRING_DATASOURCE_URL` - URL de conexión a la base de datos
- `SPRING_DATASOURCE_USERNAME` - Usuario de la base de datos
- `SPRING_DATASOURCE_PASSWORD` - Contraseña de la base de datos
- `SPRING_JPA_HIBERNATE_DDL_AUTO` - Configuración de Hibernate
- `SPRING_JPA_SHOW_SQL` - Mostrar consultas SQL
- `SPRING_APPLICATION_NAME` - Nombre de la aplicación

### Base de Datos

El servicio está configurado para trabajar con MySQL 8.0. La tabla `products` se crea automáticamente con los siguientes campos:

- `id` (Long, Primary Key)
- `name` (String, Unique)
- `description` (String)
- `price` (BigDecimal)
- `stock` (Integer)
- `created_at` (LocalDateTime)
- `updated_at` (LocalDateTime)

## Documentación de la API

La documentación de la API está disponible a través de Swagger UI en:
http://localhost:8081/swagger-ui.html

## Desarrollo

### Estructura de Clases

- **Product**: Entidad JPA que representa un producto
- **ProductDTO**: Objeto de transferencia de datos
- **ProductRepository**: Interfaz de acceso a datos
- **ProductService**: Lógica de negocio
- **ProductController**: Controlador REST

### Características

- ✅ Operaciones CRUD completas
- ✅ Validación de datos
- ✅ Manejo de errores
- ✅ Documentación con Swagger
- ✅ Configuración para microservicios
- ✅ Containerización con Docker
- ✅ Base de datos persistente

## Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## Autor

**Alejandro** - [GitHub](https://github.com/Biershoot)

## Agradecimientos

- Spring Boot Team
- Docker Community
- MySQL Team 