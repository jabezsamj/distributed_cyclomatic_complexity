FROM mysql

MAINTAINER Jabez Sam, https://github.com/jabezsam


# Environment variables
ENV MYSQL_ROOT_PASSWORD secretadmin


# Create Database
RUN	mkdir /usr/sql
RUN	chmod 644 /usr/sql

#ADD ["cyclomatic.sql", "/usr/sql/sources.sql"]

# Make port 8080 available to the world outside this container
EXPOSE 3306

RUN /etc/init.d/mysql start && \
        mysql -u root -p${MYSQL_ROOT_PASSWORD} -e "CREATE DATABASE cyclomatic" && \
    	rm -rd /usr/sql