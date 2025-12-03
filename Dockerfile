# --- Estapa 1: Build (Compilação) ---
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY . .
# O comando abaixo cria o .jar pulando os testes para ser mais rápido
RUN mvn clean package -DskipTests

# --- Etapa 2: Run (Execução) ---
FROM eclipse-temurin:25-jdk-jammy
WORKDIR /app
# Pega o jar gerado na etapa anterior
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
