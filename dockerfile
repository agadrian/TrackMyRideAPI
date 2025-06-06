FROM azul/zulu-openjdk:17-latest

# Instala curl
RUN apt-get update && apt-get install -y curl

# Crea el directorio de trabajo
WORKDIR /app

# Descarga directa desde Dropbox (con dl=1)
RUN curl -L -o app.war "https://www.dropbox.com/scl/fi/4slyu5egkwcw0wh7ev3op/TrackMyRideAPI.war?rlkey=ahvdzrp7kig1m9rfdars6c047&st=wvosdnrr&dl=1"

# Ejecuta el .war
CMD ["java", "-jar", "app.war"]
