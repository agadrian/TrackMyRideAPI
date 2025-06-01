FROM azul/zulu-openjdk:17-latest
VOLUME /tmp
COPY build/libs/*.war app.war
CMD ["/bin/sh", "-c", "echo \"$GOOGLE_APPLICATION_CREDENTIALS_JSON\" > /app/credentials.json && java -jar /app.war"]