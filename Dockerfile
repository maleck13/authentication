FROM java:8
WORKDIR /usr/src
COPY ./target/authenticate-1.0.jar /usr/src/
COPY ./authenticate-dev.yml /usr/src/
ENTRYPOINT ["java"]
CMD ["-jar", "authenticate-1.0.jar", "server" , "authenticate-dev.yml"]