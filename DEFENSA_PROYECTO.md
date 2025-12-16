# Defensa del Proyecto: DemoMarcial (Spring Boot)

## 1. Stack Tecnológico
- Java 17
- Spring Boot 3.5.x (Web, Security, Data JPA, Validation)
- Thymeleaf (vistas server-side)
- H2 (BD en memoria para desarrollo)
- Maven (build)
- Docker (containerización), Koyeb (despliegue)

## 2. Arquitectura y Capas
- Controllers: manejan rutas y retornan vistas Thymeleaf.
  - UserController: CRUD de usuarios (admin).
  - MiPlanController: gestión de membresías del usuario (ver plan, cambiar, cancelar).
  - AdminPlanesController: CRUD de planes (admin).
  - AdminAlumnosController: gestión de alumnos con detalle de membresías (admin).
- Services: lógica de negocio; `UserServiceImpl`, `PlanServiceImpl`, `MembresiaServiceImpl` con transaccionalidad.
- Repositories: interfaces Spring Data sobre entidades JPA (`Usuario`, `Role`, `Plan`, `Membresia`).
- Entities: modelo de dominio con relaciones JPA.
- Config: `SecurityConfig` define autenticación/autoridades y login/logout.

## 3. Seguridad (Spring Security)
- Autenticación: formulario custom (`/login`).
- `UserDetailsService`: `MyUserDetailsService` carga por email.
- Passwords: `BCryptPasswordEncoder`.
- Autorización: `@EnableMethodSecurity` + `@PreAuthorize("hasRole('ADMIN')")` para rutas administrativas.
- CSRF: habilitado; se ignora en `/h2-console/**` para que funcione la consola.

## 4. Persistencia y Modelo
- Entidad `Usuario`: campos `id`, `rut`, `nombre`, `apellido`, `email`, `password`, `rol`, `enabled`, `createdAt`, `updatedAt`.
- Entidad `Plan`: campos `id`, `nombre`, `descripcion`, `precio`, `duracionMeses`, `activo`.
- Entidad `Membresia`: relación Usuario-Plan con `fechaInicio`, `fechaFin`, `activa`.
- Repositorios: `UserRepository`, `PlanRepository`, `MembresiaRepository` con queries personalizadas.
- Validaciones (base): duplicidad de email/rut en servicio; passwords encriptadas; existencia de membresía activa.

## 5. CRUD y Transaccionalidad
- Usuario: crear, editar, eliminar, activar/desactivar (`@Transactional` en writes).
- Plan: crear, editar, eliminar, activar/desactivar (`@Transactional`).
- Membresía: crear (contratación), cambiar plan, cancelar (`@Transactional`).
- Lecturas: no transaccionales; queries optimizadas con `Optional` y listas.

## 6. Docker y Despliegue
- `Dockerfile` multi-stage: build con Maven, runtime con JRE 17 Alpine.
- `.dockerignore`: reduce tamaño de imagen.
- `docker-compose.yml`: prueba local.
- Config puerto dinámico: `server.port=${PORT:8080}`.
- Koyeb: buildpack Docker; despliegue conectado a GitHub.

## 7. Respuestas a Banco de Preguntas
- ¿Por qué Spring Security?:
  - Estándar de industria, fácil integración, soporte para formularios y roles.
- ¿Cómo se protegen rutas?:
  - Por método con `@PreAuthorize` y por configuración en `SecurityFilterChain`.
- ¿Cómo se autentica el usuario?:
  - `MyUserDetailsService` carga `Usuario` por email; compara contraseña encriptada con BCrypt.
- ¿Dónde se define el encoder?:
  - Bean `PasswordEncoder` en `SecurityConfig` (BCrypt).
- ¿Qué es CSRF y cómo se maneja?:
  - Protección contra falsificación de petición; se ignora en `/h2-console/**` y formularios incluyen token.
- ¿Por qué usar H2?:
  - Desarrollo rápido; no persiste entre reinicios; para producción usar Postgres/MySQL.
- ¿Qué hace `@Transactional`?:
  - Garantiza atomicidad; si una operación falla, se revierte; aplicado a crear/actualizar/borrar/toggle.
- ¿Cómo se manejan duplicados?:
  - Validaciones en servicio con `existsByEmail`/`existsByRut`; errores lanzan `IllegalArgumentException`.
- ¿Cómo se estructura el MVC?:/`existsByNombre`; errores lanzan `IllegalArgumentException`.
- ¿Cómo se estructura el MVC?:
  - Controller recibe petición → Service aplica reglas → Repository accede a BD → retorna vista Thymeleaf.
- ¿Cómo funciona el sistema de membresías?:
  - Usuario contrata plan desde `/mi-plan`, crea `Membresia` con fechas automáticas; puede cambiar plan (renueva fechas) o cancelar (marca inactiva).
- ¿Qué puede hacer un admin con planes?:
  - CRUD completo en `/admin/planes`: crear, editar, eliminar, activar/desactivar planes.
- ¿Qué puede hacer un admin con alumnos?:
  - Ver lista en `/admin/alumnos`, detalle con historial de membresías, editar, activar/desactivar, eliminar.
- ¿Cómo se configura el login?:
  - `formLogin()` en `SecurityConfig` con página `/login`, parámetros `email` y `password`, success URL `/`.
- ¿Cómo se despliega en Koyeb?:
  - Conectar GitHub, buildpack Docker, `Create and deploy`; puerto tomado de env `PORT`.
- ¿Qué mejoras harías?:
  - Validaciones bean (`@NotBlank`, `@Email`, `@Size`) + manejo de errores en vistas; BD persistente; tests de servicio; DTOs; paginación; roles más granulare
## 8. Comandos útiles
- Ejecutar local:
```bash
./mvnw spring-boot:run
```
- Docker local:
```bash
docker build -t sring-proy .
docker run -p 8080:8080 sring-proy
```
- Tests:
```bash
./mvnw test
```
- Sistema de membresías: permite gestión de suscripciones con fechas automáticas, historial y cambios de plan; fortalece el proyecto con funcionalidad real de negocio.

## 10. Diagrama simple de flujo (texto)
Usuario → `/login` → Security (auth) → Controlador → Servicio → Repositorio → BD → Vista Thymeleaf.

Alumno contrata plan → `/mi-plan/contratar` → MembresiaService crea membresía con fechas → Usuario ve su plan activo.

Admin gestiona planes → `/admin/planes` → PlanService CRUD → Vista lista planes con acciones activar/desactivar/eliminar
- BCrypt: estándar seguro para contraseñas.
- H2: rapidez en desarrollo; se comunica que no es para producción.
- `@Transactional`: cumple requisito de transaccionalidad y mejora integridad.

## 10. Diagrama simple de flujo (texto)
Usuario → `/login` → Security (auth) → Controlador → Servicio → Repositorio → BD → Vista Thymeleaf.
