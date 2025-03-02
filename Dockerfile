FROM eclipse-temurin:21-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the build files into the container
COPY build/libs/lostark-for-rice.jar /app/lostark-for-rice.jar

# Expose the port your Spring app will run on (default: 8080)
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "lostark-for-rice.jar"]
