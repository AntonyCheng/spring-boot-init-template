FROM openjdk:11
LABEL maintainer="AntonyCheng"

ENV PARAMS=""

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /opt/xxl-job-admin

COPY target/spring-boot-admin-*.jar /opt/spring-boot-admin/app.jar

VOLUME ["/opt/spring-boot-admin"]

EXPOSE 38078

CMD ["sh","-c","java -jar $JAVA_OPTS /opt/spring-boot-admin/app.jar $PARAMS"]