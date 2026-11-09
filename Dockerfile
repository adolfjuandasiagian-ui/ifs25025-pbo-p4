# --- TAHAP 1: Build aplikasi menggunakan Maven & Java 25 ---
FROM eclipse-temurin:25-jdk-jammy AS build
WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src ./src

RUN chmod +x mvnw

# WAJIB -Pproduction: membangun bundle frontend Vaadin & membuang vaadin-dev.
# Tanpa ini, JAR berjalan di mode development dan gagal ("vaadin-dev-server not found").
RUN ./mvnw clean package -Pproduction -DskipTests

# --- TAHAP 2: Jalankan aplikasi dengan JRE 25 yang ringkas ---
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-Dvaadin.productionMode=true", "-jar", "app.jar"]