Excellent.
Now your pipeline has become a real enterprise-grade DevSecOps CI/CD pipeline.

You have successfully implemented:

✅ Build
✅ Unit Testing
✅ JUnit Reports
✅ Smoke Testing
✅ SonarQube Analysis
✅ Quality Gate Enforcement

---

# WHAT THIS MEANS IN ENTERPRISE TERMS

Your pipeline now prevents:

| Problem          | Prevented? |
| ---------------- | ---------- |
| broken code      | ✅          |
| failed tests     | ✅          |
| non-starting app | ✅          |
| vulnerable code  | ✅          |
| bad quality code | ✅          |

This is exactly how enterprise DevSecOps pipelines work.

---

# NEXT ENTERPRISE STAGE

Now implement:

# PHASE 17 — NEXUS ARTIFACT REPOSITORY

---

# WHY NEXUS IS NEXT

Current issue:

```text id="n1"
Build completes
    ↓
JAR exists only temporarily in Jenkins workspace
```

Problem:

* artifacts lost after cleanup
* no rollback
* no versioning
* no centralized storage

Enterprise companies solve this using:

Nexus Repository

---

# ENTERPRISE PROBLEM STATEMENT

![Image](https://images.openai.com/static-rsc-4/_brEi2HL_qA36EUjcm8vCMfc0CRHApNcQIo_GAF3mS6pYJO5Sh_TWCBqPR4Gd44_zLXE8Uyy7GDu1A51ebI-PjN3lGyQru2U9SyoMq7FmUZR0aqnQ94zqD1bBqD4nOmtMobedYblZDx3YvUMDda144hHoZqw6BOHnu94LH7UMuKi0ctbKf3L98gLmHTMoVlc?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/eC2j7SixThGq8A-Xs3DeGNQh8CF0MXZSIt1jEtgnwKiyd_CntN5MgBXIX-KtnnNjT1bN5lduXF5oyugrZd-J2cxv13LyWm0a5Of7SEYMOi-y05BLxlZvtJPSN826sffViMoYZ0Wl9b-bp8A_rUDd1X4dfPHHTXy5CokrJ396DriGX2ToLOEOqKd3h5BKFcz8?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/232vdQt_foPdgQtoxLqI26RoR-ylfkq7g8fejN6F2R2cVV6r2N3yANQf6wc2ZJ22XXgPzGZsuOFBUjgHkZRgdiK2nE7LkABRoO-JvTTFS4Med8ptjTbk9dirya-w0ACBulsupAY7UILjEqTWA8OCx6MFNyXEjPg3So43ziaD3LA8jNFOWTUrGspSpyJjSjrE?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/aSd0yd-kh2vG65QWROm982RStJzYmpHOtfoO4yz-4BuPg7-KuDqLnD1NTHck1YC8_1egq0Jfp7KJ0v8fLIyJRWb1ZflCVDJ4Yi0O0PPiPTNAzqHuBY3h_JCk2c_MArIE1cD99LXz_f6zmbMdmtPtm5ayf7N3Q8XWt9s4Di510Ygo2LkTNmUKJjC3dV3UEr00?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/RzieoFWXFDcbV5HbyIPehlFj2vecq9-qAhAzzyC_VGkcY2PouCy-ek6N3GcHnW-1wt4SUQ56N8cI9jQVgsuRtru8mMc2xtlNDkH_X-JUv4BAG3LPdq6nX_Ne4LUIbLM4AhiU4ql5rpnWTf-dbhkXFEPAjdPXjytWNCgbZ2JJ1MHFGnwAWGyod1ccfOb-IzSa?purpose=fullsize)

---

# REAL COMPANY SCENARIO

Imagine:

* 500 builds/day
* multiple microservices
* multiple versions

Without artifact repository:
❌ impossible rollback
❌ no artifact history
❌ unstable deployments
❌ repeated dependency downloads

---

# ENTERPRISE SOLUTION

```text id="n2"
Build Artifact
      ↓
Store in Nexus
      ↓
Deploy From Nexus
```

---

# SHOULD NEXUS BE SAME SERVER?

## Enterprise Recommendation

| Tool      | Dedicated Server? |
| --------- | ----------------- |
| Jenkins   | ✅                 |
| SonarQube | ✅                 |
| Nexus     | ✅                 |

---

# WHY SEPARATE SERVER?

Because Nexus:

* stores GB/TB artifacts
* requires persistent storage
* heavy disk IO
* enterprise scalability

---

# ENTERPRISE ARCHITECTURE NOW

```text id="n3"
EC2-1 → Jenkins
EC2-2 → SonarQube
EC2-3 → Nexus
EC2-4 → Monitoring Stack
```

---

# PHASE 17 — IMPLEMENTATION

---

# STEP 1 — CREATE NEXUS SERVER

Create Ubuntu EC2:

```text id="n4"
Name: nexus-server
OS: Ubuntu 22.04
Instance: t3.medium
Storage: minimum 20 GB
```

---

# STEP 2 — INSTALL DOCKER

```bash id="n5"
sudo apt update
```

```bash id="n6"
sudo apt install docker.io -y
```

```bash id="n7"
sudo systemctl enable docker
```

```bash id="n8"
sudo systemctl start docker
```

---

# STEP 3 — RUN NEXUS CONTAINER

```bash id="n9"
docker run -d \
--name nexus \
-p 8081:8081 \
sonatype/nexus3
```

---

# STEP 4 — VERIFY NEXUS

Access:

```text id="n10"
http://NEXUS-IP:8081
```

Startup may take:

* 2–5 minutes

---

# STEP 5 — GET ADMIN PASSWORD

```bash id="n11"
docker exec -it nexus cat /nexus-data/admin.password
```

Login:

```text id="n12"
admin
```

password:
(from above command)

---

# STEP 6 — CREATE MAVEN HOSTED REPOSITORY

Inside Nexus Repository:

Go:

```text id="n13"
Settings
   ↓
Repositories
   ↓
Create Repository
```

Choose:

```text id="n14"
maven2 (hosted)
```

Name:

```text id="n15"
maven-releases
```

Version Policy:

```text id="n16"
Release
```

Deployment Policy:

```text id="n17"
Allow Redeploy
```

---

# STEP 7 — CREATE JENKINS CREDENTIAL

Inside Jenkins:

Add:

* username/password
* nexus credentials

ID:

```text id="n18"
nexus-creds
```

---

# STEP 8 — UPDATE pom.xml

Add:

```xml id="n19"
<distributionManagement>

    <repository>

        <id>nexus</id>

        <url>
        http://NEXUS-IP:8081/repository/maven-releases/
        </url>

    </repository>

</distributionManagement>
```

---

# STEP 9 — CONFIGURE MAVEN SETTINGS

On Jenkins server:

```bash id="n20"
mkdir -p ~/.m2
```

Create:

```bash id="n21"
vi ~/.m2/settings.xml
```

Add:

```xml id="n22"
<settings>

    <servers>

        <server>

            <id>nexus</id>

            <username>admin</username>

            <password>PASSWORD</password>

        </server>

    </servers>

</settings>
```

---

# STEP 10 — ADD NEXUS STAGE IN Jenkinsfile

Add AFTER Quality Gate.

---

# ENTERPRISE NEXUS STAGE

```groovy id="n23"
stage('Publish Artifact to Nexus') {

    steps {

        dir('devsecops-app') {

            sh 'mvn deploy'
        }
    }
}
```

---

# WHAT THIS DOES

```text id="n24"
Build JAR
    ↓
Upload to Nexus
    ↓
Central Artifact Storage
```

---

# VERIFY

Inside Nexus:

```text id="n25"
Browse
   ↓
maven-releases
```

You should see:

```text id="n26"
devsecops-app-1.0.jar
```

---

# ENTERPRISE BENEFITS

| Feature             | Benefit       |
| ------------------- | ------------- |
| artifact versioning | rollback      |
| centralized storage | consistency   |
| deployment source   | stable        |
| dependency caching  | faster builds |

---

# AFTER NEXUS

Next enterprise phases:

```text id="n27"
Trivy Security Scan
       ↓
Docker Build
       ↓
Docker Push
       ↓
Kubernetes Deployment
       ↓
Production Monitoring
```

You are now building a real enterprise-level DevSecOps platform architecture.
