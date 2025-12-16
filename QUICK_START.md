# 🚀 Quick Start - NakMuay API

## ⚡ Inicio Rápido en 5 Minutos

### Paso 1: Ejecutar el Servidor
```bash
# En la raíz del proyecto
./mvnw spring-boot:run
```

El servidor estará disponible en: `http://localhost:8080`

---

### Paso 2: Acceder a Swagger (Documentación Interactiva)
Abre en tu navegador:
```
http://localhost:8080/swagger-ui.html
```

Aquí puedes:
- ✅ Ver todos los endpoints documentados
- ✅ Hacer requests de prueba
- ✅ Ver ejemplos de requests/responses
- ✅ Entender los modelos de datos

---

### Paso 3: Probar Endpoints Básicos

#### Obtener todos los planes
```bash
curl http://localhost:8080/api/planes
```

#### Obtener un usuario
```bash
curl http://localhost:8080/api/usuarios/1
```

#### Obtener membresía activa
```bash
curl http://localhost:8080/api/membresias/usuario/1
```

---

## 📚 Documentación

| Archivo | Propósito |
|---------|-----------|
| **`API_DOCUMENTATION.md`** | Referencia completa de todos los endpoints |
| **`INTEGRACION_MOBILE.md`** | Guía para integrar desde app móvil |
| **`API_TEST_COMMANDS.md`** | Comandos curl para pruebas |
| **`RESUMEN_DE_CAMBIOS.md`** | Cambios técnicos implementados |

---

## 🔌 Endpoints Principales

### Planes
```
GET    /api/planes              # Todos los planes
GET    /api/planes/{id}         # Un plan específico
```

### Usuarios
```
GET    /api/usuarios            # Todos los usuarios
GET    /api/usuarios/{id}       # Un usuario
GET    /api/usuarios/email/{email}  # Usuario por email
```

### Membresías
```
GET    /api/membresias/usuario/{id}  # Membresía activa del usuario
POST   /api/membresias          # Crear membresía
```

### Horarios
```
GET    /api/horarios/membresia/{id}  # Horarios de una membresía
POST   /api/horarios            # Crear horario
DELETE /api/horarios/{id}       # Eliminar horario
```

---

## 🧪 Testing Rápido

### Opción 1: Con cURL
```bash
# Obtener planes
curl -X GET http://localhost:8080/api/planes

# Crear membresía
curl -X POST http://localhost:8080/api/membresias \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "planId": 1,
    "fechaInicio": "2025-12-16",
    "fechaFin": "2026-01-16",
    "activa": true
  }'
```

### Opción 2: Con Postman
1. Descargar Postman
2. Crear nuevo request
3. URL: `http://localhost:8080/api/planes`
4. Click "Send"

### Opción 3: Con Swagger
1. Ir a `http://localhost:8080/swagger-ui.html`
2. Seleccionar endpoint
3. Click "Try it out"
4. Click "Execute"

---

## 📱 Para Integrar en App Móvil

### Ionic/Angular
```typescript
this.http.get('http://localhost:8080/api/planes').subscribe(data => {
  console.log(data);
});
```

### React Native
```javascript
fetch('http://localhost:8080/api/planes')
  .then(res => res.json())
  .then(data => console.log(data));
```

### Flutter
```dart
var response = await http.get(Uri.parse('http://localhost:8080/api/planes'));
var planes = json.decode(response.body);
```

---

## ✅ Checklist

- [ ] Servidor ejecutándose en puerto 8080
- [ ] Acceso a Swagger en `/swagger-ui.html`
- [ ] Endpoints retornando JSON
- [ ] CORS configurado para tu app móvil
- [ ] DTOs no exponen passwords

---

## 🐛 Troubleshooting

**P: Puerto 8080 ya en uso**
```bash
# Cambia el puerto en application.properties
server.port=8081
```

**P: Error de CORS**
- Verifica que tu app móvil esté en un puerto permitido
- Puertos permitidos: 8100, 4200, 8080

**P: Swagger no aparece**
- Limpia el caché del navegador
- Verifica que la dependencia `springdoc-openapi-starter-webmvc-ui` esté en pom.xml

---

## 📂 Estructura de Carpetas

```
src/main/java/com/example/proyecto1spring/
├── controllers/api/              # ← REST Controllers
├── dto/                         # ← DTOs para API
├── entity/                      # ← Modelos JPA
├── service/                     # ← Lógica de negocios
├── repository/                  # ← Acceso a datos
└── config/                      # ← Configuración
```

---

## 🎯 Próximos Pasos

1. **Leer `API_DOCUMENTATION.md`** para entender todos los endpoints
2. **Revisar `INTEGRACION_MOBILE.md`** para tu app específica
3. **Usar Swagger** para explorar la API interactivamente
4. **Probar con Postman** antes de integrar en tu app

---

## 📞 URLs Importantes

| URL | Propósito |
|-----|-----------|
| `http://localhost:8080` | Servidor principal |
| `http://localhost:8080/swagger-ui.html` | Documentación API |
| `http://localhost:8080/api/planes` | Endpoint de prueba |
| `http://localhost:8080/h2-console` | Base de datos (opcional) |

---

## 🔐 Seguridad

✅ CORS configurado para desarrollo local
✅ Endpoints `/api/**` accesibles públicamente
✅ DTOs no exponen información sensible (passwords)
✅ Validación de datos en controllers

---

## 💡 Tips

1. Usa **Swagger** para entender qué espera cada endpoint
2. Comienza por `GET /api/planes` para verificar que todo funciona
3. Guarda ejemplos de requests/responses para tu documentación
4. Prueba primero con **cURL** o **Postman** antes de integrar en código

---

**¡Listo! Ahora puedes empezar a consumir la API desde tu aplicación móvil.** 🎉

Para más detalles, consulta la documentación completa en `API_DOCUMENTATION.md`
