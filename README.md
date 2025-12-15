# Proyecto DemoMarcial

## Resumen

Nombre: DemoMarcial — Sistema web de gestión de inscripciones y usuarios para un centro (demo).

Breve descripción: Aplicación web construida con Spring Boot que ofrece páginas públicas (inicio, galería, contacto, inscripción), registro e inicio de sesión de usuarios, y un panel de administración básico para listar, editar, activar/desactivar y eliminar usuarios. Incluye autenticación con Spring Security, persistencia en H2 (en memoria) usando Spring Data JPA y vistas con Thymeleaf.

---

## Descripción general

DemoMarcial es una aplicación web monolítica basada en Spring Boot destinada a demostrar un flujo típico de autenticación/registro y gestión de usuarios. Está pensada como proyecto educativo/prueba de concepto para comprender:

- Registro y login con Spring Security y BCrypt.
- Gestión de entidades JPA (Usuario y Role) con Spring Data JPA.
- Plantillas server-side con Thymeleaf (formularios, fragments, navbar, vistas de error personalizadas).
- Uso de H2 en modo memoria para desarrollo y pruebas rápidas.

Contexto: aplicación web MVC (no un microservicio) que sirve HTML renderizado por servidor (Thymeleaf). No es una API REST pura, aunque los controladores pueden adaptarse fácilmente.

Arquitectura (resumen):

- Controllers: manejan rutas HTTP y seleccionan vistas (p. ej. `indexController`, `UserController`, `ContactoController`).
- Services: lógica de negocio (p. ej. `UserService` / `UserServiceImpl`, `MyUserDetailsService`).
- Repositories: acceso a datos (Spring Data JPA) — `UserRepository`, `RoleRepository`.
- Entities: modelo de dominio mapeado a BD con JPA — `Usuario`, `Role`.
- Config: configuración de seguridad (`SecurityConfig`) y utilidades de carga (`DataLoader`).
- Templates: Thymeleaf en `src/main/resources/templates` con fragments para navbar/footers.

---

## Tecnologías y dependencias principales

- Java 17
- Spring Boot 3.5.6
- Maven (build)
- Thymeleaf (plantillas server-side)
- Spring Security (autenticación/roles)
- Spring Data JPA (persistencia)
- H2 Database (runtime, en memoria)
- Spring Boot DevTools (desarrollo)
- Spring Boot Starter Validation

Dependencias clave del `pom.xml` (y su propósito):

| Dependencia | Propósito |
|---|---|
| spring-boot-starter-web | Soporte web MVC, embedded Tomcat |
| spring-boot-starter-thymeleaf | Motor de plantillas para vistas HTML |
| spring-boot-starter-security | Autenticación y autorización |
| spring-boot-starter-data-jpa | Integración JPA/Hibernate y repositorios |
| com.h2database:h2 | Base de datos en memoria para desarrollo |
| spring-boot-devtools | Recarga automática en desarrollo |
| spring-boot-starter-validation | Validaciones en formularios (javax/hibernate validator) |

---

## Estructura del proyecto

Estructura principal (paquetes relevantes):

- `com.example.proyecto1spring.controllers` — Controladores MVC que manejan rutas y retornan vistas Thymeleaf.
- `com.example.proyecto1spring.service` — Interfaces y clases de servicio; contiene `MyUserDetailsService` para Spring Security.
- `com.example.proyecto1spring.service.impl` — Implementaciones concretas (`UserServiceImpl`).
- `com.example.proyecto1spring.repository` — Repositorios JPA (`UserRepository`, `RoleRepository`).
- `com.example.proyecto1spring.entity` — Entidades JPA (`Usuario`, `Role`).
- `com.example.proyecto1spring.config` — Configuración (p. ej. `SecurityConfig`).
- `com.example.proyecto1spring.util` — Utilidades, p. ej. `DataLoader` para datos iniciales.
- `src/main/resources/templates` — Plantillas Thymeleaf (.html).
- `src/main/resources/static` — Recursos estáticos (CSS, JS, imágenes).

