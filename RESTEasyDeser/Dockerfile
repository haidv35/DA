FROM tomcat:9
RUN sed -i 's/port="8080"/port="9020"/' ${CATALINA_HOME}/conf/server.xml
COPY . .
COPY ./deserialization-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
ENV JPDA_ADDRESS=5001
CMD ["catalina.sh", "jpda", "run"]
