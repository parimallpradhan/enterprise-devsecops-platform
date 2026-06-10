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


---
This is actually GOOD news.
Your enterprise Quality Gate is working correctly.

The pipeline was intentionally stopped because SonarQube did not return a successful quality status within Jenkins timeout.

---

# WHAT HAPPENED

Your pipeline reached:

```groovy id="q1"
waitForQualityGate abortPipeline: true
```

Jenkins waited for SonarQube result.

But:

* SonarQube analysis incomplete
  OR
* webhook not configured
  OR
* Quality Gate failed
  OR
* Jenkins timeout too short

Finally:

```text id="q2"
Timeout has been exceeded
Finished: ABORTED
```

---

# MOST COMMON ROOT CAUSE

## 90% CASES → SONARQUBE WEBHOOK NOT CONFIGURED

This is the most common enterprise issue.

Without webhook:

* Jenkins waits forever
* SonarQube never sends status back

---

# HOW QUALITY GATE WORKS

![Image](https://images.openai.com/static-rsc-4/jggbZe55B0GOMmEYU0I2rlICmPeCdltTEESMVLvC-1P_XohUqnNQm7u11UACsZaXhFhP0c0C3dg2uDPxr85eu2bgvPeOSdSMOZfAnW1eI0bC_gphvjOl42f01FEqa4Z_S_BJJ0HV0je6Yl5e0YlHkpwpQLlhxiEltGji0pUfAXnflRY0SIJkZHRoHN3s7mnN?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/hZGvtUebDYa-Hin1kqFp5yY8UhyykSDXMmyc17nd5lsuz8FeQNj1JCB87Zb2-3BQyiyYb2ygqWJ4S5RWem9aOdFQCgNCckea1VdSYyxvpV4xasosqrJjq0HH65NfKQsfzi4pkUS-yO5OzW0Au8gb508YMx6YFWlK6ZYZL7HO0-dUHZNwTTQXms_r799dDy-z?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/ws3WAvmJEkYqfAA8AMI9vmp-LM8ypwBgwSyVDOUw38-FoY91qKd41A57z8JcGT1GC081c01SS0RhIG-BVzFWfLM1Nm9GjYgcrTRweR6zw3BUJtY2MWDdm1GtZN5yZzAVm__dOxnJTKxFEq28HggyompOM9G8wjXaYdp9jL3WUDnrxZUjHe9ZyGcaqfcasI-h?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/PNxDh8VF7dk9C0Zd-MrK7XkRE0wzkB6w0rcL7TDIFEuVQYqwBI_fc3i1ntywHndUbVYPhgFCj5VQKNUElvtAsSYnRAoIzIEeSdhjTlBykZxuCfHq7BJcDUXnbieo7pVqWr24mlbxNr_P3WZoWGmgHSiD2dgmC6nQ5vqT2fdWtIA7W67HGi-_-kKayuGGGiUy?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/YxaiMtkRk8SYMY1x77lSXB__rfvHFIbrAaWD2RurCX_RWza5N4SCUQut3xDNV0dXc0sNYiv6HrNy6ueDvqZA3fwWNGXi__QbpheBhvhZ-rdYTGEBHexTSmwD8pdHKaRXrs82adPDlUYzFoOB-6Dv27-c1hyAAk9RLrWQJIJHqL4h7aS1vGFdBB52S9XAY0GF?purpose=fullsize)

```text id="q3"
Jenkins
   ↓
Start Sonar Scan
   ↓
SonarQube Processes Scan
   ↓
SonarQube Sends Result via Webhook
   ↓
Jenkins Receives PASS/FAIL
```

Your problem:
❌ webhook communication missing.

---

# STEP-BY-STEP FIX

---

# STEP 1 — VERIFY SONARQUBE ANALYSIS

Open:

```text id="q4"
http://SONAR-IP:9000
```

Go:

* Projects
* devsecops-app

Check:

* analysis completed?
* Quality Gate status?

---

# STEP 2 — CONFIGURE WEBHOOK (VERY IMPORTANT)

Inside SonarQube:

Go:

```text id="q5"
Administration
   ↓
Configuration
   ↓
Webhooks
```

Add:

```text id="q6"
Name: Jenkins
```

URL:

```text id="q7"
http://JENKINS-IP:8080/sonarqube-webhook/
```

VERY IMPORTANT:

* trailing slash `/` mandatory

---

# STEP 3 — VERIFY JENKINS REACHABILITY

From SonarQube server:

```bash id="q8"
curl http://JENKINS-IP:8080
```

Should return:

* Jenkins HTML

If not:

* security group issue
* firewall issue

---

# STEP 4 — OPEN SECURITY GROUPS

Allow:

| Source           | Destination    | Port |
| ---------------- | -------------- | ---- |
| SonarQube Server | Jenkins Server | 8080 |

---

# STEP 5 — INCREASE TIMEOUT

Sometimes Sonar analysis takes longer.

Replace:

```groovy id="q9"
timeout(time: 5, unit: 'MINUTES')
```

with:

```groovy id="q10"
timeout(time: 15, unit: 'MINUTES')
```

---

# STEP 6 — VERIFY SONARQUBE STATUS IN JENKINS

Go:

* Jenkins
* Manage Jenkins
* System

Verify:

* SonarQube server configured
* token valid

---

# STEP 7 — VERIFY PIPELINE ORDER

Correct order:

```text id="q11"
Compile
   ↓
Test
   ↓
SonarQube Analysis
   ↓
Quality Gate
   ↓
Package
```

NOT:

```text id="q12"
Package
   ↓
Quality Gate
```

because quality gate validates code BEFORE packaging.

---

# CORRECT ENTERPRISE PIPELINE FLOW

```groovy id="q13"
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

stage('Quality Gate') {

    steps {

        timeout(time: 15, unit: 'MINUTES') {

            waitForQualityGate abortPipeline: true
        }
    }
}
```

---

# HOW TO VERIFY WEBHOOK WORKING

Inside SonarQube:

* Administration
* Webhooks
* Recent Deliveries

Expected:

```text id="q14"
HTTP 200
```

If:

* 404
* timeout
* connection refused

then Jenkins unreachable.

---

# IF QUALITY GATE ACTUALLY FAILED

Then SonarQube detected:

* bugs
* vulnerabilities
* code smells
* low coverage

Check dashboard:

```text id="q15"
Project → Issues
```

---

# ENTERPRISE BENEFIT

This is exactly how enterprise DevSecOps prevents bad code from production.

```text id="q16"
Bad Code
   ↓
Quality Gate Fails
   ↓
Pipeline Stops
```

---

This is actually GOOD news.
Your enterprise Quality Gate is working correctly.

The pipeline was intentionally stopped because SonarQube did not return a successful quality status within Jenkins timeout.

---

# WHAT HAPPENED

Your pipeline reached:

```groovy id="q1"
waitForQualityGate abortPipeline: true
```

Jenkins waited for SonarQube result.

But:

* SonarQube analysis incomplete
  OR
* webhook not configured
  OR
* Quality Gate failed
  OR
* Jenkins timeout too short

Finally:

```text id="q2"
Timeout has been exceeded
Finished: ABORTED
```

---

## 90% CASES → SONARQUBE WEBHOOK NOT CONFIGURED

This is the most common enterprise issue.

Without webhook:

* Jenkins waits forever
* SonarQube never sends status back

---

# HOW QUALITY GATE WORKS


```text id="q3"
Jenkins
   ↓
Start Sonar Scan
   ↓
SonarQube Processes Scan
   ↓
SonarQube Sends Result via Webhook
   ↓
Jenkins Receives PASS/FAIL
```

Your problem:
❌ webhook communication missing.

---

# STEP-BY-STEP FIX

---

# STEP 1 — VERIFY SONARQUBE ANALYSIS

Open:

```text id="q4"
http://SONAR-IP:9000
```

Go:

* Projects
* devsecops-app

Check:

* analysis completed?
* Quality Gate status?

---

# STEP 2 — CONFIGURE WEBHOOK (VERY IMPORTANT)

Inside SonarQube:

Go:

```text id="q5"
Administration
   ↓
Configuration
   ↓
Webhooks
```

Add:

```text id="q6"
Name: Jenkins
```

URL:

```text id="q7"
http://JENKINS-IP:8080/sonarqube-webhook/
```

VERY IMPORTANT:

* trailing slash `/` mandatory

---

# STEP 3 — VERIFY JENKINS REACHABILITY

From SonarQube server:

```bash id="q8"
curl http://JENKINS-IP:8080
```

Should return:

* Jenkins HTML

If not:

* security group issue
* firewall issue

---

# STEP 4 — OPEN SECURITY GROUPS

Allow:

| Source           | Destination    | Port |
| ---------------- | -------------- | ---- |
| SonarQube Server | Jenkins Server | 8080 |

---

# STEP 5 — INCREASE TIMEOUT

Sometimes Sonar analysis takes longer.

Replace:

```groovy id="q9"
timeout(time: 5, unit: 'MINUTES')
```

with:

```groovy id="q10"
timeout(time: 15, unit: 'MINUTES')
```

---

# STEP 6 — VERIFY SONARQUBE STATUS IN JENKINS

Go:

* Jenkins
* Manage Jenkins
* System

Verify:

* SonarQube server configured
* token valid

---

# STEP 7 — VERIFY PIPELINE ORDER

Correct order:

```text id="q11"
Compile
   ↓
Test
   ↓
SonarQube Analysis
   ↓
Quality Gate
   ↓
Package
```

NOT:

```text id="q12"
Package
   ↓
Quality Gate
```

because quality gate validates code BEFORE packaging.

---

# CORRECT ENTERPRISE PIPELINE FLOW

```groovy id="q13"
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

stage('Quality Gate') {

    steps {

        timeout(time: 15, unit: 'MINUTES') {

            waitForQualityGate abortPipeline: true
        }
    }
}
```

---

# HOW TO VERIFY WEBHOOK WORKING

Inside SonarQube:

* Administration
* Webhooks
* Recent Deliveries

Expected:

```text id="q14"
HTTP 200
```

If:

* 404
* timeout
* connection refused

then Jenkins unreachable.

---

# IF QUALITY GATE ACTUALLY FAILED

Then SonarQube detected:

* bugs
* vulnerabilities
* code smells
* low coverage

Check dashboard:

```text id="q15"
Project → Issues
```

---

# ENTERPRISE BENEFIT

This is exactly how enterprise DevSecOps prevents bad code from production.

```text id="q16"
Bad Code
   ↓
Quality Gate Fails
   ↓
Pipeline Stops
```

---


---

# MOST LIKELY FIX FOR YOU

✅ Configure SonarQube webhook
✅ Verify Jenkins reachable from SonarQube
✅ Increase timeout to 15 mins

Then rerun pipeline.

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
