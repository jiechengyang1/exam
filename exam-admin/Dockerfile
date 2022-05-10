FROM java:8

RUN mkdir -p /opt/exam/

COPY ./target/exam-admin-0.0.1-SNAPSHOT.jar /opt/exam

COPY run.sh /opt/exam/

EXPOSE 8888

WORKDIR /opt/exam

ENTRYPOINT ["sh", "/opt/exam/run.sh"]
