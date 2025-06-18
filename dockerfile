FROM azul/zulu-openjdk:17-latest

# Instala curl
RUN apt-get update && apt-get install -y curl

# Crea el directorio de trabajo
WORKDIR /app

# Descarga directa desde Dropbox (con dl=1)
RUN curl -L -o app.war "https://www.dropbox.com/scl/fi/693mboxq0jvkchj03cjvc/finalwar.war?rlkey=3i9ixez5re3v3g9krlhcwcii4&st=e8s0evmt&dl=1"

# Ejecuta el .war
CMD ["java", "-jar", "app.war"]
