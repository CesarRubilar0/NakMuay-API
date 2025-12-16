# Defensa del Proyecto: NakMuay - Academia de Muay Thai (Spring Boot)

## 1. Stack Tecnológico y Justificación

### Tecnologías Utilizadas:
- **Java 17**: Versión LTS con características modernas (records, pattern matching, text blocks)
- **Spring Boot 3.5.6**: Framework principal para desarrollo rápido con dependencias integradas
  - `spring-boot-starter-web`: MVC con Tomcat embebido
  - `spring-boot-starter-security`: Autenticación y autorización
  - `spring-boot-starter-data-jpa`: Abstracción de persistencia con Hibernate
  - `spring-boot-starter-thymeleaf`: Motor de plantillas server-side
  - `spring-boot-starter-validation`: Validaciones JSR-303
- **H2 Database**: Base de datos en memoria para desarrollo/testing
- **Maven**: Gestión de dependencias y build (configurado en `pom.xml`)
- **Docker**: Containerización con Dockerfile multi-stage
- **Koyeb**: Plataforma de despliegue cloud conectada a GitHub

### Ubicación en el Proyecto:
- **Dependencias**: `pom.xml` (raíz del proyecto)
- **Configuración**: `src/main/resources/application.properties`
- **Build**: `Dockerfile`, `docker-compose.yml`, `.dockerignore`

## 2. Arquitectura y Capas (MVC + Service + Repository)

### Patrón Arquitectónico:
El proyecto sigue el patrón **MVC (Model-View-Controller)** extendido con capas de servicio y repositorio:

```
Controller → Service → Repository → Database
    ↓
  View (Thymeleaf)
```

### Controllers (Capa de Presentación):
**Ubicación**: `src/main/java/com/example/proyecto1spring/controllers/`

1. **indexController.java**: 
   - Rutas públicas: `/`, `/index`, `/nosotros`, `/galeria`, `/contacto`
   - **Inscripción**: `GET /inscripcion` carga planes activos; `POST /inscripcion` procesa selección de plan + horarios
   - Dependencias: `@Autowired PlanService`, `MembresiaService`, `HorarioEntrenamientoService`

2. **UserController.java** (ADMIN):
   - CRUD usuarios: `/user/list`, `/user/view/{id}`, `/user/edit/{id}`, `/user/delete/{id}`
   - Toggle estado: `/user/toggle/{id}` (activa/desactiva usuario)
   - Protección: `@PreAuthorize("hasRole('ADMIN')")` en cada método

3. **MiPlanController.java** (USER):
   - Gestión membresía personal: `/mi-plan` (ver plan activo)
   - Acciones: `/mi-plan/contratar` (POST), `/mi-plan/cambiar` (POST), `/mi-plan/cancelar` (POST)
   - Horarios: `/mi-plan/horario/agregar` y `/mi-plan/horario/eliminar/{id}` (POST)
   - Seguridad: Requiere `@AuthenticationPrincipal Usuario` (usuario autenticado)

4. **AdminPlanesController.java** (ADMIN):
   - CRUD planes: `/admin/planes`, `/admin/planes/crear`, `/admin/planes/editar/{id}`
   - Acciones: `/admin/planes/eliminar/{id}`, `/admin/planes/toggle-active/{id}`
   - Protección: `@PreAuthorize("hasRole('ADMIN')")`

5. **AdminAlumnosController.java** (ADMIN):
   - Gestión alumnos: `/admin/alumnos` (lista), `/admin/alumnos/ver/{id}` (detalle con membresías)
   - Acciones: `/admin/alumnos/toggle-enabled/{id}`, `/admin/alumnos/eliminar/{id}`

6. **ContactoController.java**:
   - Página de contacto: `/contacto` (GET)

### Services (Capa de Lógica de Negocio):
**Ubicación**: `src/main/java/com/example/proyecto1spring/service/` y `service/impl/`

1. **UserServiceImpl.java**:
   - Métodos: `findAll()`, `findById()`, `save()`, `delete()`, `toggleEnabled()`
   - Lógica: Validación duplicados (email/RUT), encriptación BCrypt, verificación existencia
   - Anotación: `@Transactional` en métodos de escritura

2. **PlanServiceImpl.java**:
   - Métodos: `findAll()`, `findActivePlanes()`, `save()`, `delete()`, `toggleActive()`
   - Lógica: Validación nombre duplicado, verificación membresías asociadas antes de eliminar

3. **MembresiaServiceImpl.java**:
   - Métodos: `crearMembresia()`, `cambiarPlan()`, `cancelarMembresia()`, `findMembresiaActivaByUsuario()`
   - Lógica crítica: 
     - Cálculo automático de fechas (inicio = hoy, fin = inicio + duracionMeses del plan)
     - Validación: solo 1 membresía activa por usuario
     - Cambio de plan: cancela anterior y crea nueva con fechas actualizadas

4. **HorarioEntrenamientoServiceImpl.java**:
   - Métodos: `crearHorarios()`, `findByMembresia()`, `delete()`
   - Lógica: Asignación horarios a membresía, validación conflictos

5. **MyUserDetailsService.java**:
   - Implementa `UserDetailsService` de Spring Security
   - Método: `loadUserByUsername(String email)` → carga `Usuario` desde BD
   - Ubicación: `src/main/java/com/example/proyecto1spring/service/`

### Repositories (Capa de Persistencia):
**Ubicación**: `src/main/java/com/example/proyecto1spring/repository/`

Todos extienden `JpaRepository<Entidad, Long>`:

1. **UserRepository.java**:
   - Query custom: `Optional<Usuario> findByEmail(String email)`
   - Validaciones: `boolean existsByEmail(String email)`, `boolean existsByRut(String rut)`

2. **PlanRepository.java**:
   - Query custom: `List<Plan> findByActivoTrue()` (planes activos)
   - Validación: `boolean existsByNombre(String nombre)`

3. **MembresiaRepository.java**:
   - Queries custom:
     - `Optional<Membresia> findByUsuarioAndActivaTrue(Usuario usuario)`
     - `List<Membresia> findByUsuario(Usuario usuario)` (historial)
     - `List<Membresia> findAllByActivaTrue()` (todas las activas)

4. **HorarioEntrenamientoRepository.java**:
   - Query custom: `List<HorarioEntrenamiento> findByMembresia(Membresia membresia)`

5. **RoleRepository.java**:
   - Query custom: `Optional<Role> findByName(String name)` (busca "ADMIN" o "USER")

### Entities (Modelo de Dominio):
**Ubicación**: `src/main/java/com/example/proyecto1spring/entity/`

1. **Usuario.java** (implementa `UserDetails`):
   - Campos: `id`, `rut`, `nombre`, `apellido`, `email`, `password`, `rol`, `enabled`, `createdAt`, `updatedAt`
   - Relación: `@OneToMany` con `Membresia`
   - Seguridad: Métodos `getAuthorities()`, `isEnabled()` para Spring Security

2. **Role.java**:
   - Campos: `id`, `name` (ADMIN, USER)
   - Sin relaciones JPA (rol almacenado como String en Usuario)

3. **Plan.java**:
   - Campos: `id`, `nombre`, `descripcion`, `precio` (BigDecimal), `duracionMeses`, `activo`, `createdAt`, `updatedAt`
   - Relación: `@OneToMany` con `Membresia`

4. **Membresia.java**:
   - Campos: `id`, `fechaInicio`, `fechaFin`, `activa`, `createdAt`, `updatedAt`
   - Relaciones: `@ManyToOne` con `Usuario` y `Plan`, `@OneToMany` con `HorarioEntrenamiento`

5. **HorarioEntrenamiento.java**:
   - Campos: `id`, `dia`, `hora`
   - Relación: `@ManyToOne` con `Membresia`

### Config (Configuración):
**Ubicación**: `src/main/java/com/example/proyecto1spring/config/`

1. **SecurityConfig.java**:
   - Beans: `SecurityFilterChain`, `BCryptPasswordEncoder`
   - Configuración: formLogin, rutas públicas/protegidas, CSRF

2. **GlobalModelAttributes.java**:
   - `@ControllerAdvice` que expone `isAuthenticated` e `isAdmin` a todas las vistas Thymeleaf

### Util (Utilidades):
**Ubicación**: `src/main/java/com/example/proyecto1spring/util/`

1. **DataLoader.java** (implementa `CommandLineRunner`):
   - Carga inicial: 2 roles, 1 admin, 4 planes
   - Se ejecuta automáticamente al arrancar Spring Boot

## 3. Seguridad (Spring Security)

### Implementación Completa:
**Archivo principal**: `src/main/java/com/example/proyecto1spring/config/SecurityConfig.java`

