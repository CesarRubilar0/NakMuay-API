# Script de Prueba de API - NakMuay

## Requisitos
- Proyecto ejecutándose en `http://localhost:8080`
- curl o Postman

## 1. Obtener todos los planes
```bash
curl -X GET http://localhost:8080/api/planes
```

## 2. Obtener un plan específico (ID 1)
```bash
curl -X GET http://localhost:8080/api/planes/1
```

## 3. Crear un nuevo plan
```bash
curl -X POST http://localhost:8080/api/planes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Plan Intermedio",
    "descripcion": "Plan para usuarios intermedios",
    "precio": 99.99,
    "duracionMeses": 3,
    "activo": true
  }'
```

## 4. Obtener todos los usuarios
```bash
curl -X GET http://localhost:8080/api/usuarios
```

## 5. Obtener un usuario por ID (ID 1)
```bash
curl -X GET http://localhost:8080/api/usuarios/1
```

## 6. Obtener usuario por email
```bash
curl -X GET http://localhost:8080/api/usuarios/email/usuario@example.com
```

## 7. Obtener todas las membresías
```bash
curl -X GET http://localhost:8080/api/membresias
```

## 8. Obtener membresía activa de un usuario (usuarioId 1)
```bash
curl -X GET http://localhost:8080/api/membresias/usuario/1
```

## 9. Crear nueva membresía
```bash
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

## 10. Obtener todos los horarios
```bash
curl -X GET http://localhost:8080/api/horarios
```

## 11. Obtener horarios de una membresía (membresiaId 1)
```bash
curl -X GET http://localhost:8080/api/horarios/membresia/1
```

## 12. Crear nuevo horario
```bash
curl -X POST http://localhost:8080/api/horarios \
  -H "Content-Type: application/json" \
  -d '{
    "membresiaId": 1,
    "diaSemana": "Lunes",
    "horaInicio": "09:00",
    "horaFin": "10:00",
    "activo": true
  }'
```

## 13. Actualizar un horario (horarioId 1)
```bash
curl -X PUT http://localhost:8080/api/horarios/1 \
  -H "Content-Type: application/json" \
  -d '{
    "membresiaId": 1,
    "diaSemana": "Martes",
    "horaInicio": "17:00",
    "horaFin": "18:00",
    "activo": true
  }'
```

## 14. Eliminar un horario (horarioId 1)
```bash
curl -X DELETE http://localhost:8080/api/horarios/1
```

## 15. Documentación Swagger
Abre en tu navegador:
```
http://localhost:8080/swagger-ui.html
```

## Notas
- Reemplaza los IDs en los ejemplos con los valores reales de tu base de datos
- Asegúrate de que el servidor esté ejecutándose antes de hacer las pruebas
- La API retorna JSON con códigos de estado HTTP estándar
- CORS está configurado para localhost:8100 y localhost:4200

## Con Postman
1. Abre Postman
2. Crea una nueva colección llamada "NakMuay-API"
3. Importa los ejemplos anteriores en lugar de copiar/pegar
4. Configura la variable de entorno `base_url` como `http://localhost:8080`
5. Usa `{{base_url}}/api/planes` en lugar de la URL completa

## Con Thunder Client (VS Code)
1. Instala la extensión Thunder Client
2. Crea una nueva solicitud
3. Copia y pega los comandos curl anteriores
4. ¡Listo para hacer pruebas!
