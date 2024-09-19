FROM openjdk:11.0.16
LABEL maintainer="AntonyCheng"

ENV PARAMS=""

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /opt/power-job-admin

COPY target/power-job-admin-*.jar /opt/power-job-admin/app.jar

VOLUME ["/opt/power-job-adminn"]

EXPOSE 38078

CMD ["sh","-c","java -jar $JAVA_OPTS /opt/power-job-admin/app.jar $PARAMS"]