# cache as most as possible in this multistage dockerfile.
FROM maven:3.6-jdk-11 as DEPS

WORKDIR /opt/app
COPY common/pom.xml common/pom.xml
COPY backend/pom.xml backend/pom.xml
COPY backend-read/pom.xml backend-read/pom.xml

COPY pom.xml .

# Download all required dependencies. Exludes the common module since its not available on maven central
# and will be build with the next step
RUN mvn -B -e -C -q -ntp org.apache.maven.plugins:maven-dependency-plugin:3.5.0:go-offline -DexcludeArtifactIds=common

FROM maven:3.6-jdk-11 as BUILDER
WORKDIR /opt/app
COPY --from=deps /root/.m2 /root/.m2
COPY --from=deps /opt/app/ /opt/app
COPY common/src /opt/app/common/src
COPY backend/src /opt/app/backend/src
COPY backend-read/src /opt/app/backend-read/src

RUN mvn -B -e clean install -DskipTests=true

FROM openjdk:11
WORKDIR /opt/app

# Only copy what backend.jar from previous step
COPY --from=builder /opt/app/backend/target/backend.jar .
CMD [ "java", "-jar", "/opt/app/backend.jar" ]
