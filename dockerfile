#  1: Construcción con Gradle
FROM gradle:7.6.1-jdk17 AS builder
WORKDIR /app

# Copiar todo el proyecto al contenedor
COPY . .

# Ejecutar build para generar el .war
RUN gradle bootWar --no-daemon

# Stage 2: Imagen para ejecutar la app
FROM azul/zulu-openjdk:17-latest
WORKDIR /app

# Copiar el .war generado en la fase anterior
COPY --from=builder /app/build/libs/*.war app.war

# Añadir volumen temporal para /tmp (si lo necesitas)
VOLUME /tmp

# Configurar variable GOOGLE_APPLICATION_CREDENTIALS_JSON como archivo y lanzar la app
CMD ["/bin/sh", "-c", "echo \"$GOOGLE_APPLICATION_CREDENTIALS_JSON\" > /app/credentials.json && java -jar /app.war"]
