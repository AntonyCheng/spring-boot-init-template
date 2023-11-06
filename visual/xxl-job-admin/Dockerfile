FROM openjdk:11
LABEL maintainer="AntonyCheng"

ENV PARAMS=""

ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /opt/xxl-job-admin

COPY target/xxl-job-admin-*.jar /opt/xxl-job-admin/app.jar

VOLUME ["/opt/xxl-job-admin"]

EXPOSE 38079

CMD ["sh","-c","java -jar $JAVA_OPTS /opt/xxl-job-admin/app.jar $PARAMS"]