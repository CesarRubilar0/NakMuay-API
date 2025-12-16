# NakMuay - Academia de Muay Thai

## Resumen

**NakMuay** — Sistema web completo de gestión para una academia de Muay Thai con REST API integrada.

Aplicación web construida con Spring Boot que incluye:
- **REST API**: Endpoints completos para integración con aplicaciones móviles
- **Sistema de Planes**: Básico, Estándar, Premium y Anual
- **Gestión de Membresías**: Inscripción con selección de horarios de entrenamiento
- **Gestión de Usuarios**: Registro, login y perfiles
- **Autenticación completa**: Spring Security con roles (USER/ADMIN)
- **Documentación Swagger**: API documentation interactiva en `/swagger-ui.html`
- **Panel de Administración**: Gestión de alumnos y planes
- **Docker Ready**: Dockerfile multi-stage para deploy en Koyeb
- **Base de datos H2**: En memoria con datos precargados vía DataLoader

---

## ✨ Novedades - REST API

Esta versión incluye una **REST API completa** para permitir integración con aplicaciones móviles (Ionic, React Native, Flutter, etc.).

### Nuevos Endpoints REST:

**Planes de Entrenamiento:**
- `GET /api/planes` - Obtener todos los planes
- `GET /api/planes/{id}` - Obtener plan específico
- `POST /api/planes` - Crear nuevo plan
- `PUT /api/planes/{id}` - Actualizar plan
- `DELETE /api/planes/{id}` - Eliminar plan

**Membresías:**
- `GET /api/membresias` - Todas las membresías
- `GET /api/membresias/usuario/{id}` - Membresía activa del usuario
- `POST /api/membresias` - Crear membresía
- `PUT /api/membresias/{id}` - Actualizar membresía
- `DELETE /api/membresias/{id}` - Eliminar membresía

**Horarios:**
- `GET /api/horarios` - Todos los horarios
- `GET /api/horarios/membresia/{id}` - Horarios de una membresía
- `POST /api/horarios` - Crear horario
- `PUT /api/horarios/{id}` - Actualizar horario
- `DELETE /api/horarios/{id}` - Eliminar horario

**Usuarios:**
- `GET /api/usuarios` - Todos los usuarios
- `GET /api/usuarios/{id}` - Usuario específico
- `GET /api/usuarios/email/{email}` - Usuario por email

### Configuración CORS:

La API acepta requests desde:
- `http://localhost:8100` (Ionic)
- `http://localhost:4200` (Angular)
- `http://localhost:8080` (desarrollo local)

---

## Descripción general

**NakMuay** es una aplicación web completa para gestión de una academia de Muay Thai que permite:

### **Funcionalidades Principales:**

1. **Sistema de Planes de Membresía:**
   - Plan Básico: $25,000 / 1 mes (3 clases por semana)
   - Plan Estándar: $45,000 / 3 meses (ilimitado + 1 clase personalizada/mes)
   - Plan Premium: $75,000 / 6 meses (ilimitado + 4 clases personalizadas + nutrición)
   - Plan Anual: $500,000 / 12 meses (acceso completo con descuento)

2. **Gestión de Membresías:**
   - Inscripción con selección de plan
   - Configuración de horarios de entrenamiento (Lunes a Viernes, 9:00-21:00)
   - Visualización de plan activo y horarios
   - Modificación, cambio de plan o cancelación

3. **Panel de Usuario (/mi-plan):**
   - Ver plan activo con fechas de inicio/fin
   - Gestionar horarios de entrenamiento
   - Cambiar de plan
   - Cancelar membresía

4. **Panel de Administración:**
   - Gestión completa de planes (CRUD)
   - Administración de alumnos
   - Activar/desactivar usuarios
   - Ver detalles de membresías y horarios

### **Arquitectura:**

- **Controllers:** MVC con Thymeleaf (indexController, UserController, MiPlanController, AdminPlanesController, AdminAlumnosController, ContactoController)
- **REST API Controllers:** Endpoints JSON en `/api/**` (PlanRestController, MembresiaRestController, HorarioRestController, UsuarioRestController)
- **Services:** Lógica de negocio con @Transactional (UserService, PlanService, MembresiaService, HorarioEntrenamientoService, MyUserDetailsService)
- **Repositories:** Spring Data JPA (UserRepository, RoleRepository, PlanRepository, MembresiaRepository, HorarioEntrenamientoRepository)
- **Entities:** Modelo JPA (Usuario, Role, Plan, Membresia, HorarioEntrenamiento)
- **Config:** Seguridad (SecurityConfig), Carga de datos (DataLoader)
- **Templates:** Thymeleaf con fragments (navbar, headers, footer)

