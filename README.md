# authentication
authentication microservice built using dropwizard. Used for photomap.me

# requires
mysql
redis

#env vars mysql
- MYSQL_URL in format jdbc:mysql://MYSQL_HOST/authentication
- MYSQL_USER
- MYSQL_PASSWORD

#env vars redis
- REDIS_HOST
- REDIS_PORT
- REDIS_PASS
