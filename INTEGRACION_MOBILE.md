# Guía de Integración - NakMuay API en Aplicaciones Móviles

## 📱 Integración de la API REST en tu Aplicación Móvil

Esta guía te muestra cómo conectar tu aplicación móvil (Ionic, React Native, Flutter, etc.) con la API REST de NakMuay.

---

## 🔌 URL Base de la API

```
http://localhost:8080/api
```

**Nota:** Reemplaza `localhost:8080` con la URL real cuando depliegues en producción.

---

## 📥 Consumir Planes

### Obtener todos los planes
```typescript
// Ionic/Angular
import { HttpClient } from '@angular/common/http';

constructor(private http: HttpClient) {}

getPlanes() {
  return this.http.get('http://localhost:8080/api/planes');
}
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "nombre": "Plan Básico",
    "descripcion": "Acceso básico a clases",
    "precio": 59.99,
    "duracionMeses": 1,
    "activo": true
  }
]
```

### Obtener plan específico
```typescript
getPlanById(id: number) {
  return this.http.get(`http://localhost:8080/api/planes/${id}`);
}
```

---

## 👤 Consumir Datos de Usuarios

### Obtener datos del usuario actual
```typescript
getUsuario(id: number) {
  return this.http.get(`http://localhost:8080/api/usuarios/${id}`);
}
```

**Respuesta:**
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

### Obtener usuario por email
```typescript
getUsuarioByEmail(email: string) {
  return this.http.get(`http://localhost:8080/api/usuarios/email/${email}`);
}
```

---

## 💳 Consumir Membresías

### Obtener membresía activa del usuario
```typescript
getMembresiaActiva(usuarioId: number) {
  return this.http.get(`http://localhost:8080/api/membresias/usuario/${usuarioId}`);
}
```

**Respuesta:**
```json
{
  "id": 1,
  "usuarioId": 1,
  "usuarioNombre": "Juan Pérez",
  "usuarioEmail": "juan@example.com",
  "planId": 1,
  "planNombre": "Plan Básico",
  "fechaInicio": "2025-12-16",
  "fechaFin": "2026-01-16",
  "activa": true
}
```

### Crear nueva membresía
```typescript
createMembresia(usuarioId: number, planId: number, 
                fechaInicio: string, fechaFin: string) {
  const payload = {
    usuarioId: usuarioId,
    planId: planId,
    fechaInicio: fechaInicio,
    fechaFin: fechaFin,
    activa: true
  };
  return this.http.post('http://localhost:8080/api/membresias', payload);
}
```

---

## ⏰ Consumir Horarios

### Obtener horarios de la membresía del usuario
```typescript
getHorarios(membresiaId: number) {
  return this.http.get(`http://localhost:8080/api/horarios/membresia/${membresiaId}`);
}
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "membresiaId": 1,
    "diaSemana": "Lunes",
    "horaInicio": "09:00",
    "horaFin": "10:00",
    "activo": true
  },
  {
    "id": 2,
    "membresiaId": 1,
    "diaSemana": "Miércoles",
    "horaInicio": "17:00",
    "horaFin": "18:00",
    "activo": true
  }
]
```

### Crear nuevo horario
```typescript
createHorario(membresiaId: number, diaSemana: string, 
              horaInicio: string, horaFin: string) {
  const payload = {
    membresiaId: membresiaId,
    diaSemana: diaSemana,
    horaInicio: horaInicio,
    horaFin: horaFin,
    activo: true
  };
  return this.http.post('http://localhost:8080/api/horarios', payload);
}
```

### Eliminar horario
```typescript
deleteHorario(id: number) {
  return this.http.delete(`http://localhost:8080/api/horarios/${id}`);
}
```

---

## 📦 Ejemplos Completos

### Servicio Angular/Ionic Completo

```typescript
// api.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // PLANES
  getPlanes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/planes`);
  }

  getPlanById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/planes/${id}`);
  }

  // USUARIOS
  getUsuario(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/usuarios/${id}`);
  }

  getUsuarioByEmail(email: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/usuarios/email/${email}`);
  }

  // MEMBRESÍAS
  getMembresiaActiva(usuarioId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/membresias/usuario/${usuarioId}`);
  }

  createMembresia(membresia: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/membresias`, membresia);
  }

  // HORARIOS
  getHorarios(membresiaId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/horarios/membresia/${membresiaId}`);
  }

  createHorario(horario: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/horarios`, horario);
  }

  deleteHorario(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/horarios/${id}`);
  }
}
```

### Componente Ionic/Angular que usa el servicio

```typescript
// planes.component.ts
import { Component, OnInit } from '@angular/core';
import { ApiService } from '../services/api.service';

@Component({
  selector: 'app-planes',
  templateUrl: './planes.component.html',
  styleUrls: ['./planes.component.scss'],
})
export class PlanesComponent implements OnInit {
  planes: any[] = [];
  loading = true;

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.cargarPlanes();
  }

  cargarPlanes() {
    this.apiService.getPlanes().subscribe({
      next: (data) => {
        this.planes = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error al cargar planes:', error);
        this.loading = false;
      }
    });
  }

  seleccionarPlan(plan: any) {
    console.log('Plan seleccionado:', plan);
    // Aquí puedes navegar a la página de inscripción
  }
}
```

---

## 🔄 Con React Native / Expo

```javascript
// api.js
const API_URL = 'http://localhost:8080/api';

export const apiService = {
  // Planes
  getPlanes: async () => {
    const response = await fetch(`${API_URL}/planes`);
    return response.json();
  },

  getPlanById: async (id) => {
    const response = await fetch(`${API_URL}/planes/${id}`);
    return response.json();
  },

  // Usuarios
  getUsuario: async (id) => {
    const response = await fetch(`${API_URL}/usuarios/${id}`);
    return response.json();
  },

  // Membresías
  getMembresiaActiva: async (usuarioId) => {
    const response = await fetch(`${API_URL}/membresias/usuario/${usuarioId}`);
    return response.json();
  },

  createMembresia: async (membresia) => {
    const response = await fetch(`${API_URL}/membresias`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(membresia)
    });
    return response.json();
  },

  // Horarios
  getHorarios: async (membresiaId) => {
    const response = await fetch(`${API_URL}/horarios/membresia/${membresiaId}`);
    return response.json();
  },

  createHorario: async (horario) => {
    const response = await fetch(`${API_URL}/horarios`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(horario)
    });
    return response.json();
  }
};
```

---

## 📱 Con Flutter

```dart
// api_service.dart
import 'package:http/http.dart' as http;
import 'dart:convert';

class ApiService {
  static const String baseUrl = 'http://localhost:8080/api';

  // PLANES
  static Future<List<dynamic>> getPlanes() async {
    final response = await http.get(Uri.parse('$baseUrl/planes'));
    if (response.statusCode == 200) {
      return json.decode(response.body);
    }
    throw Exception('Error al cargar planes');
  }

  // MEMBRESÍAS
  static Future<dynamic> getMembresiaActiva(int usuarioId) async {
    final response = await http.get(
      Uri.parse('$baseUrl/membresias/usuario/$usuarioId')
    );
    if (response.statusCode == 200) {
      return json.decode(response.body);
    }
    throw Exception('Error al cargar membresía');
  }

  // HORARIOS
  static Future<List<dynamic>> getHorarios(int membresiaId) async {
    final response = await http.get(
      Uri.parse('$baseUrl/horarios/membresia/$membresiaId')
    );
    if (response.statusCode == 200) {
      return json.decode(response.body);
    }
    throw Exception('Error al cargar horarios');
  }

  // Crear horario
  static Future<dynamic> createHorario(Map<String, dynamic> horario) async {
    final response = await http.post(
      Uri.parse('$baseUrl/horarios'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode(horario),
    );
    if (response.statusCode == 201) {
      return json.decode(response.body);
    }
    throw Exception('Error al crear horario');
  }
}
```

---

## ✅ Validación de Datos

Antes de enviar datos a la API, valida:

```typescript
// Validación de plan
const planesValidos = planes.filter(p => 
  p.nombre && p.precio > 0 && p.duracionMeses > 0
);

// Validación de membresía
const membresiaValida = {
  usuarioId: usuarioId,
  planId: planId,
  fechaInicio: new Date().toISOString().split('T')[0],
  fechaFin: new Date(Date.now() + 30*24*60*60*1000).toISOString().split('T')[0],
  activa: true
};

// Validación de horario
const horariosValidos = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes'];
if (!horariosValidos.includes(horario.diaSemana)) {
  throw new Error('Día de semana inválido');
}
```

---

## 🔐 Manejo de Errores

```typescript
this.apiService.getPlanes().subscribe({
  next: (data) => {
    console.log('Datos recibidos:', data);
  },
  error: (error) => {
    console.error('Error:', error);
    if (error.status === 404) {
      console.log('Recurso no encontrado');
    } else if (error.status === 500) {
      console.log('Error del servidor');
    }
  },
  complete: () => {
    console.log('Solicitud completada');
  }
});
```

---

## 🧪 Testing en Postman

1. **Descarga Postman**: https://www.postman.com/downloads/
2. **Crear Collection**: "NakMuay API"
3. **Crear requests** con la URL base: `http://localhost:8080/api`
4. **Configurar variables de entorno**:
   - `base_url`: `http://localhost:8080`
   - `api_path`: `/api`

---

## 📋 Checklist de Integración

- [ ] ¿Está el servidor ejecutándose en `http://localhost:8080`?
- [ ] ¿Has instalado la dependencia HTTP en tu proyecto móvil?
- [ ] ¿Has configurado CORS en la aplicación móvil?
- [ ] ¿Probaste los endpoints con Swagger (`/swagger-ui.html`)?
- [ ] ¿Validaste los datos antes de enviarlos?
- [ ] ¿Implementaste manejo de errores?
- [ ] ¿Probaste con Postman primero?

---

## 🚀 Deploy en Producción

Cuando despliegues a producción:

1. Cambia `localhost:8080` a tu dominio real
2. Configura CORS para los dominios reales
3. Usa HTTPS en lugar de HTTP
4. Agrega autenticación (JWT, OAuth2)
5. Implementa rate limiting
6. Configura logs y monitoreo

---

## 📞 Soporte

Para más información:
- Ver `API_DOCUMENTATION.md` para referencia completa
- Ver `API_TEST_COMMANDS.md` para ejemplos curl
- Acceder a Swagger en: `http://localhost:8080/swagger-ui.html`

---

**¡Ahora estás listo para consumir la API desde tu aplicación móvil!** 🎉
