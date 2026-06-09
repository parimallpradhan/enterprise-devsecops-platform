Absolutely.
Let’s implement this like a real enterprise DevSecOps project from scratch on Ubuntu EC2 step-by-step.

We will build:

```text id="inst1"
Java App
   ↓
Maven
   ↓
Docker
   ↓
Jenkins CI/CD
   ↓
Prometheus Metrics
   ↓
Grafana Dashboards
   ↓
Loki Logs
   ↓
Jaeger Tracing
```

We will start with clean installation.

---

# PHASE 1 — SERVER PREPARATION

# STEP 1 — Launch Ubuntu EC2

Recommended:

* Ubuntu 22.04
* t3.large minimum
* 30GB storage

<img width="1568" height="268" alt="image" src="https://github.com/user-attachments/assets/9e9f727c-9395-4d52-ae72-1dca6e4237c3" />

---

# STEP 2 — Open Security Group Ports

Add inbound rules:

| Port  | Purpose            |
| ----- | ------------------ |
| 22    | SSH                |
| 8080  | Java App / Jenkins |
| 3000  | Grafana            |
| 9090  | Prometheus         |
| 16686 | Jaeger             |
| 3100  | Loki               |
| 8081  | Java app           |


<img width="1830" height="797" alt="image" src="https://github.com/user-attachments/assets/75eadbbd-7c88-41a8-aca0-de28627fa870" />

---

# STEP 3 — Connect to Server

```bash id="inst2"
ssh -i key.pem ubuntu@EC2_PUBLIC_IP
```

---

# PHASE 2 — INSTALL JAVA 21

# STEP 4 — Update Server

```bash id="inst3"
sudo apt update && sudo apt upgrade -y
```
<img width="1908" height="245" alt="image" src="https://github.com/user-attachments/assets/60454457-4713-4e29-bb5d-bec04ced5086" />

---

# STEP 5 — Install Java 21

```bash id="inst4"
sudo apt install openjdk-21-jdk -y
```

Verify:

```bash id="inst5"
java -version
```

Expected:

```text id="inst6"
openjdk version "21"
```

---

# PHASE 3 — INSTALL MAVEN

# STEP 6 — Install Maven

```bash id="inst7"
sudo apt install maven -y
```

Verify:

```bash id="inst8"
mvn -version
```

---

# PHASE 4 — INSTALL DOCKER

# STEP 7 — Install Docker

```bash id="inst9"
sudo apt install docker.io -y
```

Start Docker:

```bash id="inst10"
sudo systemctl start docker
```

Enable Docker:

```bash id="inst11"
sudo systemctl enable docker
```

---

# STEP 8 — Add User to Docker Group

```bash id="inst12"
sudo usermod -aG docker ubuntu
```

Activate group:

```bash id="inst13"
newgrp docker
```

Verify:

```bash id="inst14"
docker ps
```

---

# PHASE 5 — INSTALL DOCKER COMPOSE

# STEP 9 — Install Docker Compose

```bash id="inst15"
sudo curl -L "https://github.com/docker/compose/releases/download/v2.27.0/docker-compose-linux-x86_64" \
-o /usr/local/bin/docker-compose
```

Permission:

```bash id="inst16"
sudo chmod +x /usr/local/bin/docker-compose
```

Verify:

```bash id="inst17"
docker-compose --version
```

---

# PHASE 6 — INSTALL JENKINS

# STEP 10 — Install Jenkins Dependencies

```bash id="inst18"
sudo apt install fontconfig openjdk-21-jre -y
```

---

# STEP 11 — Add Jenkins Repository

```bash id="inst19"
sudo wget -O /etc/apt/keyrings/jenkins-keyring.asc \
  https://pkg.jenkins.io/debian-stable/jenkins.io-2026.key
echo "deb [signed-by=/etc/apt/keyrings/jenkins-keyring.asc]" \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null
```

---

# STEP 12 — Install Jenkins

```bash id="inst21"
sudo apt update
```

