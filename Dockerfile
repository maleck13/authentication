FROM java:8
WORKDIR /usr/src
COPY ./target/authentication-1.0.jar /usr/src/
COPY ./authentication.yml /usr/src/
EXPOSE 9000
ENTRYPOINT ["java"]
CMD ["-jar", "authentication-1.0.jar", "server" , "authentication.yml"]