### Configuración de Seguridad:

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Habilita @PreAuthorize
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index", "/login", "/registro", 
                                "/user/create", "/nosotros", "/galeria", 
                                "/contacto", "/css/**", "/js/**", "/img/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/inscripcion", true)  // Redirige tras login exitoso
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?logout")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")  // Desactiva CSRF para consola H2
            );
        
        // Permite frames para H2 Console
        http.headers(headers -> headers.frameOptions().sameOrigin());
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Algoritmo de encriptación
    }
}
```

### Autenticación (¿Cómo se autentica un usuario?):

1. **UserDetailsService personalizado**:
   - **Archivo**: `src/main/java/com/example/proyecto1spring/service/MyUserDetailsService.java`
   - **Implementación**:
     ```java
     @Service
     public class MyUserDetailsService implements UserDetailsService {
         @Autowired
         private UserRepository userRepository;
         
         @Override
         public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
             Usuario usuario = userRepository.findByEmail(email)
                 .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
             return usuario;  // Usuario implementa UserDetails
         }
     }
     ```
   - **Flujo**: Spring Security llama a este método con el email del formulario → busca en BD → compara password encriptada con BCrypt

2. **Entidad Usuario implementa UserDetails**:
   - **Archivo**: `src/main/java/com/example/proyecto1spring/entity/Usuario.java`
   - **Métodos implementados**:
     ```java
     @Override
     public Collection<? extends GrantedAuthority> getAuthorities() {
         return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol));
     }
     
     @Override
     public String getUsername() {
         return email;  // Email como username
     }
     
     @Override
     public boolean isEnabled() {
         return enabled;  // Campo enabled controla acceso
     }
     ```

3. **Encriptación de contraseñas**:
   - **Bean**: `BCryptPasswordEncoder` definido en `SecurityConfig.java`
   - **Uso en servicio**: `src/main/java/com/example/proyecto1spring/service/impl/UserServiceImpl.java`
     ```java
     @Autowired
     private PasswordEncoder passwordEncoder;
     
     public Usuario save(Usuario usuario) {
         usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
         return userRepository.save(usuario);
     }
     ```
   - **Verificación**: Spring Security compara automáticamente la contraseña del login con el hash almacenado

### Autorización (¿Cómo se protegen rutas?):

1. **Por configuración en SecurityFilterChain**:
   - Rutas públicas: `permitAll()` para `/`, `/login`, `/registro`, recursos estáticos
   - Rutas autenticadas: `anyRequest().authenticated()` (ej: `/inscripcion`, `/mi-plan`)
   - Rutas admin: `.requestMatchers("/admin/**").hasRole("ADMIN")`

2. **Por anotación @PreAuthorize**:
   - **Ejemplo en UserController.java**:
     ```java
     @Controller
     public class UserController {
         
         @PreAuthorize("hasRole('ADMIN')")
         @GetMapping("/user/list")
         public String listUsers(Model model) { ... }
         
         @PreAuthorize("hasRole('ADMIN')")
         @PostMapping("/user/delete/{id}")
         public String deleteUser(@PathVariable Long id) { ... }
     }
     ```
   - **Ventaja**: Control granular por método, rechaza acceso antes de ejecutar lógica

3. **Roles disponibles**:
   - **ADMIN**: Acceso total (CRUD usuarios, planes, alumnos)
   - **USER**: Acceso a inscripción, gestión de su propia membresía (`/mi-plan`)

### CSRF (Cross-Site Request Forgery):

1. **¿Qué es?**: Ataque que fuerza a un usuario autenticado a ejecutar acciones no deseadas
2. **Protección automática**: Spring Security genera token CSRF en cada sesión
3. **En formularios Thymeleaf**:
   - **Ubicación**: Todas las plantillas en `src/main/resources/templates/`
   - **Implementación automática**: Thymeleaf incluye token con `th:action`
     ```html
     <form th:action="@{/mi-plan/contratar}" method="post">
         <!-- Token CSRF incluido automáticamente -->
         <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
     </form>
     ```
4. **Excepción**: Deshabilitado para `/h2-console/**` (solo desarrollo)

### Sesión y Logout:

1. **Sesión HTTP**: Gestionada automáticamente por Spring Security
2. **Logout**:
   - **Ruta**: `POST /logout` (configurado en SecurityConfig)
   - **Efecto**: Invalida sesión, elimina cookies de autenticación
   - **Redirección**: `/?logout`

### Usuario Administrador por Defecto:
- **Creado en**: `src/main/java/com/example/proyecto1spring/util/DataLoader.java`
- **Credenciales**:
  ```
  Email: admin@example.com
  Contraseña: admin (encriptada con BCrypt al guardar)
  Rol: ADMIN
  ```
- **Ejecución**: Al iniciar aplicación (implementa CommandLineRunner)

## 4. Persistencia y Modelo de Datos

### Base de Datos H2:
**Configuración**: `src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:h2:mem:demo
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always
```

**Características**:
- **En memoria**: Datos se pierden al reiniciar (ideal para desarrollo/testing)
- **DDL-Auto**: `update` crea/actualiza tablas automáticamente desde entidades JPA
- **Consola web**: Accesible en `http://localhost:8080/h2-console` (solo desarrollo)

### Entidades JPA (Modelo de Dominio):

#### 1. Usuario.java
**Ubicación**: `src/main/java/com/example/proyecto1spring/entity/Usuario.java`

```java
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String rut;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String apellido;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;  // Encriptada con BCrypt
    
    @Column(nullable = false)
    private String rol;  // "ADMIN" o "USER"
    
    @Column(nullable = false)
    private Boolean enabled = true;  // Activar/desactivar usuario
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Membresia> membresias;  // Historial de membresías
}
```

**Validaciones implementadas** (en UserServiceImpl):
- Email único: `userRepository.existsByEmail(email)`
- RUT único: `userRepository.existsByRut(rut)`
- Password encriptada antes de guardar

#### 2. Role.java
**Ubicación**: `src/main/java/com/example/proyecto1spring/entity/Role.java`

```java
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;  // "ADMIN" o "USER"
}
```

**Creación**: En `DataLoader.java` al iniciar aplicación
- Roles predefinidos: ADMIN, USER

#### 3. Plan.java
**Ubicación**: `src/main/java/com/example/proyecto1spring/entity/Plan.java`

```java
@Entity
@Table(name = "planes")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String nombre;  // "Plan Básico", "Plan Estándar", etc.
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;  // BigDecimal para precisión en dinero
    
    @Column(nullable = false)
    private Integer duracionMeses;  // 1, 3, 6, 12 meses
    
    @Column(nullable = false)
    private Boolean activo = true;  // Activar/desactivar plan
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<Membresia> membresias;
}
```

**Planes precargados** (en DataLoader.java):
- Plan Básico: $25,000 / 1 mes
- Plan Estándar: $45,000 / 3 meses
- Plan Premium: $75,000 / 6 meses
- Plan Anual: $500,000 / 12 meses

#### 4. Membresia.java
**Ubicación**: `src/main/java/com/example/proyecto1spring/entity/Membresia.java`

```java
@Entity
@Table(name = "membresias")
public class Membresia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;
    
    @Column(nullable = false)
    private LocalDate fechaInicio;  // Calculada automáticamente (hoy)
    
    @Column(nullable = false)
    private LocalDate fechaFin;  // fechaInicio + duracionMeses del plan
    
    @Column(nullable = false)
    private Boolean activa = true;  // Solo 1 activa por usuario
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "membresia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorarioEntrenamiento> horarios;
}
```

**Lógica de negocio** (en MembresiaServiceImpl):
- **Cálculo de fechas**:
  ```java
  LocalDate fechaInicio = LocalDate.now();
  LocalDate fechaFin = fechaInicio.plusMonths(plan.getDuracionMeses());
  ```
- **Restricción**: Solo 1 membresía activa por usuario
  ```java
  Optional<Membresia> activa = membresiaRepository.findByUsuarioAndActivaTrue(usuario);
  if (activa.isPresent()) {
      throw new IllegalStateException("El usuario ya tiene una membresía activa");
  }
  ```

#### 5. HorarioEntrenamiento.java
**Ubicación**: `src/main/java/com/example/proyecto1spring/entity/HorarioEntrenamiento.java`

```java
@Entity
@Table(name = "horarios_entrenamiento")
public class HorarioEntrenamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membresia_id", nullable = false)
    private Membresia membresia;
    
    @Column(nullable = false)
    private String dia;  // "Lunes", "Martes", ..., "Viernes"
    
    @Column(nullable = false)
    private String hora;  // "09:00", "11:00", "15:00", "18:00", "20:00"
}
```

**Creación**: En `/inscripcion` (POST), usuario selecciona múltiples horarios
- Arrays recibidos: `dias[]` y `horarios[]`
- Procesamiento en `indexController.java`:
  ```java
  for (int i = 0; i < dias.length; i++) {
      HorarioEntrenamiento horario = new HorarioEntrenamiento();
      horario.setMembresia(membresia);
      horario.setDia(dias[i]);
      horario.setHora(horarios[i]);
      horarioService.save(horario);
  }
  ```

### Relaciones JPA:

```
Usuario (1) ←→ (N) Membresia (N) ←→ (1) Plan
                      ↓
                     (N)
             HorarioEntrenamiento
```

- **Usuario ↔ Membresia**: `@OneToMany` / `@ManyToOne` (un usuario tiene historial de membresías)
- **Plan ↔ Membresia**: `@OneToMany` / `@ManyToOne` (un plan puede tener múltiples suscripciones)
- **Membresia ↔ HorarioEntrenamiento**: `@OneToMany` / `@ManyToOne` con `cascade` y `orphanRemoval`

### Repositorios (Spring Data JPA):

#### UserRepository.java
```java
public interface UserRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByRut(String rut);
}
```

#### PlanRepository.java
```java
public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByActivoTrue();  // Planes activos para inscripción
    boolean existsByNombre(String nombre);
}
```

#### MembresiaRepository.java
```java
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {
    Optional<Membresia> findByUsuarioAndActivaTrue(Usuario usuario);
    List<Membresia> findByUsuario(Usuario usuario);  // Historial
    List<Membresia> findAllByActivaTrue();  // Admin: todas las activas
}
```

#### HorarioEntrenamientoRepository.java
```java
public interface HorarioEntrenamientoRepository extends JpaRepository<HorarioEntrenamiento, Long> {
    List<HorarioEntrenamiento> findByMembresia(Membresia membresia);
}
```

#### RoleRepository.java
```java
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
```

### Validaciones Implementadas:

1. **En UserServiceImpl** (`src/main/java/.../service/impl/UserServiceImpl.java`):
   ```java
   if (userRepository.existsByEmail(usuario.getEmail())) {
       throw new IllegalArgumentException("El email ya está registrado");
   }
   if (userRepository.existsByRut(usuario.getRut())) {
       throw new IllegalArgumentException("El RUT ya está registrado");
   }
   ```

2. **En PlanServiceImpl**:
   ```java
   if (planRepository.existsByNombre(plan.getNombre())) {
       throw new IllegalArgumentException("Ya existe un plan con ese nombre");
   }
   ```

3. **En MembresiaServiceImpl**:
   ```java
   Optional<Membresia> membresiaActiva = membresiaRepository.findByUsuarioAndActivaTrue(usuario);
   if (membresiaActiva.isPresent()) {
       throw new IllegalStateException("Ya tienes una membresía activa");
   }
   ```

## 5. CRUD y Transaccionalidad

### ¿Qué es @Transactional?
Anotación que garantiza **atomicidad**: todas las operaciones dentro del método se completan exitosamente o se revierten por completo si hay error.

**Ubicación**: Se aplica en métodos de escritura de los servicios (package `service/impl/`)

### Implementación por Entidad:

---

### CRUD de Usuarios:

**Archivo**: `src/main/java/com/example/proyecto1spring/service/impl/UserServiceImpl.java`

#### Crear Usuario:
```java
@Override
@Transactional  // Si falla, revierte todo
public Usuario save(Usuario usuario) {
    // Validaciones
    if (userRepository.existsByEmail(usuario.getEmail())) {
        throw new IllegalArgumentException("El email ya está registrado");
    }
    if (userRepository.existsByRut(usuario.getRut())) {
        throw new IllegalArgumentException("El RUT ya está registrado");
    }
    
    // Encriptar contraseña
    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
    
    // Timestamps
    if (usuario.getId() == null) {
        usuario.setCreatedAt(LocalDateTime.now());
    }
    usuario.setUpdatedAt(LocalDateTime.now());
    
    return userRepository.save(usuario);
}
```

**Controller**: `UserController.java` → `POST /user/create`
- **Ubicación**: `src/main/java/.../controllers/UserController.java`
- **Protección**: `@PreAuthorize("hasRole('ADMIN')")` (solo admin puede crear usuarios desde panel)
- **Vista**: `templates/user_create.html`

#### Leer Usuarios:
```java
@Override
public List<Usuario> findAll() {
    return userRepository.findAll();
}

@Override
public Optional<Usuario> findById(Long id) {
    return userRepository.findById(id);
}
```

**Controller**: `GET /user/list` (listar), `GET /user/view/{id}` (ver detalle)

#### Actualizar Usuario:
```java
@Override
@Transactional
public Usuario save(Usuario usuario) {
    // Mismo método de crear, pero si tiene ID, actualiza
    Usuario existente = userRepository.findById(usuario.getId())
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    
    // Validar email duplicado (excepto el mismo usuario)
    Optional<Usuario> conEmail = userRepository.findByEmail(usuario.getEmail());
    if (conEmail.isPresent() && !conEmail.get().getId().equals(usuario.getId())) {
        throw new IllegalArgumentException("El email ya está en uso");
    }
    
    usuario.setUpdatedAt(LocalDateTime.now());
    return userRepository.save(usuario);
}
```

**Controller**: `POST /user/edit/{id}`
- **Vista**: `templates/user_edit.html`

#### Eliminar Usuario:
```java
@Override
@Transactional
public void delete(Long id) {
    Usuario usuario = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    userRepository.delete(usuario);
}
```

**Controller**: `GET /user/delete/{id}` (redirige tras confirmar)

#### Activar/Desactivar Usuario:
```java
@Override
@Transactional
public void toggleEnabled(Long id) {
    Usuario usuario = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    usuario.setEnabled(!usuario.getEnabled());  // Alterna estado
    usuario.setUpdatedAt(LocalDateTime.now());
    userRepository.save(usuario);
}
```

**Controller**: `GET /user/toggle/{id}`
- **Efecto**: Si `enabled=true` → cambia a `false` (bloquea login), y viceversa

---

### CRUD de Planes:

**Archivo**: `src/main/java/com/example/proyecto1spring/service/impl/PlanServiceImpl.java`

#### Crear/Editar Plan:
```java
@Override
@Transactional
public Plan save(Plan plan) {
    // Validar nombre duplicado
    if (plan.getId() == null && planRepository.existsByNombre(plan.getNombre())) {
        throw new IllegalArgumentException("Ya existe un plan con ese nombre");
    }
    
    if (plan.getId() == null) {
        plan.setCreatedAt(LocalDateTime.now());
    }
    plan.setUpdatedAt(LocalDateTime.now());
    
    return planRepository.save(plan);
}
```

**Controller**: `AdminPlanesController.java`
- `POST /admin/planes/crear`
- `POST /admin/planes/editar/{id}`
- **Vista**: `templates/admin_planes_form.html`

#### Listar Planes:
```java
@Override
public List<Plan> findAll() {
    return planRepository.findAll();
}

@Override
public List<Plan> findActivePlanes() {
    return planRepository.findByActivoTrue();  // Solo planes activos
}
```

**Controller**:
- `GET /admin/planes` (admin: todos los planes)
- `GET /inscripcion` (usuario: solo planes activos)

#### Eliminar Plan:
```java
@Override
@Transactional
public void delete(Long id) {
    Plan plan = planRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado"));
    
    // Verificar si tiene membresías asociadas
    if (!plan.getMembresias().isEmpty()) {
        throw new IllegalStateException("No se puede eliminar un plan con membresías activas");
    }
    
    planRepository.delete(plan);
}
```

**Controller**: `POST /admin/planes/eliminar/{id}`

#### Activar/Desactivar Plan:
```java
@Override
@Transactional
public void toggleActive(Long id) {
    Plan plan = planRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado"));
    plan.setActivo(!plan.getActivo());
    plan.setUpdatedAt(LocalDateTime.now());
    planRepository.save(plan);
}
```

**Controller**: `POST /admin/planes/toggle-active/{id}`
- **Efecto**: Oculta/muestra plan en `/inscripcion` sin eliminarlo

---

### CRUD de Membresías:

**Archivo**: `src/main/java/com/example/proyecto1spring/service/impl/MembresiaServiceImpl.java`

#### Crear Membresía (Contratar Plan):
```java
@Override
@Transactional
public Membresia crearMembresia(Usuario usuario, Plan plan) {
    // Validar: solo 1 membresía activa por usuario
    Optional<Membresia> activa = membresiaRepository.findByUsuarioAndActivaTrue(usuario);
    if (activa.isPresent()) {
        throw new IllegalStateException("Ya tienes una membresía activa");
    }
    
    // Crear membresía con fechas automáticas
    Membresia membresia = new Membresia();
    membresia.setUsuario(usuario);
    membresia.setPlan(plan);
    membresia.setFechaInicio(LocalDate.now());
    membresia.setFechaFin(LocalDate.now().plusMonths(plan.getDuracionMeses()));
    membresia.setActiva(true);
    membresia.setCreatedAt(LocalDateTime.now());
    
    return membresiaRepository.save(membresia);
}
```

**Controller**: `MiPlanController.java` → `POST /mi-plan/contratar`
- **Parámetros**: `planId` (Long)
- **Usuario**: Obtenido de `@AuthenticationPrincipal Usuario usuario`
- **Vista**: `templates/mi_plan.html`

#### Cambiar Plan:
```java
@Override
@Transactional
public Membresia cambiarPlan(Usuario usuario, Plan nuevoPlan) {
    // Cancelar membresía actual
    Membresia actual = membresiaRepository.findByUsuarioAndActivaTrue(usuario)
        .orElseThrow(() -> new IllegalStateException("No tienes membresía activa"));
    
    actual.setActiva(false);
    actual.setUpdatedAt(LocalDateTime.now());
    membresiaRepository.save(actual);
    
    // Crear nueva membresía con fechas actualizadas
    return crearMembresia(usuario, nuevoPlan);
}
```

**Controller**: `POST /mi-plan/cambiar`
- **Efecto**: Cancela plan actual y crea uno nuevo con fechas desde hoy

#### Cancelar Membresía:
```java
@Override
@Transactional
public void cancelarMembresia(Usuario usuario) {
    Membresia activa = membresiaRepository.findByUsuarioAndActivaTrue(usuario)
        .orElseThrow(() -> new IllegalStateException("No tienes membresía activa"));
    
    activa.setActiva(false);
    activa.setUpdatedAt(LocalDateTime.now());
    membresiaRepository.save(activa);
}
```

**Controller**: `POST /mi-plan/cancelar`
- **Efecto**: Marca `activa=false`, usuario pierde acceso a beneficios del plan

#### Consultar Membresía Activa:
```java
@Override
public Optional<Membresia> findMembresiaActivaByUsuario(Usuario usuario) {
    return membresiaRepository.findByUsuarioAndActivaTrue(usuario);
}
```

**Controller**: `GET /mi-plan`
- **Vista**: Muestra plan activo, fechas inicio/fin, horarios
- **Condicional**: Si no tiene membresía activa, muestra botón "Contratar Plan"

---

### CRUD de Horarios:

**Archivo**: `src/main/java/com/example/proyecto1spring/service/impl/HorarioEntrenamientoServiceImpl.java`

#### Crear Horarios:
```java
@Override
@Transactional
public HorarioEntrenamiento save(HorarioEntrenamiento horario) {
    return horarioRepository.save(horario);
}
```

**Controller**: `indexController.java` → `POST /inscripcion`
- **Proceso**: 
  1. Crea membresía
  2. Recibe arrays `dias[]` y `horarios[]`
  3. Loop: crea un HorarioEntrenamiento por cada par día/hora
- **Vista**: `templates/inscripcion.html` (formulario con checkboxes múltiples)

#### Agregar Horario a Membresía Existente:
```java
// Mismo método save, pero desde MiPlanController
```

**Controller**: `POST /mi-plan/horario/agregar`
- **Parámetros**: `dia`, `hora`
- **Efecto**: Agrega horario adicional a membresía activa

#### Eliminar Horario:
```java
@Override
@Transactional
public void delete(Long id) {
    HorarioEntrenamiento horario = horarioRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Horario no encontrado"));
    horarioRepository.delete(horario);
}
```

**Controller**: `POST /mi-plan/horario/eliminar/{id}`
- **Efecto**: Elimina un horario específico (usuario puede ajustar su agenda)

---

### Resumen de Transaccionalidad:

| Operación | Transaccional | Razón |
|---|---|---|
| `findAll()`, `findById()` | ❌ No | Solo lectura, no modifica BD |
| `save()`, `update()` | ✅ Sí | Escritura, requiere atomicidad |
| `delete()` | ✅ Sí | Eliminación, puede afectar relaciones |
| `toggleEnabled()`, `toggleActive()` | ✅ Sí | Actualización de estado |
| `crearMembresia()` | ✅ Sí | Crea múltiples registros (membresía + horarios) |
| `cambiarPlan()` | ✅ Sí | Cancela membresía actual + crea nueva (2 operaciones) |

**Ventaja**: Si falla cualquier paso dentro de un método `@Transactional`, la BD revierte a estado anterior (rollback automático).

## 6. Docker y Despliegue

### Dockerfile (Multi-Stage Build):

**Ubicación**: `Dockerfile` en la raíz del proyecto

```dockerfile
# ========== STAGE 1: BUILD ==========
FROM maven:3.9.5-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copiar archivos de configuración Maven
COPY pom.xml .
COPY src ./src

# Compilar proyecto (sin ejecutar tests para velocidad)
RUN mvn clean package -DskipTests

# ========== STAGE 2: RUNTIME ==========
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar JAR compilado desde stage anterior
COPY --from=builder /app/target/*.jar app.jar

# Exponer puerto 8080
EXPOSE 8080

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Ventajas del Multi-Stage Build:

1. **Imagen final ligera**: 
   - Stage 1 (builder): ~500MB con Maven + JDK 17 completo
   - Stage 2 (runtime): ~150MB solo con JRE 17 Alpine
   - **Resultado**: Solo el JAR ejecutable en imagen final

2. **Seguridad**:
   - No incluye herramientas de compilación en producción
   - Menos superficie de ataque
   - Alpine Linux: distribución mínima con menos vulnerabilidades

3. **Reproducibilidad**:
   - Build consistente en cualquier máquina
   - No depende de Maven instalado localmente

### .dockerignore:

**Ubicación**: `.dockerignore` en la raíz

```
target/
.mvn/
.git/
.gitignore
*.md
README.md
mvnw
mvnw.cmd
.vscode/
.idea/
*.iml
```

**Propósito**: Excluir archivos innecesarios del contexto Docker para reducir tamaño y tiempo de build

### docker-compose.yml:

**Ubicación**: `docker-compose.yml` en la raíz

```yaml
version: '3.8'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SERVER_PORT=8080
    restart: unless-stopped
```

**Uso**: Simplifica ejecución local con Docker
```bash
docker-compose up -d      # Inicia en background
docker-compose logs -f     # Ver logs en tiempo real
docker-compose down        # Detener servicios
```

### Configuración para Despliegue:

**Archivo**: `src/main/resources/application.properties`

```properties
# Puerto dinámico (Koyeb asigna variable PORT)
server.port=${PORT:8080}

# H2 en memoria (para desarrollo/demo)
spring.datasource.url=jdbc:h2:mem:demo
spring.h2.console.enabled=true

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always
```

**Clave**: `${PORT:8080}` → usa variable de entorno `PORT` si existe, sino 8080 por defecto

### Comandos Docker:

#### Build local:
```bash
docker build -t nakmuay-app .
```
- **Proceso**: Ejecuta ambos stages, crea imagen `nakmuay-app:latest`
- **Tiempo**: ~3-5 minutos (primera vez, luego cachea capas)

#### Ejecutar contenedor:
```bash
docker run -p 8080:8080 nakmuay-app
```
- **Efecto**: Inicia aplicación, accesible en `http://localhost:8080`
- **Logs**: Se muestran en terminal

#### Ejecutar en background:
```bash
docker run -d -p 8080:8080 --name nakmuay-container nakmuay-app
```
- `-d`: Detached mode (background)
- `--name`: Nombre personalizado para el contenedor

#### Ver logs:
```bash
docker logs nakmuay-container
docker logs -f nakmuay-container  # Follow (tiempo real)
```

#### Detener/eliminar:
```bash
docker stop nakmuay-container
docker rm nakmuay-container
docker rmi nakmuay-app  # Eliminar imagen
```

---

### Despliegue en Koyeb:

**Koyeb**: Plataforma cloud que ejecuta contenedores Docker con auto-deploy desde GitHub

#### Configuración Inicial:

1. **Conectar GitHub a Koyeb**:
   - Ir a [app.koyeb.com](https://app.koyeb.com)
   - Click "Create Service" → "GitHub"
   - Autorizar acceso al repositorio: `CesarRubilar0/Sring_proy`

2. **Configuración del Servicio**:
   - **Service name**: `nakmuay-app`
   - **Region**: Elegir (ej: Frankfurt, Washington)
   - **Build method**: Docker (Koyeb detecta automáticamente `Dockerfile`)
   - **Builder**: Docker
   - **Dockerfile path**: `/Dockerfile` (por defecto)
   - **Port**: 8080 (debe coincidir con `EXPOSE 8080` en Dockerfile)

3. **Variables de Entorno** (opcional):
   ```
   SPRING_PROFILES_ACTIVE=prod
   SERVER_PORT=8080
   ```
   - **Ubicación en Koyeb**: Service Settings → Environment variables

4. **Health Check**:
   - **Path**: `/` (verifica que la app responda)
   - **Port**: 8080
   - **Timeout**: 30 segundos

#### Flujo de Auto-Deploy:

```
1. git add . && git commit -m "Actualización"
2. git push origin main
3. Koyeb detecta cambio en repositorio
4. Trigger: Inicia build Docker
5. Build: Ejecuta Dockerfile (Stage 1 + 2)
6. Test: Health check en /
7. Deploy: Sustituye versión anterior
8. URL: https://nakmuay-app-<tu-usuario>.koyeb.app
```

**Tiempo total**: ~3-5 minutos desde push hasta disponible

#### Verificación Post-Deploy:

1. **Logs en Koyeb**:
   - Dashboard → Service → "Logs" tab
   - Buscar: "✅ 4 planes creados exitosamente" (confirma DataLoader ejecutado)

2. **Prueba funcional**:
   ```
   https://<tu-app>.koyeb.app/
   https://<tu-app>.koyeb.app/login
   https://<tu-app>.koyeb.app/inscripcion
   ```

3. **Login con admin**:
   ```
   Email: admin@example.com
   Contraseña: admin
   ```

4. **Verificar planes**:
   - Ir a `/inscripcion` → Dropdown debe mostrar 4 planes
   - Ir a `/admin/planes` → Lista completa con opciones CRUD

#### Solución de Problemas en Koyeb:

| Problema | Causa | Solución |
|---|---|---|
| **Build fails** | Error en Maven dependencies | Revisar "Build logs" → verificar `pom.xml` |
| **App no inicia** | Puerto incorrecto | Verificar `EXPOSE 8080` en Dockerfile y `server.port=${PORT:8080}` en properties |
| **404 en todas las rutas** | JAR no ejecuta correctamente | Revisar "Runtime logs" → verificar que Spring Boot arranque |
| **No aparecen planes** | DataLoader no ejecutó | Buscar en logs "4 planes creados" → si no aparece, revisar `application.properties` |
| **Auto-deploy no funciona** | Webhook deshabilitado | Koyeb → Service → Settings → "Auto-deploy from GitHub" = ON |

#### Comandos Git para Deploy:

```bash
# Ver cambios
git status

# Agregar cambios
git add .

# Commit con mensaje descriptivo
git commit -m "Agregar funcionalidad de horarios personalizados"

# Push (trigger auto-deploy)
git push origin main

# Ver estado del deploy
# → Ir al dashboard de Koyeb en el navegador
```

#### Rollback (si deploy falla):

1. Koyeb → Service → "Deployments" tab
2. Seleccionar versión anterior funcional
3. Click "Redeploy"

---

### Ventajas de Docker + Koyeb:

1. **Portabilidad**: Misma imagen funciona en local, staging, producción
2. **Aislamiento**: Aplicación con sus dependencias en contenedor independiente
3. **Escalabilidad**: Koyeb puede escalar automáticamente según tráfico
4. **CI/CD**: Auto-deploy elimina pasos manuales
5. **Monitoreo**: Logs centralizados en Koyeb dashboard

## 7. Banco de Preguntas y Respuestas Técnicas

### Seguridad:

#### ¿Por qué usar Spring Security?
**Respuesta**: Spring Security es el estándar de facto en el ecosistema Spring para autenticación y autorización. Proporciona:
- **Integración nativa** con Spring Boot (solo agregar dependencia)
- **Protección automática**: CSRF, session fixation, XSS
- **Flexibilidad**: Soporta formularios, OAuth2, JWT, LDAP
- **Comunidad activa**: Ampliamente probado en producción

**Ubicación en el proyecto**: 
- Dependencia: `spring-boot-starter-security` en `pom.xml`
- Configuración: `src/main/java/.../config/SecurityConfig.java`

#### ¿Cómo se protegen las rutas?
**Respuesta**: Dos mecanismos combinados:

1. **Por configuración en SecurityFilterChain** (`SecurityConfig.java`):
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/", "/login", "/registro").permitAll()
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .anyRequest().authenticated()
)
```

2. **Por anotación @PreAuthorize** (en controllers):
```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/user/list")
public String listUsers() { ... }
```

**Ventaja**: @PreAuthorize ejecuta verificación antes del método, rechaza acceso sin ejecutar lógica

#### ¿Cómo se autentica un usuario?
**Respuesta**: Flujo completo:

1. **Usuario envía credenciales** en `/login` (email + password)
2. **Spring Security intercepta** la petición
3. **MyUserDetailsService** (`service/MyUserDetailsService.java`) carga usuario por email:
```java
Usuario usuario = userRepository.findByEmail(email)
    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
return usuario;  // Usuario implementa UserDetails
```
4. **Comparación**: Spring Security compara password encriptada (BCrypt) almacenada vs la ingresada
5. **Sesión**: Si coincide, crea sesión HTTP con `Authentication` en `SecurityContext`
6. **Redirección**: `defaultSuccessUrl("/inscripcion")` redirige tras login exitoso

**Ubicaciones clave**:
- UserDetailsService: `service/MyUserDetailsService.java`
- Entidad: `entity/Usuario.java` (implementa `UserDetails`)
- Configuración login: `SecurityConfig.java` → `formLogin()`

#### ¿Dónde se define el encoder de passwords?
**Respuesta**: Bean en `SecurityConfig.java`:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**Uso**:
- **Al guardar usuario** (`UserServiceImpl.java`):
```java
@Autowired
private PasswordEncoder passwordEncoder;

usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
```

- **Al autenticar**: Spring Security usa el mismo bean automáticamente para comparar

**Algoritmo BCrypt**: Hash con salt (valor aleatorio único por contraseña), resistente a rainbow tables

#### ¿Qué es CSRF y cómo se maneja?
**Respuesta**: 
**CSRF (Cross-Site Request Forgery)**: Ataque que fuerza a un usuario autenticado a ejecutar acciones no deseadas (ej: cambiar contraseña, transferir dinero)

**Protección en Spring Security**:
- **Habilitado por defecto**: Genera token único por sesión
- **En formularios Thymeleaf**: Token incluido automáticamente con `th:action`
```html
<form th:action="@{/mi-plan/contratar}" method="post">
    <!-- Spring Security inyecta esto automáticamente: -->
    <input type="hidden" name="_csrf" value="<token-aleatorio>">
</form>
```

**Excepción**: Deshabilitado para `/h2-console/**` (solo desarrollo):
```java
.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
```

**Ubicación**: `SecurityConfig.java` → método `filterChain()`

---

### Persistencia:

#### ¿Por qué usar H2?
**Respuesta**:
- **Desarrollo rápido**: No requiere instalación de BD externa (MySQL, Postgres)
- **Testing**: Datos se resetean automáticamente entre ejecuciones
- **Consola web**: `/h2-console` permite inspeccionar BD en tiempo real
- **Portabilidad**: Funciona igual en cualquier máquina sin configuración

**Limitaciones**:
- **No persiste datos**: En memoria (`jdbc:h2:mem:demo`), se pierde al reiniciar
- **Producción**: Se recomienda cambiar a PostgreSQL o MySQL con archivo de configuración separado

**Configuración**: `application.properties`
```properties
spring.datasource.url=jdbc:h2:mem:demo
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

#### ¿Qué hace @Transactional?
**Respuesta**: Garantiza **atomicidad** en operaciones de BD:
- **Todas las operaciones exitosas → COMMIT** (se aplican cambios)
- **Alguna operación falla → ROLLBACK** (se revierten todos los cambios)

**Ejemplo práctico** (`MembresiaServiceImpl.java`):
```java
@Override
@Transactional
public Membresia cambiarPlan(Usuario usuario, Plan nuevoPlan) {
    // 1. Cancelar membresía actual
    Membresia actual = membresiaRepository.findByUsuarioAndActivaTrue(usuario)
        .orElseThrow(() -> new IllegalStateException("No tienes membresía activa"));
    actual.setActiva(false);
    membresiaRepository.save(actual);  // UPDATE membresias SET activa=false
    
    // 2. Crear nueva membresía
    return crearMembresia(usuario, nuevoPlan);  // INSERT nueva membresía
}
```

**Escenario sin @Transactional**: Si falla el paso 2, el paso 1 ya se aplicó → usuario queda sin plan activo
**Con @Transactional**: Si falla paso 2, paso 1 también se revierte → usuario mantiene plan original

**Ubicación**: Métodos de escritura en `service/impl/` (UserServiceImpl, PlanServiceImpl, MembresiaServiceImpl)

#### ¿Cómo se manejan duplicados?
**Respuesta**: Validaciones en capa de servicio antes de guardar:

1. **Email duplicado** (`UserServiceImpl.java`):
```java
if (userRepository.existsByEmail(usuario.getEmail())) {
    throw new IllegalArgumentException("El email ya está registrado");
}
```

2. **RUT duplicado** (`UserServiceImpl.java`):
```java
if (userRepository.existsByRut(usuario.getRut())) {
    throw new IllegalArgumentException("El RUT ya está registrado");
}
```

3. **Nombre de plan duplicado** (`PlanServiceImpl.java`):
```java
if (planRepository.existsByNombre(plan.getNombre())) {
    throw new IllegalArgumentException("Ya existe un plan con ese nombre");
}
```

**Queries custom en repositories**:
```java
// UserRepository.java
boolean existsByEmail(String email);
boolean existsByRut(String rut);

// PlanRepository.java
boolean existsByNombre(String nombre);
```

**Manejo en controller**: Try-catch captura exception y muestra mensaje en vista con `RedirectAttributes`

---

### Arquitectura:

#### ¿Cómo se estructura el MVC?
**Respuesta**: Patrón en capas con separación de responsabilidades:

```
Cliente (Browser)
    ↓ HTTP Request
Controller (indexController.java)
    ↓ Llama método de negocio
Service (MembresiaServiceImpl.java)
    ↓ Operaciones BD
Repository (MembresiaRepository.java)
    ↓ Query JPA
Database (H2)
    ↓ Resultado
Service → procesa
    ↓ Retorna objeto
Controller → agrega al Model
    ↓ Retorna nombre vista
View (Thymeleaf: inscripcion.html)
    ↓ HTTP Response
Cliente (HTML renderizado)
```

**Ejemplo concreto** (POST /inscripcion):

1. **Controller** (`indexController.java`):
```java
@PostMapping("/inscripcion")
public String procesarInscripcion(@RequestParam Long planId,
                                  @RequestParam String[] dias,
                                  @RequestParam String[] horarios,
                                  @AuthenticationPrincipal Usuario usuario) {
    // Validaciones básicas
    Plan plan = planService.findById(planId).orElseThrow();
    
    // Llamar servicio
    Membresia membresia = membresiaService.crearMembresia(usuario, plan);
    
    // Crear horarios
    for (int i = 0; i < dias.length; i++) {
        HorarioEntrenamiento horario = new HorarioEntrenamiento();
        horario.setMembresia(membresia);
        horario.setDia(dias[i]);
        horario.setHora(horarios[i]);
        horarioService.save(horario);
    }
    
    return "redirect:/mi-plan";
}
```

2. **Service** (`MembresiaServiceImpl.java`):
```java
@Transactional
public Membresia crearMembresia(Usuario usuario, Plan plan) {
    // Lógica de negocio: validar membresía activa
    // Calcular fechas
    // Guardar en BD
}
```

3. **Repository** (`MembresiaRepository.java`):
```java
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {
    Optional<Membresia> findByUsuarioAndActivaTrue(Usuario usuario);
}
```

---

### Funcionalidades del Sistema:

#### ¿Cómo funciona el sistema de membresías?
**Respuesta**: Flujo completo desde inscripción hasta gestión:

**1. Inscripción** (`/inscripcion`):
- Usuario autenticado accede al formulario
- **Vista**: `templates/inscripcion.html` carga planes activos
- **Controller**: `indexController.java` → `GET /inscripcion`
```java
List<Plan> planes = planService.findActivePlanes();
model.addAttribute("planes", planes);
```
- Usuario selecciona plan + horarios (días y horas)
- **POST**: Crea `Membresia` + múltiples `HorarioEntrenamiento`

**2. Cálculo de fechas** (`MembresiaServiceImpl.java`):
```java
LocalDate fechaInicio = LocalDate.now();  // Hoy
LocalDate fechaFin = fechaInicio.plusMonths(plan.getDuracionMeses());
```
Ejemplo: Plan Estándar (3 meses) contratado 15/12/2025 → fin 15/03/2026

**3. Gestión en /mi-plan**:
- **Ver plan activo**: Muestra nombre plan, fechas, precio, horarios
- **Cambiar plan**: Cancela actual + crea nuevo (fechas desde hoy)
- **Cancelar**: Marca `activa=false` (usuario pierde acceso)
- **Agregar/eliminar horarios**: CRUD individual de horarios

**Ubicaciones**:
- Controller: `controllers/MiPlanController.java`
- Service: `service/impl/MembresiaServiceImpl.java`
- Vistas: `templates/mi_plan.html`, `templates/inscripcion.html`

#### ¿Qué puede hacer un admin con planes?
**Respuesta**: CRUD completo en `/admin/planes`

**Controller**: `AdminPlanesController.java`
**Protección**: `@PreAuthorize("hasRole('ADMIN')")` en todos los métodos

**Operaciones**:

1. **Listar planes** (`GET /admin/planes`):
```java
List<Plan> planes = planService.findAll();  // Activos e inactivos
model.addAttribute("planes", planes);
```
Vista: `templates/admin_planes_list.html`

2. **Crear plan** (`POST /admin/planes/crear`):
```java
Plan plan = new Plan();
plan.setNombre(nombre);
plan.setPrecio(new BigDecimal(precio));
plan.setDuracionMeses(duracionMeses);
planService.save(plan);
```
Vista: `templates/admin_planes_form.html`

3. **Editar plan** (`POST /admin/planes/editar/{id}`):
```java
Plan plan = planService.findById(id).orElseThrow();
plan.setNombre(nuevoNombre);
plan.setPrecio(nuevoPrecio);
planService.save(plan);
```

4. **Eliminar plan** (`POST /admin/planes/eliminar/{id}`):
```java
planService.delete(id);  // Valida que no tenga membresías activas
```

5. **Activar/desactivar** (`POST /admin/planes/toggle-active/{id}`):
```java
planService.toggleActive(id);  // Cambia campo activo
```
Efecto: Plan inactivo no aparece en `/inscripcion` pero sigue en BD

#### ¿Qué puede hacer un admin con alumnos?
**Respuesta**: Gestión completa en `/admin/alumnos`

**Controller**: `AdminAlumnosController.java`

**Operaciones**:

1. **Listar todos los alumnos** (`GET /admin/alumnos`):
```java
List<Usuario> usuarios = userService.findAll();
model.addAttribute("usuarios", usuarios);
```
Vista: Tabla con nombre, email, rol, estado (enabled)

2. **Ver detalle de alumno** (`GET /admin/alumnos/ver/{id}`):
```java
Usuario usuario = userService.findById(id).orElseThrow();
List<Membresia> membresias = membresiaService.findByUsuario(usuario);
model.addAttribute("usuario", usuario);
model.addAttribute("membresias", membresias);  // Historial completo
```
Vista: Perfil completo + tabla de membresías (activas e inactivas) + horarios de cada una

3. **Activar/desactivar alumno** (`POST /admin/alumnos/toggle-enabled/{id}`):
```java
userService.toggleEnabled(id);
```
Efecto: Usuario deshabilitado no puede hacer login

4. **Eliminar alumno** (`POST /admin/alumnos/eliminar/{id}`):
```java
userService.delete(id);  // Cascade elimina membresías y horarios asociados
```

**Ubicaciones**:
- Controller: `controllers/AdminAlumnosController.java`
- Service: `service/impl/UserServiceImpl.java`, `MembresiaServiceImpl.java`
- Vistas: `templates/admin_alumnos_list.html`, `templates/admin_alumnos_detail.html`

---

### Configuración y Deploy:

#### ¿Cómo se configura el login?
**Respuesta**: Configuración completa en `SecurityConfig.java`:

```java
.formLogin(form -> form
    .loginPage("/login")                    // URL del formulario de login
    .loginProcessingUrl("/login")            // URL que procesa credenciales
    .usernameParameter("email")              // Campo del formulario (email en vez de username)
    .passwordParameter("password")           // Campo del formulario
    .defaultSuccessUrl("/inscripcion", true) // Redirección tras login exitoso
    .failureUrl("/login?error=true")         // Redirección si falla
    .permitAll()                             // Todos pueden acceder a /login
)
```

**Vista**: `templates/login.html`
```html
<form th:action="@{/login}" method="post">
    <input type="email" name="email" required>
    <input type="password" name="password" required>
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">
    <button type="submit">Iniciar Sesión</button>
</form>
```

**Flujo**:
1. Usuario ingresa email + password
2. POST a `/login` (Spring Security intercepta)
3. MyUserDetailsService carga usuario por email
4. Compara passwords (BCrypt)
5. Si coincide → sesión creada → redirect `/inscripcion`
6. Si no coincide → redirect `/login?error=true` (muestra mensaje de error)

#### ¿Cómo se despliega en Koyeb?
**Respuesta**: Flujo completo CI/CD:

**Prerequisitos**:
1. Cuenta en Koyeb
2. Repositorio GitHub: `CesarRubilar0/Sring_proy`
3. `Dockerfile` en raíz del proyecto

**Pasos**:

1. **Conectar repositorio** (una sola vez):
   - Koyeb Dashboard → "Create Service"
   - Seleccionar "GitHub"
   - Autorizar acceso → elegir repositorio

2. **Configurar servicio** (una sola vez):
   - Service name: `nakmuay-app`
   - Builder: Docker
   - Port: 8080
   - Environment: `SERVER_PORT=8080` (opcional)

3. **Auto-deploy** (automático tras setup):
   ```bash
   git add .
   git commit -m "Agregar validación de horarios"
   git push origin main
   ```
   → Koyeb detecta push → inicia build Docker → despliega nueva versión

**Build process en Koyeb**:
1. Clone repositorio
2. Ejecuta `docker build -t nakmuay-app .`
3. Ejecuta stage 1: Maven compila proyecto
4. Ejecuta stage 2: Copia JAR a imagen JRE Alpine
5. Push imagen a registry interno
6. Deploy: Ejecuta contenedor con `ENTRYPOINT ["java", "-jar", "app.jar"]`
7. Health check: Verifica que `/` responda
8. Live: Redirige tráfico a nueva versión

**Verificación**:
- URL: `https://nakmuay-app-<tu-usuario>.koyeb.app`
- Logs: Koyeb Dashboard → Service → Logs → buscar "✅ 4 planes creados exitosamente"

**Ubicaciones clave**:
- Build config: `Dockerfile` (raíz)
- Port config: `application.properties` → `server.port=${PORT:8080}`
- Deployment: Koyeb Dashboard

---

### Mejoras y Buenas Prácticas:

#### ¿Qué mejoras harías al proyecto?
**Respuesta**: Mejoras priorizadas por impacto:

**1. Validaciones con Bean Validation** (Jakarta):
```java
@Entity
public class Usuario {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "Nombre debe tener entre 2 y 50 caracteres")
    private String nombre;
    
    @Email(message = "Email inválido")
    @NotBlank
    private String email;
    
    @Pattern(regexp = "^[0-9]{7,8}-[0-9Kk]$", message = "RUT inválido")
    private String rut;
}
```
Ubicación: Anotar entidades en `entity/` package

**2. DTOs (Data Transfer Objects)**:
```java
public class UsuarioDTO {
    private String nombre;
    private String email;
    // Sin password, sin campos internos
}
```
Ventaja: No exponer entidades JPA en controllers, mejor control de qué datos se envían al frontend

**3. Manejo centralizado de excepciones**:
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgument(IllegalArgumentException ex) {
        ModelAndView mav = new ModelAndView("error/error");
        mav.addObject("mensaje", ex.getMessage());
        return mav;
    }
}
```
Ubicación: Crear en `config/` package

**4. Tests unitarios e integración**:
```java
@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private UserService userService;
    
    @Test
    void shouldNotSaveUserWithDuplicateEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            // Test logic
        });
    }
}
```
Ubicación: `src/test/java/.../service/`

**5. Base de datos persistente (Producción)**:
```properties
# application-prod.properties
spring.datasource.url=jdbc:postgresql://db.example.com:5432/nakmuay_db
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate  # No auto-create en prod
```

**6. Paginación en listas**:
```java
@GetMapping("/user/list")
public String listUsers(@RequestParam(defaultValue = "0") int page, Model model) {
    Pageable pageable = PageRequest.of(page, 10);
    Page<Usuario> usuarios = userRepository.findAll(pageable);
    model.addAttribute("usuarios", usuarios);
    return "user_list";
}
```

**7. Roles más granulares**:
- Entidad `Role` con relación ManyToMany a `Usuario`
- Roles: SUPER_ADMIN, ADMIN, INSTRUCTOR, USER
- Permisos: READ_USERS, WRITE_USERS, MANAGE_PLANS

**8. Auditoría con JPA**:
```java
@EntityListeners(AuditingEntityListener.class)
public class Usuario {
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @CreatedBy
    private String createdBy;
}
```

**9. API REST (además de vistas)**:
```java
@RestController
@RequestMapping("/api/planes")
public class PlanRestController {
    @GetMapping
    public List<PlanDTO> getPlanes() { ... }
}
```
Ventaja: Consumir desde frontend SPA (React, Vue) o mobile app

**10. Logging estructurado**:
```java
@Slf4j  // Lombok
public class UserServiceImpl {
    public Usuario save(Usuario usuario) {
        log.info("Creando usuario: {}", usuario.getEmail());
        // ...
        log.debug("Usuario creado exitosamente con ID: {}", usuario.getId());
    }
}
```

**Prioridad**:
1. Validaciones Bean (fácil, alto impacto)
2. Tests (calidad del código)
3. BD persistente (producción real)
4. DTOs (seguridad)
5. Manejo excepciones (UX)
## 8. Comandos Útiles y Testing

### Ejecución Local:

#### Compilar y ejecutar:
```bash
# Compilar proyecto
./mvnw clean package

# Ejecutar aplicación
./mvnw spring-boot:run

# Ejecutar JAR generado
java -jar target/DemoMarcial-0.0.1-SNAPSHOT.jar

# Ejecutar en puerto diferente
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

#### Build sin tests (más rápido):
```bash
./mvnw clean package -DskipTests
```

### Docker Local:

```bash
# Construir imagen
docker build -t nakmuay-app .

# Ejecutar contenedor
docker run -p 8080:8080 nakmuay-app

# Ejecutar en background
docker run -d -p 8080:8080 --name nakmuay-container nakmuay-app

# Ver logs
docker logs -f nakmuay-container

# Detener y eliminar
docker stop nakmuay-container
docker rm nakmuay-container

# Docker Compose
docker-compose up -d      # Inicia servicios
docker-compose logs -f     # Ver logs
docker-compose down        # Detener todo
```

### Git y Deploy:

```bash
# Ver estado
git status

# Agregar cambios
git add .

# Commit
git commit -m "Agregar funcionalidad X"

# Push (trigger auto-deploy en Koyeb)
git push origin main

# Ver historial
git log --oneline -10
```

### Testing:

#### Ejecutar tests:
```bash
# Todos los tests
./mvnw test

# Tests específicos
./mvnw test -Dtest=UserServiceImplTest

# Con cobertura
./mvnw test jacoco:report
```

#### Archivo de test actual:
**Ubicación**: `src/test/java/com/example/proyecto1spring/Proyecto1springApplicationTests.java`

```java
@SpringBootTest
class Proyecto1springApplicationTests {
    @Test
    void contextLoads() {
        // Verifica que el contexto Spring se cargue correctamente
    }
}
```

#### Tests recomendados a implementar:

**1. UserServiceImplTest.java**:
```java
@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private UserService userService;
    
    @Test
    void shouldSaveUserWithEncryptedPassword() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@test.com");
        usuario.setPassword("password123");
        
        Usuario saved = userService.save(usuario);
        
        assertNotNull(saved.getId());
        assertNotEquals("password123", saved.getPassword());  // Debe estar encriptada
    }
    
    @Test
    void shouldThrowExceptionForDuplicateEmail() {
        Usuario usuario1 = new Usuario();
        usuario1.setEmail("duplicate@test.com");
        userService.save(usuario1);
        
        Usuario usuario2 = new Usuario();
        usuario2.setEmail("duplicate@test.com");
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.save(usuario2);
        });
    }
}
```

**2. MembresiaServiceImplTest.java**:
```java
@SpringBootTest
class MembresiaServiceImplTest {
    @Autowired
    private MembresiaService membresiaService;
    
    @Test
    void shouldCalculateFechaFinCorrectly() {
        Usuario usuario = createTestUser();
        Plan plan = createTestPlan(3);  // 3 meses
        
        Membresia membresia = membresiaService.crearMembresia(usuario, plan);
        
        assertEquals(LocalDate.now().plusMonths(3), membresia.getFechaFin());
    }
    
    @Test
    void shouldNotAllowMultipleActiveMembresias() {
        Usuario usuario = createTestUser();
        Plan plan1 = createTestPlan(1);
        Plan plan2 = createTestPlan(3);
        
        membresiaService.crearMembresia(usuario, plan1);
        
        assertThrows(IllegalStateException.class, () -> {
            membresiaService.crearMembresia(usuario, plan2);
        });
    }
}
```

**3. SecurityIntegrationTest.java**:
```java
@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldRedirectToLoginWhenAccessingInscripcionWithoutAuth() throws Exception {
        mockMvc.perform(get("/inscripcion"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrlPattern("**/login"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAccessAdminPlanesWithAdminRole() throws Exception {
        mockMvc.perform(get("/admin/planes"))
               .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyAccessToAdminPlanesWithUserRole() throws Exception {
        mockMvc.perform(get("/admin/planes"))
               .andExpect(status().isForbidden());
    }
}
```

### Acceso a H2 Console (solo desarrollo):

```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:demo
User: sa
Password: (dejar en blanco)
```

**Queries útiles en consola**:
```sql
-- Ver todos los usuarios
SELECT * FROM USUARIOS;

