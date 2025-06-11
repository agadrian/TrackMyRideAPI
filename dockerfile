FROM azul/zulu-openjdk:17-latest

# Instala curl
RUN apt-get update && apt-get install -y curl

# Crea el directorio de trabajo
WORKDIR /app

# Descarga directa desde Dropbox (con dl=1)
RUN curl -L -o app.war "https://www.dropbox.com/scl/fi/6p8s0mi474mfql3cr5bbd/TrackMyRideAPI_1.0.war?rlkey=zzmdorikzubebu7l5i7x833ky&st=lora3ywl&dl=1"

# Ejecuta el .war
CMD ["java", "-jar", "app.war"]
