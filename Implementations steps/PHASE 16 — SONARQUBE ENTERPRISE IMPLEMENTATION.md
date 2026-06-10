Your next implementation should be:

```text id="arch1"
SonarQube  →  Nexus  →  Docker  →  Kubernetes
```

NOT Nexus first.

---

# WHY SONARQUBE BEFORE NEXUS?

Because enterprise flow is:

```text id="arch2"
Code Quality Check
        ↓
Artifact Storage
```

You should NEVER store:

* vulnerable code
* bad quality code
* failed quality gate artifacts

inside artifact repository.

---

# ENTERPRISE PROBLEM STATEMENT


---

# REAL COMPANY PROBLEM

Imagine:

* 500 developers
* daily commits
* multiple microservices

Without centralized quality control:

* bad code reaches production
* duplicated code increases
* vulnerabilities go unnoticed
* unstable builds stored in Nexus

Enterprise companies solve this using:

| Tool             | Purpose                 |
| ---------------- | ----------------------- |
| SonarQube        | code quality & security |
| Nexus Repository | artifact storage        |
| Jenkins          | CI/CD orchestration     |

---

# ENTERPRISE IMPLEMENTATION STRATEGY

---

# SHOULD YOU INSTALL ON SAME SERVER?

## SHORT ANSWER

| Tool             | Same Server? | Enterprise Recommendation |
| ---------------- | ------------ | ------------------------- |
| Jenkins          | dedicated    | ✅                         |
| SonarQube        | separate     | ✅                         |
| Nexus            | separate     | ✅                         |
| Monitoring Stack | separate     | ✅                         |

---

# WHY SEPARATE SERVERS?

Because:

* SonarQube consumes high RAM
* Nexus stores large artifacts
* Jenkins executes builds
* monitoring uses CPU heavily

If all on same server:
❌ performance issues
❌ memory crashes
❌ disk exhaustion

---

# ENTERPRISE ARCHITECTURE

```text id="arch3"
EC2-1 → Jenkins
EC2-2 → SonarQube
EC2-3 → Nexus
EC2-4 → Monitoring Stack
EC2-5 → Kubernetes Cluster
```

---

# RECOMMENDED AWS INSTANCE TYPES

| Server     | Recommended |
| ---------- | ----------- |
| Jenkins    | t3.medium   |
| SonarQube  | t3.large    |
| Nexus      | t3.medium   |
| Monitoring | t3.large    |

---

# NEXT IMPLEMENTATION PLAN

---

# PHASE 16 — SONARQUBE ENTERPRISE IMPLEMENTATION

---

# USE CASE

Before artifact creation:

* analyze code quality
* detect vulnerabilities
* detect bugs
* detect code smells
* enforce quality gate

---

# ENTERPRISE FLOW

```text id="arch4"
Developer Push
       ↓
Jenkins Build
       ↓
SonarQube Scan
       ↓
Quality Gate Pass?
      / \
    YES  NO
     |    |
 Continue Stop Pipeline
```

---

# STEP 1 — CREATE NEW EC2

Create Ubuntu server:

```text id="arch5"
Name: sonarqube-server
OS: Ubuntu 22.04
RAM: Minimum 4 GB
```

---

# WHY 4 GB MINIMUM?

SonarQube requires:

* Elasticsearch
* Java
* PostgreSQL

Consumes significant memory.

---

# STEP 2 — INSTALL DOCKER

On SonarQube server:

```bash id="arch6"
sudo apt update
```

```bash id="arch7"
sudo apt install docker.io -y
```

```bash id="arch8"
sudo systemctl enable docker
```

```bash id="arch9"
sudo systemctl start docker
```
```bash id="arch9"
sudo usermod -aG docker $USER
```
```
newgrp docker
```

---

# STEP 3 — CREATE SONARQUBE CONTAINER

```bash id="arch10"
docker run -d \
--name sonarqube \
-p 9000:9000 \
sonarqube:lts-community
```

---

# ACCESS

```text id="arch11"
http://SONAR-IP:9000
```

Default:

```text id="arch12"
admin/admin
```

---

# STEP 4 — INSTALL JENKINS PLUGINS

Inside Jenkins:

Install:

* SonarQube Scanner
* Quality Gates

---

# STEP 5 — CONFIGURE SONARQUBE IN JENKINS

Manage Jenkins → System

Add:

* SonarQube URL
* authentication token

---

# STEP 6 — GENERATE TOKEN

Inside SonarQube:

```text id="arch13"
Administration
    ↓
Security
    ↓
Users
    ↓
Generate Token
```

---

# STEP 7 — ADD JENKINS CREDENTIAL

Add:

* Secret Text
* sonar-token

---

# STEP 8 — UPDATE Jenkinsfile

Add AFTER Test stage.

---

# ENTERPRISE SONAR STAGE

```groovy id="arch14"
stage('SonarQube Analysis') {

    steps {

        dir('devsecops-app') {

            withSonarQubeEnv('sonarqube') {

                sh '''
                mvn sonar:sonar \
                -Dsonar.projectKey=devsecops-app \
                -Dsonar.projectName=devsecops-app
                '''
            }
        }
    }
}
```

---

# STEP 9 — ADD QUALITY GATE

VERY IMPORTANT.

Without this:

* bad code still proceeds.

---

# ENTERPRISE QUALITY GATE

```groovy id="arch15"
stage('Quality Gate') {

    steps {

        timeout(time: 5, unit: 'MINUTES') {

            waitForQualityGate abortPipeline: true
        }
    }
}
```

---

# WHAT THIS DOES

If:

* vulnerabilities
* bugs
* code smells
* low coverage

Then:

```text id="arch16"
Pipeline FAILED
```

Deployment stops.

---

# ENTERPRISE BENEFIT

| Problem         | SonarQube Solves |
| --------------- | ---------------- |
| bad code        | ✅                |
| vulnerabilities | ✅                |
| duplication     | ✅                |
| low coverage    | ✅                |
| maintainability | ✅                |

---

# NEXT AFTER SONARQUBE

THEN implement:

---

# ENTERPRISE FINAL ARCHITECTURE

```text id="arch20"
GitHub
   ↓
Jenkins
   ↓
SonarQube
   ↓
Quality Gate
   ↓
Nexus
   ↓
Trivy
   ↓
Docker
   ↓
Kubernetes
   ↓
Prometheus/Grafana
```