Qué hace cada carpeta y su relación:

- Controllers reciben las peticiones HTTP, validan parámetros mínimos y delegan la lógica al Service.
- Services contienen las reglas de negocio y orquestan llamadas a Repositories.
- Repositories abstraen la persistencia (CRUD sobre entidades).
- Entities representan las tablas en la BD y son usadas por Repositories y Services.
- Config configura seguridad y beans globales.

---

## Rutas y endpoints principales

La aplicación es principalmente server-side (rutas que retornan vistas). A continuación un resumen de rutas importantes:

| URL | Método | Descripción |
|---|---:|---|
| `/` | GET | Página de inicio (`index.html`). |
| `/index` | GET | Alias a la página de inicio. |
| `/login` | GET | Página de login (formulario). |
| `/login` | POST | (manejada por Spring Security) Procesa login; parámetros: `email`, `password`. |
| `/registro` | GET | Muestra el formulario de registro (usa `user_create.html`). |
| `/user/create` | GET | Formulario de creación de usuario (registro). |
| `/user/create` | POST | Crea un usuario nuevo. Parámetros: `nombre`, `apellido`, `email`, `rut`, `password`, `rol` (opcional). Redirige a `/login?registered` en éxito. |
| `/inscripcion` | GET | Formulario de inscripción público. |
| `/inscripcion` | POST | Envía inscripción (almacenada en memoria). |
| `/galeria` | GET | Página galería. |
| `/nosotros` | GET | Página 'nosotros'. |
| `/contacto` | GET | Página de contacto. |
| `/user/list` | GET | (ADMIN) Lista usuarios — plantilla `user_list.html`. |
| `/usuarios_list` | GET | (ADMIN) Alias para listar usuarios. |
| `/user/view/{id}` | GET | (ADMIN) Ver detalles de un usuario. |
| `/user/edit/{id}` | GET | (ADMIN) Mostrar formulario de edición para usuario con id. |
| `/user/edit/{id}` | POST | (ADMIN) Procesa la edición; acepta un formulario con los campos del `Usuario`. |
| `/user/delete/{id}` | GET | (ADMIN) Elimina usuario por id y redirige a la lista. |
| `/user/toggle/{id}` | GET | (ADMIN) Activa/desactiva (campo `enabled`) de usuario. |
| `/h2-console/**` | GET | Consola H2 (habilitada, solo para desarrollo). |

Notas:
- Las rutas de administración están protegidas mediante `@PreAuthorize("hasRole('ADMIN')")` y requieren autenticación.
- El login es manejado por Spring Security y espera `email` y `password` como parámetros del formulario.

---

## Comunicación interna

Patrón: Controller → Service → Repository → Base de datos.

- Controllers: manipulan solicitudes HTTP y vistas. No se usan DTOs dedicados; los controladores trabajan con la entidad `Usuario` o con parámetros simples.
- Services: encapsulan la lógica de negocio (validaciones para creación/actualización del usuario, encriptación de contraseña en `UserServiceImpl`).
- Repositories: extienden `JpaRepository` para operaciones CRUD.

Excepciones y manejo de errores:

- El código usa `IllegalArgumentException` para casos como email o RUT duplicados; los controladores redirigen en esos casos (p. ej. `?error`). No hay una capa centralizada de errores con `@ControllerAdvice` (aparte de la clase `GlobalModelAttributes` que añade atributos al modelo); sin embargo, existen plantillas para `error/404.html` y `error/error.html` para vistas de error.
- No se usan `ResponseEntity` ni DTOs REST en el flujo actual (es HTML renderizado por servidor).

---

## Configuración y ejecución (local)

Requisitos previos:

- JDK 17
- Maven (o usar el wrapper incluido `mvnw` / `mvnw.cmd`)

Pasos para ejecutar:

1. Clonar el repositorio:

```powershell
git clone <repo-url>
cd DemoMarcial
```

2. Construir el proyecto (compila y empaqueta):