```bash id="inst22"
sudo apt install jenkins -y
```

---

# STEP 13 — Start Jenkins

```bash id="inst23"
sudo systemctl start jenkins
```

```bash id="inst24"
sudo systemctl enable jenkins
```

Check:

```bash id="inst25"
sudo systemctl status jenkins
```

---

# STEP 14 — Get Jenkins Password

```bash id="inst26"
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

---

# STEP 15 — Open Jenkins

```text id="inst27"
http://EC2_PUBLIC_IP:8080
```

Install suggested plugins.

---

# PHASE 7 — CREATE JAVA PROJECT

# STEP 16 — Create Project

```bash id="inst28"
mkdir devsecops-app
```

```bash id="inst29"
cd devsecops-app
```

---

# STEP 17 — Create Maven Structure

```bash id="inst30"
mkdir -p src/main/java/com/company/controller
```

```bash id="inst31"
mkdir -p src/main/resources
```

---

# STEP 18 — Create Application.java

```bash id="inst32"
nano src/main/java/com/company/Application.java
```

Paste:

```java id="inst33"
package com.company;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

---

# STEP 19 — Create REST Controller

```bash id="inst34"
nano src/main/java/com/company/controller/AppController.java
```

Paste:

```java id="inst35"
package com.company.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @GetMapping("/")
    public String home() {
        return "DevSecOps App Running";
    }
}
```

---

# STEP 20 — Create pom.xml

```bash id="inst36"
nano pom.xml
```

Paste:

```xml id="inst37"
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.0</version>
    </parent>

    <groupId>com.company</groupId>
    <artifactId>devsecops-app</artifactId>
    <version>1.0</version>

    <properties>
        <java.version>21</java.version>
    </properties>

    <dependencies>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

    </dependencies>

    <build>
        <plugins>

            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>

        </plugins>
    </build>

</project>
```

---

# STEP 21 — Build Project

```bash id="inst38"
mvn clean package
```

Expected:

```text id="inst39"
BUILD SUCCESS
```

---

# STEP 22 — Run Application

```bash id="inst40"
java -jar target/devsecops-app-1.0.jar
```

---

# STEP 23 — Test Application

Open:

```text id="inst41"
http://EC2_PUBLIC_IP:8080
```

Expected:

```text id="inst42"
DevSecOps App Running
```

---

# PHASE 8 — DOCKERIZE APPLICATION

# STEP 24 — Create Dockerfile

```bash id="inst43"
nano Dockerfile
```

Paste:

```dockerfile id="inst44"
FROM eclipse-temurin:21

WORKDIR /app

COPY target/devsecops-app-1.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
```

---

# STEP 25 — Build Docker Image

```bash id="inst45"
docker build -t devsecops-app:v1 .
```

---

# STEP 26 — Run Container

```bash id="inst46"
docker run -d -p 8080:8080 devsecops-app:v1
```

---

# PHASE 9 — OBSERVABILITY STACK

# STEP 27 — Create Observability Directory

```bash id="inst47"
mkdir ~/observability-stack
```

```bash id="inst48"
cd ~/observability-stack
```

---

# STEP 28 — Create docker-compose.yml

```bash id="inst49"
nano docker-compose.yml
```

Add Prometheus + Grafana + Loki + Jaeger stack.

(Use the compose file we created earlier.)

---

# STEP 29 — Start Observability Stack

```bash id="inst50"
docker-compose up -d
```

---

# STEP 30 — Access Tools

| Tool       | URL               |
| ---------- | ----------------- |
| Grafana    | `http://IP:3000`  |
| Prometheus | `http://IP:9090`  |
| Jaeger     | `http://IP:16686` |

---

# NEXT PHASES

After installation we can continue with:

## Phase 10

Jenkins Pipeline

## Phase 11

SonarQube Integration

## Phase 12

Trivy Security Scanning

## Phase 13

Kubernetes Deployment

## Phase 14

ArgoCD GitOps

## Phase 15

Production Monitoring

This becomes a complete enterprise DevSecOps platform.
