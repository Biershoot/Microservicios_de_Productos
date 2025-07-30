# Sistema de Microservicios con JWT y Roles

Sistema completo de microservicios implementando autenticación y autorización basada en JWT (JSON Web Tokens) con gestión de roles.

## 🎯 Problemática

### Contexto
En un sistema de e-commerce tradicional, todos los servicios (autenticación, productos, órdenes) estaban acoplados en una aplicación monolítica, lo que generaba:

- **Escalabilidad limitada**: No se podía escalar servicios individuales según la demanda
- **Mantenimiento complejo**: Cambios en un módulo afectaban toda la aplicación
- **Seguridad centralizada**: Un punto de falla comprometía todo el sistema
- **Despliegues lentos**: Actualizar un módulo requería desplegar toda la aplicación
- **Tecnologías acopladas**: No se podía usar la mejor tecnología para cada servicio

### Desafíos Específicos
1. **Autenticación distribuida**: ¿Cómo mantener sesiones consistentes entre microservicios?
2. **Autorización granular**: ¿Cómo implementar roles y permisos específicos por servicio?
3. **Comunicación segura**: ¿Cómo validar tokens JWT en cada microservicio?
4. **Service Discovery**: ¿Cómo encontrar y comunicarse entre microservicios?
5. **API Gateway**: ¿Cómo centralizar el acceso y la seguridad?

## 💡 Solución Implementada

### Arquitectura de Microservicios
Implementamos un sistema distribuido con 4 microservicios especializados:

#### 1. **Auth Service** (Puerto 8081)
- **Responsabilidad**: Autenticación y autorización centralizada
- **Funcionalidades**:
  - Registro de usuarios con roles
  - Login con validación de credenciales
  - Generación y validación de tokens JWT
  - Gestión de roles (USER, ADMIN)
- **Tecnologías**: Spring Boot, Spring Security, JWT, MySQL/H2

#### 2. **Product Service** (Puerto 8082)
- **Responsabilidad**: Gestión completa de productos
- **Funcionalidades**:
  - CRUD de productos
  - Búsqueda por nombre
  - Validación de stock
  - Protección con JWT y roles
- **Tecnologías**: Spring Boot, Spring Data JPA, Spring Security, MySQL/H2

#### 3. **Order Service** (Puerto 8083)
- **Responsabilidad**: Gestión de órdenes y pedidos
- **Funcionalidades**:
  - Creación de órdenes con múltiples items
  - Cálculo automático de totales
  - Gestión de estados de orden
  - Protección con JWT y roles
- **Tecnologías**: Spring Boot, Spring Data JPA, Spring Security, MySQL/H2

#### 4. **Gateway Service** (Puerto 8080)
- **Responsabilidad**: Punto de entrada único y enrutamiento
- **Funcionalidades**:
  - Enrutamiento inteligente a microservicios
  - Validación centralizada de JWT
  - Filtros de seguridad globales
  - Service discovery con Eureka
- **Tecnologías**: Spring Cloud Gateway, Spring Security, Eureka Client

### Seguridad JWT Implementada

#### Flujo de Autenticación
1. **Registro/Login**: Usuario se autentica en Auth Service
2. **Token JWT**: Auth Service genera token con claims de usuario y roles
3. **Validación**: Gateway valida token antes de enrutar requests
4. **Autorización**: Cada microservicio valida roles específicos
5. **Respuesta**: Usuario recibe respuesta del microservicio correspondiente

#### Roles y Permisos
- **USER**: Acceso a lectura de productos y creación de órdenes
- **ADMIN**: Acceso completo a todos los servicios (CRUD completo)

### Características Técnicas

#### Manejo de Excepciones
- **Excepciones personalizadas**: `ProductNotFoundException`, `OrderNotFoundException`, `UserAlreadyExistsException`
- **Global Exception Handler**: Manejo centralizado de errores con respuestas JSON estructuradas
- **Códigos HTTP apropiados**: 404 para no encontrado, 400 para validación, 500 para errores internos

#### Configuración Flexible
- **Perfiles de desarrollo**: H2 in-memory database para desarrollo local
- **Perfiles de producción**: MySQL para entornos de producción
- **Configuración externalizada**: Properties y YAML para diferentes entornos

#### Documentación API
- **Swagger/OpenAPI**: Documentación automática de endpoints
- **Ejemplos de uso**: Código curl y Postman para testing
- **Especificaciones claras**: Parámetros, respuestas y códigos de error

## 🚀 Características Principales

- ✅ **Autenticación JWT** centralizada y distribuida
- ✅ **Autorización basada en roles** (USER, ADMIN)
- ✅ **Service Discovery** con Eureka
- ✅ **API Gateway** con enrutamiento inteligente
- ✅ **Manejo de excepciones** personalizado y global
- ✅ **Documentación API** con Swagger
- ✅ **Configuración flexible** para desarrollo y producción
- ✅ **Logging detallado** para debugging
- ✅ **Health checks** para monitoreo
- ✅ **Docker support** para containerización

