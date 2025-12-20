# Configuración CORS para NakMuay API

## ✅ Configuración Implementada

La API ahora cuenta con una configuración CORS global y limpia siguiendo las mejores prácticas.

### Cambios Realizados

#### 1. SecurityConfig.java - Configuración CORS Global
- **CSRF deshabilitado** completamente para endpoints de API (necesario para clientes externos)
- **CORS habilitado** mediante `.cors(cors -> {})` en SecurityFilterChain
- **Configuración unificada** en `CorsConfigurationSource`

#### 2. Orígenes Permitidos
```java
"http://localhost:8100"              // Ionic dev server
"http://localhost:4200"              // Angular dev server  
"http://localhost:8080"              // Spring Boot local
"capacitor://localhost"              // App móvil Capacitor (iOS/Android)
"http://localhost"                   // Variante de Capacitor
"https://nakmuay-api-nfg4.onrender.com"  // Producción (si tu frontend está aquí)
```

#### 3. Métodos y Headers Permitidos
- **Métodos**: GET, POST, PUT, PATCH, DELETE, OPTIONS
- **Headers**: Authorization, Content-Type, Accept, X-Requested-With, Origin
- **Credentials**: Habilitado (allowCredentials: true)
- **MaxAge**: 3600 segundos (1 hora de cache para preflight)

#### 4. Limpieza de Anotaciones
Se eliminaron **todas** las anotaciones `@CrossOrigin` de los controladores:
- ❌ HorarioRestController
- ❌ MembresiaRestController
- ❌ PlanRestController
- ❌ UsuarioRestController

**Ahora solo existe UNA estrategia CORS: la configuración global.**

---

## 🧪 Pruebas de CORS

### Prueba 1: OPTIONS Preflight desde localhost:8100
```bash
curl -i -X OPTIONS https://nakmuay-api-nfg4.onrender.com/api/planes \
    -H "Origin: http://localhost:8100" \
    -H "Access-Control-Request-Method: POST" \
    -H "Access-Control-Request-Headers: content-type"
```

**Respuesta esperada:**
```
HTTP/1.1 200 OK
Access-Control-Allow-Origin: http://localhost:8100
Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
Access-Control-Allow-Headers: Authorization, Content-Type, Accept, X-Requested-With, Origin
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 3600
```

### Prueba 2: GET desde Capacitor
```bash
curl -i -X GET https://nakmuay-api-nfg4.onrender.com/api/planes \
    -H "Origin: capacitor://localhost"
```

**Respuesta esperada:**
```
HTTP/1.1 200 OK
Access-Control-Allow-Origin: capacitor://localhost
Access-Control-Allow-Credentials: true
Content-Type: application/json
```

### Prueba 3: POST con datos
```bash
curl -i -X POST https://nakmuay-api-nfg4.onrender.com/api/membresias \
    -H "Origin: http://localhost:8100" \
    -H "Content-Type: application/json" \
    -d '{"usuarioId":1,"planId":2}'
```

---

## 🚀 Despliegue en Render

### Consideraciones Importantes

1. **Cold Start en Plan Free**
   - Render free puede tardar 30-60 segundos en "despertar"
   - La primera petición puede fallar por timeout
   - Considera implementar un endpoint `/health` que la app móvil llame primero

2. **Variables de Entorno**
   - Asegúrate de configurar `ALLOWED_ORIGINS` si quieres hacer la lista dinámica
   - Ejemplo en application.properties:
   ```properties
   cors.allowed-origins=http://localhost:8100,capacitor://localhost
   ```

3. **Reverse Proxy**
   - Render maneja automáticamente las cabeceras CORS
   - No necesitas configuración adicional en Render

---

## 📱 Configuración en Ionic/Angular

### capacitor.config.ts
```typescript
import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.nakmuay.app',
  appName: 'NakMuay',
  webDir: 'www',
  bundledWebRuntime: false,
  server: {
    androidScheme: 'https',
    iosScheme: 'capacitor',
    hostname: 'localhost',
    cleartext: true
  }
};

export default config;
```

### environment.ts (desarrollo)
```typescript
export const environment = {
  production: false,
  apiUrl: 'https://nakmuay-api-nfg4.onrender.com/api'
};
```

### Servicio HTTP en Angular
```typescript
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getPlanes() {
    return this.http.get(`${this.apiUrl}/planes`);
  }

  // Si necesitas enviar credenciales (cookies/auth):
  getPlanesWithCredentials() {
    return this.http.get(`${this.apiUrl}/planes`, { withCredentials: true });
  }
}
```

---

## ⚠️ Troubleshooting

### Error: "No 'Access-Control-Allow-Origin' header"
**Causa**: El origen no está en la lista de permitidos  
**Solución**: Agregar el origen a `configuration.setAllowedOrigins()` en SecurityConfig

### Error: "CORS policy: Credentials flag is 'true'"
**Causa**: Intentas usar `allowCredentials: true` con `allowedOrigins: ["*"]`  
**Solución**: Ya está resuelto - usamos lista específica de orígenes

### Error: 401 Unauthorized en OPTIONS
**Causa**: Spring Security requiere autenticación para preflight  
**Solución**: Ya está resuelto - `.csrf(csrf -> csrf.disable())` y `.requestMatchers("/api/**").permitAll()`

### App móvil no puede conectar
1. Verifica que uses `capacitor://localhost` como origen
2. Asegúrate de que `cleartext: true` está en capacitor.config.ts (solo desarrollo)
3. Revisa logs de red en Chrome DevTools vía `chrome://inspect`

### Render responde lento
1. Implementa un endpoint `/api/health` que responda rápido
2. Llama a `/health` al iniciar la app para "despertar" el servidor
3. Considera plan de pago si necesitas respuesta instantánea

---

## 🔒 Seguridad en Producción

Una vez que tengas tu dominio frontend definitivo:

1. **Actualiza los orígenes permitidos**:
```java
configuration.setAllowedOrigins(Arrays.asList(
    "https://tu-app.web.app",           // Firebase Hosting
    "https://tu-dominio.com",           // Dominio personalizado
    "capacitor://localhost",            // Apps móviles
    "ionic://localhost"                 // Alternativa Ionic
));
```

2. **Elimina orígenes de desarrollo** (localhost:8100, etc.)

3. **Considera HTTPS obligatorio** en producción

4. **Implementa rate limiting** para proteger tu API

---

## ✨ Resumen

✅ Configuración CORS global en SecurityConfig  
✅ CSRF deshabilitado para APIs  
✅ Todas las anotaciones @CrossOrigin eliminadas  
✅ Orígenes específicos (no usar "*")  
✅ Métodos y headers explícitos  
✅ Credentials habilitado  
✅ Preflight cache de 1 hora  

**Tu API está lista para recibir peticiones desde tu SPA Ionic/Angular.**