---

## Tecnologías y dependencias principales

- **Java 17**
- **Spring Boot 3.5.6**
- **Maven** (build tool)
- **Thymeleaf** (plantillas server-side)
- **Spring Security** (autenticación/autorización con BCrypt)
- **Spring Data JPA** (persistencia)
- **H2 Database** (runtime, en memoria)
- **Docker** (contenedorización)
- **Spring Boot DevTools** (desarrollo)
- **Spring Boot Starter Validation**

### Dependencias clave del `pom.xml`:

| Dependencia | Propósito |
|---|---|
| spring-boot-starter-web | Soporte web MVC, embedded Tomcat |
| spring-boot-starter-thymeleaf | Motor de plantillas para vistas HTML |
| spring-boot-starter-security | Autenticación y autorización |
| spring-boot-starter-data-jpa | Integración JPA/Hibernate y repositorios |
| com.h2database:h2 | Base de datos en memoria para desarrollo |
| spring-boot-devtools | Recarga automática en desarrollo |
| spring-boot-starter-validation | Validaciones JSR-303 en formularios |

---

## Estructura del proyecto

```
src/main/java/com/example/proyecto1spring/
├── controllers/
│   ├── indexController.java          # Rutas públicas (/,/inscripcion,/nosotros,etc.)
│   ├── UserController.java           # CRUD usuarios (ADMIN)
│   ├── MiPlanController.java         # Gestión de plan del usuario
│   ├── AdminPlanesController.java    # CRUD planes (ADMIN)
│   ├── AdminAlumnosController.java   # Gestión alumnos (ADMIN)
│   └── ContactoController.java       # Página de contacto
├── service/
│   ├── UserService.java & impl/      # Lógica usuarios
│   ├── PlanService.java & impl/      # Lógica planes
│   ├── MembresiaService.java & impl/ # Lógica membresías
│   ├── HorarioEntrenamientoService.java & impl/ # Lógica horarios
│   └── MyUserDetailsService.java     # UserDetailsService para Security
├── repository/
│   ├── UserRepository.java           # CRUD Usuario
│   ├── RoleRepository.java           # CRUD Role
│   ├── PlanRepository.java           # CRUD Plan
│   ├── MembresiaRepository.java      # CRUD Membresia
│   └── HorarioEntrenamientoRepository.java # CRUD Horario
├── entity/
│   ├── Usuario.java                  # Entidad usuario (implements UserDetails)
│   ├── Role.java                     # Entidad rol
│   ├── Plan.java                     # Entidad plan de membresía
│   ├── Membresia.java                # Entidad membresía (usuario + plan)
│   └── HorarioEntrenamiento.java     # Entidad horario de entrenamiento
├── config/
│   ├── SecurityConfig.java           # Configuración Spring Security
│   └── GlobalModelAttributes.java    # Atributos globales para vistas
└── util/
    └── DataLoader.java               # Carga inicial de datos (roles, admin, planes)

src/main/resources/
├── templates/
│   ├── index.html                    # Página principal
│   ├── login.html                    # Página de login
│   ├── inscripcion.html              # Formulario inscripción + selección horarios
│   ├── mi_plan.html                  # Vista plan activo del usuario
│   ├── user_*.html                   # CRUD usuarios (list, create, edit, view)
│   ├── admin_planes_*.html           # CRUD planes (list, form)
│   ├── admin_alumnos_*.html          # Gestión alumnos (list, detail)
│   ├── error/                        # Vistas de error personalizadas
│   └── fragments/                    # Headers, navbar, footer
├── static/
│   └── img/                          # Imágenes estáticas
├── application.properties            # Configuración app
└── import.sql                        # Scripts SQL iniciales
```

---

## Rutas y endpoints principales