## 📋 Prerrequisitos

- Java 17+
- Maven 3.6+
- MySQL 8.0+ (opcional, H2 para desarrollo)
- Docker (opcional)

## ⚙️ Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone https://github.com/Biershoot/Microservicios_de_Productos.git
cd Microservicios_de_Productos
```

### 2. Configurar base de datos (opcional)
```sql
-- Para desarrollo, se usa H2 automáticamente
-- Para producción, crear las bases de datos:
CREATE DATABASE auth_db;
CREATE DATABASE product_service_db;
CREATE DATABASE order_service_db;
```

### 3. Compilar todos los servicios
```bash
# Compilar product-service (incluye todos los microservicios)
cd product-service
mvn clean install
```

### 4. Ejecutar los servicios

#### Opción A: Ejecutar individualmente
```bash
# Terminal 1 - Auth Service
cd auth-service
mvn spring-boot:run

# Terminal 2 - Product Service  
cd product-service
mvn spring-boot:run

# Terminal 3 - Order Service
cd order-service
mvn spring-boot:run

# Terminal 4 - Gateway Service
cd gateway-service
mvn spring-boot:run
```

#### Opción B: Con Docker Compose (próximamente)
```bash
docker-compose up -d
```

## 🔐 Endpoints y Uso

### 1. Registrar usuario
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "roles": ["USER"]
  }'
```

### 2. Login y obtener token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### 3. Crear producto (requiere ADMIN)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "name": "Laptop Gaming",
    "description": "Laptop para gaming de alto rendimiento",
    "price": 1299.99,
    "stock": 10
  }'
```

### 4. Obtener productos (requiere USER o ADMIN)
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer <token>"
```

### 5. Crear orden (requiere USER o ADMIN)
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "items": [
      {
        "productId": 1,
        "quantity": 2
      }
    ]
  }'
```

## 📊 Monitoreo y Health Checks

- **Auth Service**: `http://localhost:8081/api/auth/health`
- **Product Service**: `http://localhost:8082/api/products/health`
- **Order Service**: `http://localhost:8083/api/orders/health`
- **Gateway Service**: `http://localhost:8080/api/gateway/health`

## 🐳 Docker

### Construir imágenes
```bash
# Auth Service
cd auth-service
docker build -t auth-service .

# Product Service
cd product-service
docker build -t product-service .

# Order Service
cd order-service
docker build -t order-service .

# Gateway Service
cd gateway-service
docker build -t gateway-service .
```

### Ejecutar contenedores
```bash
docker run -p 8081:8081 auth-service
docker run -p 8082:8082 product-service
docker run -p 8083:8083 order-service
docker run -p 8080:8080 gateway-service
```

## 🔧 Configuración Avanzada

### Variables de Entorno
```bash
# JWT Configuration
export JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
export JWT_EXPIRATION=86400000

# Database Configuration
export DB_URL=jdbc:mysql://localhost:3306/
export DB_USERNAME=root
export DB_PASSWORD=password
```

### Perfiles de Spring
- **local**: H2 in-memory database
- **prod**: MySQL database
- **dev**: Configuración de desarrollo

## 🚨 Troubleshooting

### Error: "Service not found"
- Verificar que todos los servicios estén ejecutándose
- Verificar los puertos configurados
- Revisar logs de Eureka

### Error: "Unauthorized"
- Verificar que el token JWT sea válido
- Verificar que el token no haya expirado
- Verificar el formato: `Authorization: Bearer <token>`
- Verificar que el usuario tenga los roles necesarios

### Error: "Connection refused"
- Verificar que MySQL esté ejecutándose (si usa perfil prod)
- Verificar que los puertos no estén ocupados
- Revisar logs de cada servicio

## 📚 Tecnologías Utilizadas

- **Spring Boot**: Framework base para microservicios
- **Spring Security**: Autenticación y autorización
- **Spring Data JPA**: Persistencia de datos
- **Spring Cloud Gateway**: API Gateway
- **Spring Cloud Netflix Eureka**: Service Discovery
- **JWT (jjwt)**: Tokens de autenticación
- **MySQL/H2**: Bases de datos
- **Maven**: Gestión de dependencias
- **Lombok**: Reducción de boilerplate code
- **Swagger/OpenAPI**: Documentación de API

## 🤝 Contribución

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/NuevaFuncionalidad`)
3. Commit tus cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/NuevaFuncionalidad`)
5. Abrir un Pull Request

## 📞 Contacto

- **Desarrollador**: Alejandro Arango Calderón
- **Email**: alejodim27@gmail.com
- **GitHub**: [https://github.com/Biershoot]

---

**Nota**: Este proyecto es una implementación educativa de microservicios con JWT. Para uso en producción, se recomienda implementar medidas de seguridad adicionales como rate limiting, HTTPS, y monitoreo avanzado. 