-- Ver planes activos
SELECT * FROM PLANES WHERE ACTIVO = TRUE;

-- Ver membresías activas
SELECT u.NOMBRE, u.APELLIDO, p.NOMBRE AS PLAN, m.FECHA_INICIO, m.FECHA_FIN
FROM MEMBRESIAS m
JOIN USUARIOS u ON m.USUARIO_ID = u.ID
JOIN PLANES p ON m.PLAN_ID = p.ID
WHERE m.ACTIVA = TRUE;

-- Ver horarios de un usuario
SELECT u.EMAIL, m.FECHA_INICIO, h.DIA, h.HORA
FROM HORARIOS_ENTRENAMIENTO h
JOIN MEMBRESIAS m ON h.MEMBRESIA_ID = m.ID
JOIN USUARIOS u ON m.USUARIO_ID = u.ID
WHERE u.EMAIL = 'admin@example.com';
```

---

## 9. Funcionalidades Clave del Proyecto

### Sistema de Membresías:

**Valor agregado**: Permite gestión completa de suscripciones con:
- **Automatización de fechas**: Calcula inicio/fin sin intervención manual
- **Historial completo**: Mantiene registro de todas las membresías del usuario
- **Flexibilidad**: Cambio de plan sin perder historial
- **Control admin**: Visualiza todas las membresías activas por alumno

**Impacto en negocio real**:
- Reducción de errores humanos en cálculo de vencimientos
- Trazabilidad completa para auditorías
- Facilita renovaciones y upgrades
- Base para reportes financieros (ingresos por plan, retención, etc.)

### Sistema de Horarios Personalizados:

**Valor agregado**:
- Usuario elige días y horas específicas (Lunes-Viernes, 9:00-21:00)
- Múltiples horarios por membresía (ej: Lunes 9:00, Miércoles 18:00, Viernes 20:00)
- CRUD individual: agregar/eliminar horarios sin afectar la membresía

**Implementación técnica**:
- Relación `@OneToMany` entre `Membresia` y `HorarioEntrenamiento`
- `orphanRemoval=true`: Eliminar horario automáticamente si se elimina membresía
- Formulario con arrays: `dias[]` y `horarios[]` procesados en loop

### Panel de Administración Completo:

**Funcionalidades**:
1. **Gestión de Planes** (`/admin/planes`):
   - Crear planes con nombre, precio, duración, descripción
   - Activar/desactivar sin eliminar (mantiene historial)
   - Validación: no eliminar planes con membresías activas

2. **Gestión de Alumnos** (`/admin/alumnos`):
   - Vista consolidada: todos los usuarios con estado
   - Detalle individual: perfil + historial completo de membresías
   - Acciones: activar/desactivar (bloquea login), eliminar

3. **Gestión de Usuarios** (`/user/list`):
   - CRUD completo con validaciones
   - Toggle estado (enabled/disabled)
   - Visualización de rol (ADMIN/USER)

---

## 10. Diagrama de Flujos Principales

### Flujo de Autenticación:

```
Usuario → GET /inscripcion (sin sesión)
    ↓
