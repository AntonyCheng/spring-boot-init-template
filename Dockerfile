FROM openjdk:11
LABEL maintainer="AntonyCheng"

ENV PARAMS=""

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /opt/spring-boot-init-template

COPY target/spring-boot-init-template-*.jar /opt/spring-boot-init-template/app.jar

VOLUME ["/opt/spring-boot-init-template"]

EXPOSE 38080

CMD ["sh","-c","java -jar $JAVA_OPTS /opt/spring-boot-init-template/app.jar $PARAMS"]