### **Rutas Públicas** (sin autenticación):
| URL | Método | Descripción |
|---|---:|---|
| `/` | GET | Página de inicio con información de la academia |
| `/index` | GET | Alias a la página de inicio |
| `/login` | GET/POST | Página de login (manejada por Spring Security) |
| `/registro` | GET | Formulario de registro de nuevos usuarios |
| `/user/create` | POST | Crea un usuario nuevo |
| `/nosotros` | GET | Página 'Quiénes somos' |
| `/galeria` | GET | Galería de fotos |
| `/contacto` | GET | Página de contacto |

### **Rutas de Usuario** (requieren autenticación):
| URL | Método | Descripción |
|---|---:|---|
| `/inscripcion` | GET | Muestra planes disponibles y formulario de inscripción |
| `/inscripcion` | POST | Procesa inscripción con selección de plan y horarios |
| `/mi-plan` | GET | Muestra plan activo y horarios del usuario |
| `/mi-plan/contratar` | POST | Contrata un nuevo plan |
| `/mi-plan/cambiar` | POST | Cambia a otro plan |
| `/mi-plan/cancelar` | POST | Cancela la membresía activa |
| `/mi-plan/horario/agregar` | POST | Agrega un nuevo horario de entrenamiento |
| `/mi-plan/horario/eliminar/{id}` | POST | Elimina un horario específico |

### **Rutas de Administración** (requieren rol ADMIN):
| URL | Método | Descripción |
|---|---:|---|
| `/user/list` | GET | Lista todos los usuarios |
| `/user/view/{id}` | GET | Ver detalles de un usuario |
| `/user/edit/{id}` | GET/POST | Editar usuario |
| `/user/delete/{id}` | GET | Eliminar usuario |
| `/user/toggle/{id}` | GET | Activar/desactivar usuario |
| `/admin/planes` | GET | Lista todos los planes |
| `/admin/planes/crear` | GET/POST | Crear nuevo plan |
| `/admin/planes/editar/{id}` | GET/POST | Editar plan existente |
| `/admin/planes/eliminar/{id}` | POST | Eliminar plan |
| `/admin/planes/toggle-active/{id}` | POST | Activar/desactivar plan |
| `/admin/alumnos` | GET | Lista todos los alumnos |
| `/admin/alumnos/ver/{id}` | GET | Ver detalle alumno (membresía + horarios) |
| `/admin/alumnos/toggle-enabled/{id}` | POST | Activar/desactivar alumno |
| `/admin/alumnos/eliminar/{id}` | POST | Eliminar alumno |

### **Otras rutas:**
| URL | Método | Descripción |
|---|---:|---|
| `/h2-console/**` | GET | Consola H2 (solo desarrollo) |

---

## Seguridad y autenticación

El sistema utiliza **Spring Security** con autenticación basada en formularios:

### **Flujo de autenticación:**
1. **Usuario sin sesión**: Al intentar acceder a `/inscripcion` u otras rutas protegidas, es redirigido a `/login`
2. **Login exitoso**: Tras autenticarse, el usuario es redirigido automáticamente a `/inscripcion` para seleccionar su plan
3. **Credenciales**: Email y contraseña (contraseñas encriptadas con BCrypt)
4. **Roles**: ADMIN (gestión completa) y USER (acceso a membresías y horarios)

### **Usuario administrador por defecto:**
```
Email: admin@example.com
Contraseña: admin
```

### **Permisos por rol:**
- **Rutas públicas**: `/`, `/login`, `/registro`, `/nosotros`, `/galeria`, `/contacto`
- **Rutas de usuario (USER)**: `/inscripcion`, `/mi-plan/*` (requieren autenticación)
- **Rutas de admin (ADMIN)**: `/admin/*`, `/user/*` (requieren rol ADMIN con @PreAuthorize)

### **Implementación técnica:**
- **SecurityConfig.java**: Configuración de Spring Security con filterChain, BCryptPasswordEncoder, defaultSuccessUrl("/inscripcion")
- **MyUserDetailsService.java**: Implementa UserDetailsService, carga usuarios por email desde base de datos
- **Usuario.java**: Entidad que implementa UserDetails con roles y credenciales encriptadas

### **CSRF Protection:**
Todos los formularios Thymeleaf incluyen token CSRF automáticamente. Para formularios manuales:
```html
<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
```

---

## Inicialización de datos

El sistema utiliza **DataLoader.java** (`util` package) para cargar datos iniciales al arrancar:

### **Datos precargados automáticamente:**
- ✅ **2 roles**: ADMIN, USER
- ✅ **1 usuario admin**: admin@example.com / admin (contraseña encriptada con BCrypt)
- ✅ **4 planes** (solo si `planRepository.count() == 0`):
  - Plan Básico: $25,000 / 1 mes
  - Plan Estándar: $45,000 / 3 meses  
  - Plan Premium: $75,000 / 6 meses
  - Plan Anual: $500,000 / 12 meses

### **¿Por qué DataLoader en lugar de import.sql?**
- **Confiabilidad**: Se ejecuta después de la creación del esquema JPA, evitando problemas de sincronización
- **Control programático**: Verifica si ya existen datos antes de insertar (idempotencia)
- **Logs visibles**: Registra la creación de datos en la consola ("✅ 4 planes creados exitosamente")
- **Portabilidad**: Funciona igual en desarrollo (H2) y producción sin cambios

### **Configuración necesaria** (application.properties):
```properties
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always
server.port=${PORT:8080}  # Compatible con Koyeb
```

---

## Despliegue con Docker

El proyecto incluye configuración completa para Docker con build multi-stage optimizado:

### **Dockerfile:**
```dockerfile
# Stage 1: Build con Maven
FROM maven:3.9.5-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime con JRE Alpine ligero
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### **Ventajas del multi-stage build:**
- **Imagen final ligera**: Solo incluye JRE (no Maven ni compiladores)
- **Alpine Linux**: Base mínima (~150MB vs ~500MB con JDK completo)
- **Seguridad**: Menos superficie de ataque, menos vulnerabilidades

### **Comandos Docker:**

```bash
# Construir imagen
docker build -t nakmuay-app .

# Ejecutar contenedor en puerto 8080
docker run -p 8080:8080 nakmuay-app

# Ver logs
docker logs <container-id>

# Con docker-compose (incluye configuración de entorno)
docker-compose up -d

# Detener servicios
docker-compose down
```

### **docker-compose.yml:**
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SERVER_PORT=8080
    restart: unless-stopped
```

### **.dockerignore:**
El archivo `.dockerignore` excluye archivos innecesarios del build para reducir tamaño:
```
target/
.mvn/
.git/
*.md
.gitignore
mvnw
mvnw.cmd
```

---

## Despliegue en Koyeb

**Koyeb** es la plataforma de despliegue en la nube conectada al repositorio GitHub:

### **Configuración inicial:**

