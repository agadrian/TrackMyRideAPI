FROM azul/zulu-openjdk:17-latest

# Instala curl
RUN apt-get update && apt-get install -y curl

# Crea el directorio de trabajo
WORKDIR /app

# Descarga directa desde Dropbox (con dl=1)
RUN curl -L -o app.war "https://www.dropbox.com/scl/fi/dmbtuvdfrye1y4fkbbbqy/TrackMyRideAPI_1.0.war?rlkey=7m52qrles0dnr9j4k07ibj3df&st=fgk62jtp&dl=1"

# Ejecuta el .war
CMD ["java", "-jar", "app.war"]
