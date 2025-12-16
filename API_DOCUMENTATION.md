# NakMuay-API: Documentación REST API

## Descripción General

**NakMuay-API** es una REST API desarrollada con Spring Boot para la gestión de una academia de Muay Thai. Proporciona endpoints para administrar planes, membresías, horarios de entrenamiento y usuarios.

## Tecnologías Utilizadas

- **Framework**: Spring Boot 3.5.6
- **Lenguaje**: Java 17
- **Base de Datos**: H2 (en memoria)
- **Documentación API**: Swagger/OpenAPI
- **Seguridad**: Spring Security con CORS configurado

## Configuración CORS

La API permite requests desde los siguientes orígenes:
- `http://localhost:8100` (Ionic)
- `http://localhost:4200` (Angular)
- `http://localhost:8080` (desarrollo local)

## Endpoints Disponibles

### 📋 Planes de Entrenamiento

#### Obtener todos los planes
```
GET /api/planes
```

#### Obtener plan por ID
```
GET /api/planes/{id}
```

#### Crear nuevo plan
```
POST /api/planes
Content-Type: application/json

{
  "nombre": "Plan Principiante",
  "descripcion": "Plan para iniciar en Muay Thai",
  "precio": 59.99,
  "duracionMeses": 1,
  "activo": true
}
```

#### Actualizar plan
```
PUT /api/planes/{id}
Content-Type: application/json

{
  "nombre": "Plan Principiante Actualizado",
  "descripcion": "Descripción actualizada",
  "precio": 69.99,
  "duracionMeses": 1,
  "activo": true
}
```

#### Eliminar plan
```
DELETE /api/planes/{id}
```

---

### 👥 Usuarios

#### Obtener todos los usuarios
```
GET /api/usuarios
```

#### Obtener usuario por ID
```
GET /api/usuarios/{id}
```

#### Obtener usuario por email
```
GET /api/usuarios/email/{email}
```

---

### 💳 Membresías

#### Obtener todas las membresías
```
GET /api/membresias
```

#### Obtener membresía activa del usuario
```
GET /api/membresias/usuario/{usuarioId}
```

#### Obtener membresía por ID
```
GET /api/membresias/{id}
```

#### Crear nueva membresía
```
POST /api/membresias
Content-Type: application/json

{
  "usuarioId": 1,
  "planId": 1,
  "fechaInicio": "2025-12-16",
  "fechaFin": "2026-01-16",
  "activa": true
}
```

#### Actualizar membresía
```
PUT /api/membresias/{id}
Content-Type: application/json

{
  "usuarioId": 1,
  "planId": 1,
  "fechaInicio": "2025-12-16",
  "fechaFin": "2026-01-16",
  "activa": true
}
```

#### Eliminar membresía
```
DELETE /api/membresias/{id}
```

---

### ⏰ Horarios de Entrenamiento

#### Obtener todos los horarios
```
GET /api/horarios
```

#### Obtener horarios de una membresía
```
GET /api/horarios/membresia/{membresiaId}
```

#### Obtener horario por ID
```
GET /api/horarios/{id}
```

#### Crear nuevo horario
```
POST /api/horarios
Content-Type: application/json

{
  "membresiaId": 1,
  "diaSemana": "Lunes",
  "horaInicio": "09:00",
  "horaFin": "10:00",
  "activo": true
}
```

#### Actualizar horario
```
PUT /api/horarios/{id}
Content-Type: application/json

{
  "membresiaId": 1,
  "diaSemana": "Martes",
  "horaInicio": "17:00",
  "horaFin": "18:00",
  "activo": true
}
```

#### Eliminar horario
```
DELETE /api/horarios/{id}
```

---

## Documentación Interactiva

Accede a la documentación interactiva de Swagger/OpenAPI en:

```
http://localhost:8080/swagger-ui.html
```

Esta interfaz te permite explorar todos los endpoints y hacer requests de prueba directamente.

---

## Modelos de Datos

### PlanDTO
```json
{
  "id": 1,
  "nombre": "Plan Principiante",
  "descripcion": "Descripción del plan",
  "precio": 59.99,
  "duracionMeses": 1,
  "activo": true
}
```

### UsuarioDTO
```json
{
  "id": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan@example.com",
  "rut": "12.345.678-9",
  "rol": "ALUMNO"
}
```

### MembresiaDTO
```json
{
  "id": 1,
  "usuarioId": 1,
  "usuarioNombre": "Juan Pérez",
  "usuarioEmail": "juan@example.com",
  "planId": 1,
  "planNombre": "Plan Principiante",
  "fechaInicio": "2025-12-16",
  "fechaFin": "2026-01-16",
  "activa": true
}
```

### HorarioDTO
```json
{
  "id": 1,
  "membresiaId": 1,
  "diaSemana": "Lunes",
  "horaInicio": "09:00",
  "horaFin": "10:00",
  "activo": true
}
```

---

## Códigos de Respuesta HTTP

| Código | Descripción |
|--------|-------------|
| 200 | OK - Solicitud exitosa |
| 201 | Created - Recurso creado exitosamente |
| 204 | No Content - Solicitud exitosa sin contenido |
| 400 | Bad Request - Solicitud inválida |
| 404 | Not Found - Recurso no encontrado |
| 500 | Internal Server Error - Error del servidor |

---

## Seguridad

- Los endpoints de API están configurados para permitir acceso sin autenticación (públicos)
- CSRF está deshabilitado para `/api/**`
- CORS está configurado para los puertos de desarrollo locales
- Las contraseñas se almacenan codificadas con BCrypt

---

## Uso desde una Aplicación Móvil

### Con Ionic/Angular:

```typescript
import { HttpClient } from '@angular/common/http';

export class ApiService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getPlanes() {
    return this.http.get(`${this.apiUrl}/planes`);
  }

  getMembresiaActiva(usuarioId: number) {
    return this.http.get(`${this.apiUrl}/membresias/usuario/${usuarioId}`);
  }

  getHorarios(membresiaId: number) {
    return this.http.get(`${this.apiUrl}/horarios/membresia/${membresiaId}`);
  }
}
```

### Con React Native o Flutter:

```javascript
// Ejemplo con fetch
fetch('http://localhost:8080/api/planes')
  .then(response => response.json())
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));
```

---

## Ejecución del Proyecto

### Compilar
```bash
./mvnw clean compile
```

### Ejecutar
```bash
./mvnw spring-boot:run
```

### Acceso a la Consola H2
```
http://localhost:8080/h2-console
```

---

## Desarrollo

El proyecto fue desarrollado para permitir la integración con una aplicación móvil de evaluación.

### Autor
Proyecto NakMuay - Academia de Muay Thai

### Última actualización
16 de diciembre de 2025