```powershell
.\mvnw clean package
```

3. Ejecutar la aplicación:

```powershell
.\mvnw spring-boot:run
# o ejecutar el JAR generado:
java -jar target\DemoMarcial-0.0.1-SNAPSHOT.jar
```

Por defecto la app corre en `http://localhost:8080`.

Si el puerto 8080 está ocupado puedes lanzar en otro puerto:

```powershell
.\mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

Acceso H2 (solo desarrollo): `http://localhost:8080/h2-console` (configuración en `application.properties`).

Usuario administrador inicial (creado por `DataLoader`):

- Email: `admin@example.com`
- Password: `admin`

---

## Variables de entorno y configuración

Configuración principal en `src/main/resources/application.properties`:

- `spring.datasource.url=jdbc:h2:mem:demo` — base de datos en memoria (desarrollo).
- `spring.jpa.hibernate.ddl-auto=update` — crea/actualiza esquema automáticamente.
- `spring.h2.console.enabled=true` y `spring.h2.console.path=/h2-console`.

Para producción se recomienda usar una base de datos persistente (MySQL, PostgreSQL, etc.) y externalizar credenciales mediante variables de entorno o `application-{profile}.properties`.

---

## Comandos útiles

- Construir: `mvn clean install` o `./mvnw clean package`
- Ejecutar: `mvn spring-boot:run` o `./mvnw spring-boot:run`
- Ejecutar tests: `mvn test` o `./mvnw test`
- Ejecutar sin tests (build rápido): `mvn -DskipTests package`

---

## Pruebas

El proyecto incluye la dependencia `spring-boot-starter-test`. En `src/test` existe una clase de prueba de arranque (`Proyecto1springApplicationTests.java`).

Ejecutar pruebas:

```powershell
.\mvnw test
```

Actualmente no hay un conjunto extenso de tests de unidad o integración; se recomienda añadir pruebas para `UserService` (creación/actualización/delete) y para validación de controladores.

---

## Seguridad

- Autenticación: Spring Security con formulario (`formLogin`).
- Encriptación de contraseñas: `BCryptPasswordEncoder` (bean declarado en `SecurityConfig`).
- UserDetails: la entidad `Usuario` implementa `UserDetails`. El `MyUserDetailsService` carga usuarios por email (`findByEmail`).
- Roles: string simple en la entidad (`rol`) con valores `ADMIN` o `USER`. Se convierten a authorities con prefijo `ROLE_`.
- Protección de rutas: las rutas administrativas usan `@PreAuthorize("hasRole('ADMIN')")`. Se permite acceso público a recursos estáticos y páginas públicas (`/`, `/index`, `/login`, `/registro`, `/user/create`, `/nosotros`, `/galeria`, `/contacto`).

---

## Errores comunes y soluciones

- 403 Forbidden al enviar formularios (edición/registro): normalmente causado por ausencia del token CSRF en el formulario o por falta de mapping POST en el controlador. Soluciones:
  - Asegurarse de incluir en el formulario Thymeleaf: `<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />`.
  - Comprobar que exista un `@PostMapping` para la URL destino y que el usuario tenga permisos.

- 404 Not Found para rutas públicas: revisar nombres de plantillas en `src/main/resources/templates` y mappings `@GetMapping` en controladores.

- Problema al iniciar por puerto ocupado (8080): ejecutar la app en otro puerto pasando el argumento `--server.port=8081` o terminar el proceso que ocupa 8080.

- Error `Ambiguous mapping` al arrancar: suele ocurrir si hay dos controladores con la misma ruta (p. ej. `/registro`). Eliminar/ajustar el mapping duplicado.

- Thymeleaf TemplateInputException relacionado con `#request` u objetos de request: versiones recientes de Thymeleaf + Spring Boot pueden requerir exponer atributos desde `@ControllerAdvice` o usar expresión `${param}` en el modelo. En este proyecto se añadió `GlobalModelAttributes` para exponer `isAuthenticated` y `isAdmin`.

---
