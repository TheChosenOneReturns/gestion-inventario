# =================================================================
# ETAPA 1: Construcción (Build Stage) - Usa una imagen con Maven para compilar el proyecto
# =================================================================
FROM maven:3.8.5-openjdk-17 AS builder

# Establece el directorio de trabajo
WORKDIR /build

# Copia solo el pom.xml para descargar las dependencias primero
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia el resto del código fuente y compila el proyecto
COPY src ./src
RUN mvn clean package -DskipTests

# =================================================================
# ETAPA 2: Ejecución (Final Stage) - Usa una imagen ligera de Java para correr la aplicación
# =================================================================
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo
WORKDIR /app

# Copia el .jar compilado desde la etapa 'builder' a la etapa final
COPY --from=builder /build/target/gestion-inventario-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto de la aplicación
EXPOSE 5000

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]