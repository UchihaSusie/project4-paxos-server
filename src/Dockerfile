FROM bellsoft/liberica-openjdk-alpine-musl:21
WORKDIR /app
COPY . /app
RUN javac server/*.java client/*.java
CMD ["java", "server.ServerApp"]
