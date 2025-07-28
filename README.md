# Microservicios - Arquitectura Completa

## Descripción

Este proyecto implementa una arquitectura completa de microservicios con Spring Cloud, incluyendo un Gateway como punto de entrada único, Service Discovery con Eureka, y un microservicio de productos.

## Arquitectura de Microservicios

### Componentes

1. **Gateway Service** - Puerto 8080
   - Punto de entrada único para todos los microservicios
   - Enruta peticiones usando Spring Cloud Gateway
   - URL: http://localhost:8080

2. **Discovery Server (Eureka)** - Puerto 8761
   - Servidor de descubrimiento para microservicios
   - Dashboard: http://localhost:8761

3. **Product Service** - Puerto 8081
   - Microservicio de gestión de productos
   - API REST completa
   - Se registra automáticamente en Eureka

4. **MySQL Database** - Puerto 3307
   - Base de datos persistente

## Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.5.4**
- **Spring Cloud Gateway**
- **Spring Cloud Netflix Eureka**
- **Spring Data JPA**
- **MySQL 8.0**
- **Docker & Docker Compose**
- **Swagger/OpenAPI 3**
- **Lombok**

## Estructura del Proyecto

```
microservices/
├── gateway-service/          # Spring Cloud Gateway
│   ├── src/main/java/
│   ├── src/main/resources/
│   ├── Dockerfile
│   └── pom.xml
├── discovery-server/         # Eureka Discovery Server
│   ├── src/main/java/
│   ├── src/main/resources/
│   ├── Dockerfile
│   └── pom.xml
└── product-service/          # Product Service
    ├── src/main/java/
    ├── src/main/resources/
    ├── Dockerfile
    ├── docker-compose.yml
    └── pom.xml
```

## Endpoints de la API

### A través del Gateway (Puerto 8080)

- `GET /api/products` - Obtener todos los productos
- `GET /api/products/{id}` - Obtener producto por ID
- `GET /api/products/name/{name}` - Obtener producto por nombre
- `POST /api/products` - Crear nuevo producto
- `PUT /api/products/{id}` - Actualizar producto
- `DELETE /api/products/{id}` - Eliminar producto
- `GET /api/products/in-stock` - Productos con stock disponible
- `GET /api/products/price-range?minPrice=X&maxPrice=Y` - Productos por rango de precio

### Directo al Product Service (Puerto 8081)

Los mismos endpoints están disponibles directamente en el product-service para desarrollo y debugging.

## Instalación y Ejecución

### Prerrequisitos
- Java 17
- Maven
- Docker & Docker Compose

### Ejecución con Docker Compose (Recomendado)

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/Biershoot/Microservicios_de_Productos.git
   cd product-service
   ```

2. **Construir y ejecutar todos los servicios:**
   ```bash
   docker-compose up --build
   ```

3. **Acceder a los servicios:**
   - **Gateway:** http://localhost:8080/api/products
   - **Eureka Dashboard:** http://localhost:8761
   - **Product API Directo:** http://localhost:8081/api/products
   - **Swagger UI:** http://localhost:8081/swagger-ui.html

### Ejecución Local

1. **Ejecutar Discovery Server:**
   ```bash
   cd ../discovery-server
   ./mvnw spring-boot:run
   ```

2. **Ejecutar Product Service:**
   ```bash
   cd ../product-service
   ./mvnw spring-boot:run
   ```

3. **Ejecutar Gateway Service:**
   ```bash
   cd ../gateway-service
   ./mvnw spring-boot:run
   ```

## Configuración

### Variables de Entorno

El gateway utiliza las siguientes variables de entorno:

- `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE` - URL del servidor Eureka

### Rutas del Gateway

El gateway está configurado para enrutar:

- **`/api/products/**`** → **`product-service`**
  - Usa Load Balancer (`lb://product-service`)
  - Descubre automáticamente las instancias en Eureka

## Gateway Service

### Características
- ✅ **Punto de entrada único** para todos los microservicios
- ✅ **Enrutamiento dinámico** basado en Service Discovery
- ✅ **Load Balancing** automático
- ✅ **Filtros y predicados** configurables
- ✅ **Integración con Eureka** para descubrimiento de servicios

### Configuración de Rutas

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
```

## Discovery Server (Eureka)

### Características
- ✅ Registro automático de microservicios
- ✅ Dashboard web para monitoreo
- ✅ Descubrimiento dinámico de servicios
- ✅ Health checks automáticos

### Dashboard
Accede al dashboard de Eureka en http://localhost:8761 para ver:
- Microservicios registrados
- Estado de salud de los servicios
- Información de instancias

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
- ✅ Service Discovery con Eureka
- ✅ API Gateway con Spring Cloud Gateway
- ✅ Arquitectura de microservicios completa

## Pruebas

### Probar el Gateway

```bash
# Obtener todos los productos a través del gateway
curl http://localhost:8080/api/products

# Crear un producto a través del gateway
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Product","description":"Test Description","price":99.99,"stock":10}'

# Obtener productos en stock
curl http://localhost:8080/api/products/in-stock
```

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
- Spring Cloud Team
- Docker Community
- MySQL Team 