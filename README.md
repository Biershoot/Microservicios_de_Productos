# Gateway Service

API Gateway para el sistema de microservicios con autenticación JWT.

## 🚀 Características

- **Enrutamiento inteligente** a microservicios
- **Autenticación JWT** centralizada
- **Service Discovery** con Eureka
- **Filtros de seguridad** globales
- **Logging** detallado de requests

## 📋 Prerrequisitos

- Java 17+
- Maven 3.6+
- Eureka Server (puerto 8761)
- Auth Service (puerto 8081)
- Product Service (puerto 8082)
- Order Service (puerto 8083)

## ⚙️ Configuración

### application.yml
```yaml
server:
  port: 8080

spring:
  application:
    name: gateway-service
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
      routes:
        - id: auth-service
          uri: lb://auth-service
          predicates:
            - Path=/api/auth/**
        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
```

## 🔐 Seguridad

### Rutas Públicas (sin autenticación)
- `/api/auth/register` - Registro de usuarios
- `/api/auth/login` - Login de usuarios
- `/api/auth/health` - Health check del auth service
- `/actuator/health` - Health check del gateway
- `/actuator/info` - Información del gateway

### Rutas Protegidas (requieren JWT)
- `/api/products/**` - Todos los endpoints de productos
- `/api/orders/**` - Todos los endpoints de órdenes
- `/api/gateway/**` - Endpoints del gateway

## 🛠️ Instalación y Ejecución

### 1. Clonar el repositorio
```bash
git clone <repository-url>
cd gateway-service
```

### 2. Compilar el proyecto
```bash
mvn clean install
```

### 3. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

### 4. Verificar que esté funcionando
```bash
curl http://localhost:8080/api/gateway/health
```

## 📡 Endpoints

### Gateway Health
- `GET /api/gateway/health` - Estado del gateway
- `GET /api/gateway/info` - Información del gateway

### Enrutamiento a Microservicios
- `GET /api/auth/**` → Auth Service (puerto 8081)
- `GET /api/products/**` → Product Service (puerto 8082)
- `GET /api/orders/**` → Order Service (puerto 8083)

## 🔄 Flujo de Autenticación

1. **Usuario se registra/login** en `/api/auth/register` o `/api/auth/login`
2. **Recibe token JWT** del auth-service
3. **Usa el token** en header `Authorization: Bearer <token>`
4. **Gateway valida** el token antes de enrutar
5. **Microservicio recibe** la request con el token validado

## 📝 Ejemplo de Uso

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

### 3. Usar token para acceder a productos
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer <token-from-login>"
```

## 🐳 Docker

### Construir imagen
```bash
docker build -t gateway-service .
```

### Ejecutar contenedor
```bash
docker run -p 8080:8080 gateway-service
```

## 📊 Monitoreo

- **Health Check**: `http://localhost:8080/actuator/health`
- **Info**: `http://localhost:8080/actuator/info`
- **Logs**: Configurados en `application.yml`

## 🔧 Configuración Avanzada

### Variables de Entorno
```bash
export JWT_SECRET=mySecretKey123456789012345678901234567890123456789012345678901234567890
export JWT_EXPIRATION=86400000
```

### Configuración de Eureka
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

## 🚨 Troubleshooting

### Error: "Service not found"
- Verificar que Eureka Server esté ejecutándose
- Verificar que los microservicios estén registrados

### Error: "Unauthorized"
- Verificar que el token JWT sea válido
- Verificar que el token no haya expirado
- Verificar el formato: `Authorization: Bearer <token>`

### Error: "Connection refused"
- Verificar que todos los servicios estén ejecutándose
- Verificar los puertos configurados

## 📚 Dependencias Principales

- **Spring Cloud Gateway**: Enrutamiento y filtros
- **Spring Cloud Netflix Eureka Client**: Service discovery
- **JWT (jjwt)**: Validación de tokens
- **Lombok**: Reducción de boilerplate code

## 🤝 Contribución

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles. 