Spring Security intercepta
    ↓
Redirect → GET /login
    ↓
Usuario ingresa email + password
    ↓
POST /login (Spring Security procesa)
    ↓
MyUserDetailsService.loadUserByUsername(email)
    ↓
UserRepository.findByEmail(email)
    ↓
Usuario encontrado → Compara password (BCrypt)
    ↓
✅ Match → Crea sesión HTTP
    ↓
Redirect → /inscripcion (defaultSuccessUrl)
    ↓
Usuario autenticado → Accede al sistema
```

### Flujo de Inscripción (Contratar Plan):

```
Usuario autenticado → GET /inscripcion
    ↓
indexController.inscripcionForm()
    ↓
PlanService.findActivePlanes()
    ↓
PlanRepository.findByActivoTrue()
    ↓
Vista: inscripcion.html (dropdown con 4 planes)
    ↓
Usuario selecciona plan + horarios
    ↓
POST /inscripcion (planId, dias[], horarios[])
    ↓
indexController.procesarInscripcion()
    ↓
MembresiaService.crearMembresia(usuario, plan)
    ↓
Validar: ¿Ya tiene membresía activa? → ❌ Error
    ↓
✅ No tiene → Crear Membresia
    ├─ fechaInicio = hoy
    ├─ fechaFin = hoy + plan.duracionMeses
    └─ activa = true
    ↓
