# FROM hseeberger/scala-sbt:11.0.11_1.5.5_2.13.6
FROM sbtscala/scala-sbt:eclipse-temurin-18.0.2.1_1_1.8.1_3.2.1

# Set the working directory
WORKDIR /app

# Copy the build.sbt and project folder to the working directory
COPY build.sbt .
COPY project project

# Run sbt to fetch dependencies
# RUN sbt update

# Copy the rest of the source code
COPY . .

# Compile the services
RUN sbt compile

# Expose the ports for the services
EXPOSE 8081 8082

# Run the services
# CMD sbt "runMain auth.AuthMain" & sbt "runMain routing.RoutingMain"
