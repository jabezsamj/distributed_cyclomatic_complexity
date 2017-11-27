FROM java:8

WORKDIR /app

# Copy the current directory contents into the container at /app
ADD . /app

# Make port 8080 available to the world outside this container
EXPOSE 80

# Run app.py when the container launches
CMD ["java", "-jar", "com_distributed_cyclomatic-3.0.0.jar"]


