# --- TAHAP 1: Build aplikasi menggunakan Maven & Java 25 ---
FROM eclipse-temurin:25-jdk-jammy AS build
WORKDIR /app

# Salin file konfigurasi dan direktori proyek
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src ./src

# Berikan izin eksekusi pada mvnw
RUN chmod +x mvnw

# Lakukan build file JAR (lewati unit test agar lebih cepat)
RUN ./mvnw clean package -DskipTests

# --- TAHAP 2: Jalankan aplikasi dengan JRE 25 yang ringkas ---
FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

# Salin hasil file .jar dari tahap build sebelumnya
COPY --from=build /app/target/*.jar app.jar

# Port default aplikasi Spring Boot
EXPOSE 8080

# Perintah untuk menjalankan aplikasi saat container aktif
ENTRYPOINT ["java", "-jar", "app.jar"]