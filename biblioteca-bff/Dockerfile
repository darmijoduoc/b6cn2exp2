FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY wallet /app/wallet
EXPOSE 8080
ENV SERVER_PORT=8080
ENV DB_URL=jdbc:oracle:thin:@gibi3xseta997y7i_tp?TNS_ADMIN=/app/wallet
ENV DB_USER=ADMIN
ENV DB_PASSWORD=aWxpYqvej@bUin3P!tbP
ENV FN_USUARIOS_URL=https://exp2-usuarios-d7gdduepc3etf6ga.brazilsouth-01.azurewebsites.net
ENV FN_RESERVAS_URL=https://exp2-reservas-bhfxdyeqchhkh7bc.brazilsouth-01.azurewebsites.net
ENV FN_LIBROS_URL=https://exp2-libros-babgfdhjg8dec6c4.brazilsouth-01.azurewebsites.net
ENV FN_PRESTAMOS_URL=https://exp2-prestamos-hnenhffdc4ecgnea.brazilsouth-01.azurewebsites.net
ENTRYPOINT ["java", "-jar", "app.jar"]