1. **Conectar repositorio a Koyeb:**
   - Accede a [app.koyeb.com](https://app.koyeb.com)
   - Crea una cuenta o inicia sesión
   - Click en "Create Service" → "GitHub"
   - Autoriza acceso a tu repositorio: `CesarRubilar0/Sring_proy`

2. **Configurar servicio:**
   - **Name**: `nakmuay-app` (o el nombre que prefieras)
   - **Build method**: Docker (Koyeb detecta automáticamente el Dockerfile)
   - **Port**: 8080 (EXPOSE 8080 en Dockerfile)
   - **Health check path**: `/` (verifica que la app responda)
   - **Environment variables** (opcional):
     ```
     SPRING_PROFILES_ACTIVE=prod
     SERVER_PORT=8080
     ```

3. **Deploy automático:**
   - Koyeb monitorea el repositorio GitHub
   - Al hacer `git push origin main`, se despliega automáticamente
   - El build toma ~3-5 minutos (compilación Maven + imagen Docker)

4. **Verificar deployment:**
   - URL generada: `https://nakmuay-app-tu-usuario.koyeb.app` (o similar)
   - Revisar logs en el dashboard de Koyeb para ver DataLoader ejecutándose
   - Probar login: admin@example.com / admin
   - Verificar que aparezcan los 4 planes en `/inscripcion`

### **Comandos Git para deployment:**
```bash
# Verificar cambios
git status

# Agregar cambios
git add .

# Commit con mensaje descriptivo
git commit -m "Actualizar README con documentación completa"

# Push a GitHub (trigger auto-deploy en Koyeb)
git push origin main
```

### **Solución de problemas en Koyeb:**
- **Build falla**: Revisar logs en Koyeb → "Deployment" → "Build logs"
- **App no inicia**: Verificar que `EXPOSE 8080` esté en Dockerfile y `server.port=${PORT:8080}` en application.properties
- **404 en todas las rutas**: Verificar que el JAR se construya correctamente y que la app Spring Boot arranque (ver logs de runtime)
- **No muestra planes**: DataLoader debe ejecutarse; buscar en logs "✅ 4 planes creados exitosamente"

---

## Ejecución local (desarrollo)

### **Requisitos previos:**
- JDK 17 o superior
- Maven (o usar el wrapper incluido `mvnw.cmd`)
- Git

### **Pasos para ejecutar:**

1. **Clonar el repositorio:**
```powershell
git clone https://github.com/CesarRubilar0/Sring_proy.git
cd Sring_proy-main
```

2. **Compilar el proyecto:**
```powershell
.\mvnw clean package -DskipTests
```

3. **Ejecutar la aplicación:**
```powershell
# Opción 1: Con Maven
.\mvnw spring-boot:run

# Opción 2: Ejecutar el JAR generado
java -jar target\DemoMarcial-0.0.1-SNAPSHOT.jar

# Opción 3: En otro puerto si 8080 está ocupado
.\mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

4. **Acceder a la aplicación:**
- App: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console (solo desarrollo)
  - JDBC URL: `jdbc:h2:mem:demo`
  - User: `sa`
  - Password: (dejar en blanco)

5. **Credenciales por defecto:**
```
Email: admin@example.com
Contraseña: admin
```

---

## Comandos útiles

```powershell
# Compilar sin tests (más rápido)
.\mvnw clean package -DskipTests

# Ejecutar tests
.\mvnw test

# Ver dependencias del proyecto
.\mvnw dependency:tree

# Limpiar carpeta target
.\mvnw clean

# Verificar actualizaciones de dependencias
.\mvnw versions:display-dependency-updates

# Git: Ver estado
git status

# Git: Commit y push
git add .
git commit -m "Descripción del cambio"
git push origin main
```

---

## Errores comunes y soluciones

### **1. Planes no aparecen en /inscripcion:**
**Síntoma**: El dropdown de planes está vacío.
**Causa**: DataLoader no se ejecutó o la base de datos no tiene datos.
**Solución**:
- Verificar logs al iniciar: buscar "✅ 4 planes creados exitosamente"
- Verificar `application.properties` tenga:
  ```properties
  spring.jpa.defer-datasource-initialization=true
  spring.sql.init.mode=always
  ```
- Reiniciar la aplicación para que DataLoader se ejecute nuevamente

### **2. 403 Forbidden al enviar formularios:**
**Síntoma**: Error al enviar POST desde formularios.
**Causa**: Falta token CSRF en el formulario.
**Solución**:
- Thymeleaf incluye CSRF automáticamente con `th:action`
- Para formularios manuales, agregar:
  ```html
  <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
  ```

### **3. Puerto 8080 ocupado:**
**Síntoma**: Error "Port 8080 is already in use".
**Solución**:
```powershell
# Opción 1: Usar otro puerto
.\mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"

# Opción 2: Liberar puerto 8080 (Windows)
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### **4. Docker build falla:**
**Síntoma**: Error al construir imagen Docker.
**Causa**: Maven no puede resolver dependencias o JDK incorrecto.
**Solución**:
```bash
# Limpiar caché de Docker
docker builder prune -a

# Rebuilding con --no-cache
docker build --no-cache -t nakmuay-app .

# Verificar que local compile correctamente primero
.\mvnw clean package
```

### **5. Koyeb no despliega después de push:**
**Síntoma**: Git push exitoso pero Koyeb no construye.
**Causa**: Auto-deploy deshabilitado o webhook no configurado.
**Solución**:
- Ir a Koyeb → Service → Settings → "Auto-deploy" debe estar ON
- Manualmente: Koyeb → Service → "Redeploy"

---

## Pruebas (Testing)

El proyecto incluye `spring-boot-starter-test` con JUnit 5 y Mockito.

### **Ejecutar tests:**
```powershell
.\mvnw test
```

### **Clase de prueba actual:**
- `Proyecto1springApplicationTests.java`: Prueba básica de contexto (verifica que la aplicación arranque)

### **Recomendaciones para expandir testing:**
- **Unit tests**: Servicios (UserServiceImpl, PlanServiceImpl) con Mockito para repositories
- **Integration tests**: Controllers con MockMvc y @SpringBootTest
- **Repository tests**: @DataJpaTest para verificar consultas custom
- **Security tests**: @WithMockUser para probar autenticación/autorización

---
