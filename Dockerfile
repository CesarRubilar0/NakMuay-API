# Etapa 1: Build
FROM maven:3.9.5-eclipse-temurin-17 as builder

WORKDIR /app

# Copiar archivos de configuración
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Copiar código fuente
COPY src src

# Compilar aplicación
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar JAR compilado desde etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Puerto por defecto (será configurado por Koyeb)
EXPOSE 8080

# Comando para ejecutar
ENTRYPOINT ["java", "-jar", "app.jar"]