Loop: Para cada par (dia, hora)
    ├─ Crear HorarioEntrenamiento
    ├─ Asociar a Membresia
    └─ HorarioService.save()
    ↓
✅ Inscripción exitosa
    ↓
Redirect → /mi-plan (ver plan activo)
```

### Flujo de Cambio de Plan:

```
Usuario → GET /mi-plan (tiene membresía activa)
    ↓
MiPlanController.miPlan()
    ↓
MembresiaService.findMembresiaActivaByUsuario(usuario)
    ↓
Vista: mi_plan.html (muestra plan actual + botón "Cambiar Plan")
    ↓
Usuario selecciona nuevo plan
    ↓
POST /mi-plan/cambiar (nuevoPlanId)
    ↓
MiPlanController.cambiarPlan()
    ↓
MembresiaService.cambiarPlan(usuario, nuevoPlan)
    ↓
@Transactional inicia
    ├─ 1. Obtener membresía activa actual
    ├─ 2. Marcar actual.activa = false
    ├─ 3. membresiaRepository.save(actual)
    ├─ 4. Crear nueva membresía
    │    ├─ fechaInicio = hoy (no hereda de anterior)
    │    ├─ fechaFin = hoy + nuevoPlan.duracionMeses
    │    └─ activa = true
    └─ 5. membresiaRepository.save(nueva)
    ↓
