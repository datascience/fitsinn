FROM tomcat:8.5.93-jre11

ENV CATALINA_BASE "/usr/local/tomcat"
ENV FITS_PORT 8080


RUN apt update && \
    apt install -y file unzip && \
    apt clean && \
    rm -rf /var/lib/apt/lists/

RUN wget https://github.com/harvard-lts/fits/releases/download/1.6.0/fits-1.6.0.zip && \
    unzip fits-1.6.0.zip -d /fits/ && \
    rm fits-1.6.0.zip

RUN wget https://github.com/harvard-lts/FITSservlet/releases/download/2.1.0/fits-service-2.1.0.war -O $CATALINA_BASE/webapps/fits.war


RUN echo 'fits.home=/fits/' >> ${CATALINA_BASE}/conf/catalina.properties
RUN echo "shared.loader=/fits/lib/*.jar" >> $CATALINA_BASE/conf/catalina.properties
RUN echo "http.port=${FITS_PORT}" >> $CATALINA_BASE/conf/catalina.properties

RUN chown -R 1001 ${CATALINA_BASE} \
    && chmod -R "g+rwX" ${CATALINA_BASE}

EXPOSE 8080
USER 1001
ENTRYPOINT ["catalina.sh"]
CMD ["run"]

