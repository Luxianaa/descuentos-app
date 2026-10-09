# Practica 8 - Imagen de la API (la usa Render al desplegar)
# Etapa 1: compilar y empaquetar con Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q -DskipTests package

# Etapa 2: imagen final, solo con el JRE y el jar
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/api.jar app.jar
EXPOSE 8080
# Render define PORT; application.properties lo lee. 75 % de la RAM evita que el plan gratuito (512 MB) mate el proceso.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
