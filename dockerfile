FROM azul/zulu-openjdk:17-latest

# Instala curl
RUN apt-get update && apt-get install -y curl

# Crea el directorio de trabajo
WORKDIR /app

# Descarga directa desde Dropbox (con dl=1)
RUN curl -L -o app.war "enlacedropbox"

# Ejecuta el .war
CMD ["java", "-jar", "app.war"]
