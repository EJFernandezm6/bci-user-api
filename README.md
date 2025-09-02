# BCI User API

API REST para registro y consulta de usuarios.  
Tecnologías principales: **Java 17**, **Spring Boot 3**, **Spring Security (JWT)**, **H2**, **springdoc-openapi (Swagger)**.

## Requisitos

- **Java JDK 17** o superior  
  `java -version` → debe mostrar 17+  
- **Apache Maven 3.9+**  
  `mvn -v`  
- **Git** para clonar el repositorio
- **Puerto libre 8080** (o especificar otro con `--server.port`)

> **Nota:** No se requiere instalar una base de datos externa; se usa **H2 en memoria**.

---

## Descargar el proyecto

```bash
git clone https://github.com/EJFernandezm6/bci-user-api.git
cd bci-user-api
```

---

## Compilar y generar JAR

```bash
mvn clean package
```

El artefacto se genera en:  
```
target/bci-user-api-0.0.1-SNAPSHOT.jar
```

## Ejecutar el JAR

### Ejecución (puerto 8080)
```bash
java -jar target/bci-user-api-0.0.1-SNAPSHOT.jar
```

## Probar la API

### Swagger UI
- Abrir: `http://localhost:8080/swagger-ui/index.html`

### 1) Crear usuario (no requiere token)

**Swagger UI**
- Ingresa a **POST** `/api/v1/users` y click en **Try it out**.
- En **Request body** ingresar el JSON:
```json
{
  "name":"Edu Fernandez",
  "email":"edu@fernandez.cl",
  "password":"Bci2025123",
  "phones":[
    {
      "number":"1234567",
      "citycode":"1",
      "contrycode":"57"
    }
  ]
}
```
- Dar click en **Execute**. 
- Respuesta esperada: **201 Created** con JSON:
```json
{
  "id": "7d6cd32e-6da0-4121-8616-9887c86927a4",
  "created": "2025-09-02T01:35:26.753095500Z",
  "modified": "2025-09-02T01:35:26.753095500Z",
  "lastLogin": "2025-09-02T01:35:26.753095500Z",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlZHVAZmVybmFuZGV6LmNsIiwiaWF0IjoxNzU2Nzc2OTI2LCJleHAiOjE3NTY3ODA1MjZ9.wo6rE82NM5nZtiOGaNjyGWFxQV_6QOsA_Oc1yZ7X8vI",
  "active": true
}
```

> Guarda el `id` y el `token`.

### 2) Consultar usuario (requiere token)

**Swagger UI**  
- Clic **Authorize** (arriba a la derecha)  
- Selecciona `bearerAuth` → pega **solo** el token (sin escribir “Bearer ”)  
- Ejecuta **GET** `/api/v1/users/{id}` con el `id` devuelto.

### H2 Console (dev)
- `http://localhost:8080/h2-console`  
- JDBC URL: `jdbc:h2:mem:bci`  
- User: `sa` | Password: *(vacío)*

---

## Estructura de seguridad

- **POST** `/api/v1/users` → **público**  
- **GET** `/api/v1/users/{id}` → **protegido** con **JWT**  
  - El `JwtAuthFilter` valida firma/expiración y que el token coincida con el persistido.

---

## Tests

- **Unit tests** con JUnit 5 y Mockito (`mvn test`).  
- Tests del controller usan `@WebMvcTest` + `MockMvc`.  

---

### Diagrama de secuencia de registro
```mermaid
---
config:
      theme: redux
---
flowchart TD
        C[Cliente] -->|POST /api/v1/users| API[UserController]
        API --> SVC[UserService]
        SVC -->|existsByEmail| Repo[UserRepository]
        SVC -->|JWT| Jwt[JwtService]
        SVC -->|save| Repo
        SVC -->|findByEmail| Repo
        Repo --> DB[(H2 + JPA)]