@Transactional commit (ambas operaciones exitosas)
    ↓
✅ Plan cambiado
    ↓
Redirect → /mi-plan (muestra nuevo plan)
```

### Flujo de Gestión Admin (Ver Detalle Alumno):

```
Admin → GET /admin/alumnos
    ↓
AdminAlumnosController.listarAlumnos()
    ↓
UserService.findAll()
    ↓
Vista: admin_alumnos_list.html (tabla con todos los usuarios)
    ↓
Admin click "Ver Detalle" (alumno ID=5)
    ↓
GET /admin/alumnos/ver/5
    ↓
AdminAlumnosController.verAlumno(5)
    ↓
UserService.findById(5) → Usuario
    ↓
MembresiaService.findByUsuario(usuario)
    ↓
MembresiaRepository.findByUsuario(usuario) → List<Membresia>
    ↓
Para cada Membresia:
    └─ HorarioService.findByMembresia(membresia) → List<Horarios>
    ↓
Vista: admin_alumnos_detail.html
    ├─ Sección 1: Perfil (nombre, email, RUT, rol, estado)
    ├─ Sección 2: Membresía Activa (si tiene)
    │    ├─ Nombre del plan
    │    ├─ Fechas inicio/fin
    │    └─ Horarios configurados
    └─ Sección 3: Historial (membresías inactivas)
         └─ Tabla con todas las membresías anteriores
```

---

Admin gestiona planes → `/admin/planes` → PlanService CRUD → Vista lista planes con acciones activar/desactivar/eliminar
- BCrypt: estándar seguro para contraseñas.
- H2: rapidez en desarrollo; se comunica que no es para producción.
- `@Transactional`: cumple requisito de transaccionalidad y mejora integridad.

## 10. Diagrama simple de flujo (texto)
Usuario → `/login` → Security (auth) → Controlador → Servicio → Repositorio → BD → Vista Thymeleaf.
