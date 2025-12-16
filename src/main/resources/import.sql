-- Datos de prueba para la base de datos
-- Este archivo se ejecuta después de la creación del esquema por Hibernate (import.sql).
-- Nota sobre contraseñas: los valores en la columna `password` deben ser hashes bcrypt para que
-- Spring Security los reconozca al autenticar. Aquí dejamos las contraseñas como cadenas
-- vacías (''), porque la clase DataLoader del proyecto ya crea el usuario admin con
-- contraseña codificada. Si quieres que estos usuarios puedan iniciar sesión, reemplaza
-- las cadenas vacías por hashes bcrypt. Más abajo incluyo instrucciones para generar hashes.

-- Roles (si no están presentes, DataLoader también los crea)
INSERT INTO roles (name) VALUES ('ADMIN');
INSERT INTO roles (name) VALUES ('USER');

-- Usuarios de ejemplo (password en blanco: no pueden iniciar sesión hasta que asignes un hash bcrypt)
-- Insertar 10 usuarios normales (ROLE = USER) y 2 administradores (ROLE = ADMIN)
INSERT INTO usuarios (rut, nombre, apellido, password, email, rol, enabled, creat_at, update_at) VALUES
	('11111111-1', 'Juan', 'Pérez', '', 'juan.perez@example.com', 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
	('22222222-2', 'María', 'González', '', 'maria.gonzalez@example.com', 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
	('33333333-3', 'Carlos', 'Ramírez', '', 'carlos.ramirez@example.com', 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
	('44444444-4', 'Lucía', 'Fernández', '', 'lucia.fernandez@example.com', 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
	('55555555-5', 'Andrés', 'Vargas', '', 'andres.vargas@example.com', 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
	('66666666-6', 'Sofía', 'Martínez', '', 'sofia.martinez@example.com', 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
	('77777777-7', 'Diego', 'López', '', 'diego.lopez@example.com', 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
	('88888888-8', 'Valentina', 'Silva', '', 'valentina.silva@example.com', 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
	('99999999-9', 'Mateo', 'Torres', '', 'mateo.torres@example.com', 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
	('10101010-0', 'Elena', 'Rojas', '', 'elena.rojas@example.com', 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
	-- Administradores
	('12121212-1', 'Admin', 'Uno', '', 'admin1@example.com', 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
	('13131313-1', 'Admin', 'Dos', '', 'admin2@example.com', 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Instrucciones para generar un hash bcrypt (desde Java con Spring Security):
-- 1) Puedes crear un pequeño main que use BCryptPasswordEncoder:
--
--    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
--    public class BcryptGen {
--        public static void main(String[] args) {
--            BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
--            System.out.println(enc.encode("tuPasswordAquí"));
--        }
--    }
--
-- 2) Ejecuta ese main con las dependencias del proyecto y copia el hash resultante en la columna
--    `password` de las filas de `usuarios` (reemplaza las comillas vacías '').
-- Alternativamente, puedes usar utilidades externas para generar bcrypt y pegarlas aquí.

-- Planes de membresía
INSERT INTO planes (nombre, descripcion, precio, duracion_meses, activo, created_at, updated_at) VALUES
	('Plan Básico', 'Acceso a clases grupales 3 veces por semana', 25000, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
	('Plan Estándar', 'Acceso ilimitado a clases grupales + 1 clase personalizada al mes', 45000, 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
	('Plan Premium', 'Acceso ilimitado + 4 clases personalizadas al mes + nutrición', 75000, 6, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
	('Plan Anual', 'Acceso total por 12 meses con descuento especial', 500000, 12, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
