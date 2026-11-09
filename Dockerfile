# --- TAHAP 1: Build aplikasi menggunakan Maven ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Salin file konfigurasi dan direktori proyek
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src ./src

# Lakukan build file JAR (lewati unit test agar lebih cepat)
RUN ./mvnw clean package -DskipTests

# --- TAHAP 2: Jalankan aplikasi dengan JRE yang ringkas ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# Salin hasil file .jar dari tahap build sebelumnya
COPY --from=build /app/target/*.jar app.jar

# Port default aplikasi Spring Boot
EXPOSE 8080

# Perintah untuk menjalankan aplikasi saat container aktif
ENTRYPOINT ["java", "-jar", "app.jar"]