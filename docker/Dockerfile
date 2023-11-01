FROM adoptopenjdk/openjdk16:ubi
LABEL authors="VladislavSavko"
WORKDIR /application
COPY ./target /target
WORKDIR /target
EXPOSE 8080
ENTRYPOINT ["java","-jar","application-jar-with-dependencies.jar"]