That is actually the better enterprise design.

In real CI/CD pipelines, Docker build and Docker push are usually separated because:

* build validation can happen independently
* image scanning happens before push
* only approved images are pushed
* easier rollback/debugging
* promotion workflows become cleaner

---

# ENTERPRISE FLOW


```text id="db1"
Build Application
      ↓
Build Docker Image
      ↓
Scan Docker Image
      ↓
Push Docker Image
      ↓
Deploy
```

---

# BEST PRACTICE PIPELINE ORDER

Your updated enterprise flow should become:

```text id="db2"
Checkout
↓
Compile
↓
Test
↓
SonarQube
↓
Quality Gate
↓
Trivy Filesystem Scan
↓
Package
↓
Publish Artifact to Nexus
↓
Docker Build
↓
Trivy Docker Image Scan
↓
Docker Push
↓
Smoke Test
```

---

# STEP 1 — CREATE Dockerfile

Inside `devsecops-app`

```dockerfile id="3yrz1g"
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java","-jar","app.jar"]
```

---

# STEP 2 — DOCKER BUILD STAGE

Add AFTER:

```text id="db3"
Publish Artifact to Nexus
```

---

# ENTERPRISE DOCKER BUILD STAGE

Replace:

* `yourdockerhubuser`

with your actual DockerHub username.

```groovy id="w3r10x"
stage('Docker Build') {

    steps {

        dir('devsecops-app') {

            sh '''
            docker build -t yourdockerhubuser/devsecops-app:${BUILD_NUMBER} .
            '''
        }
    }
}
```

---

# WHY ENTERPRISES SEPARATE BUILD

This stage only:

* creates image
* validates Dockerfile
* ensures container can build

No registry interaction yet.

---

# STEP 3 — IMAGE SECURITY SCAN (VERY IMPORTANT)

This is enterprise-grade DevSecOps.

Before pushing image to registry:

* scan image
* block vulnerable containers

---

# ENTERPRISE IMAGE SCAN STAGE

Add AFTER:

```text id="db4"
Docker Build
```

---

```groovy id="5uq7uq"
stage('Trivy Docker Image Scan') {

    steps {

        dir('devsecops-app') {

            sh '''
            mkdir -p trivy-image-reports

            trivy image \
            --severity CRITICAL,HIGH \
            --exit-code 0 \
            --format table \
            yourdockerhubuser/devsecops-app:${BUILD_NUMBER} \
            > trivy-image-reports/docker-image-scan.txt
            '''
        }
    }

    post {

        always {

            archiveArtifacts artifacts: 'devsecops-app/trivy-image-reports/*',
            allowEmptyArchive: true
        }
    }
}
```

---

# WHY THIS IS IMPORTANT

Filesystem scan:

* scans source code/dependencies

Docker image scan:

* scans OS packages
* base image vulnerabilities
* container runtime dependencies

Enterprise environments require BOTH.

---

# STEP 4 — DOCKER PUSH STAGE

Add AFTER:

```text id="db5"
Trivy Docker Image Scan
```

---

# ENTERPRISE DOCKER PUSH STAGE

```groovy id="a6dtxr"
stage('Docker Push') {

    steps {

        script {

            withCredentials([usernamePassword(
                credentialsId: 'dockerhub-creds',
                usernameVariable: 'DOCKER_USER',
                passwordVariable: 'DOCKER_PASS'
            )]) {

                sh '''
                docker login -u $DOCKER_USER -p $DOCKER_PASS

                docker push $DOCKER_USER/devsecops-app:${BUILD_NUMBER}
                '''
            }
        }
    }
}
```

---

# ENTERPRISE BENEFIT

Now pipeline behavior becomes:

| Scenario                    | Result        |
| --------------------------- | ------------- |
| Docker build fails          | stop          |
| image vulnerabilities found | stop          |
| push fails                  | retry         |
| registry down               | isolate issue |

---

# ENTERPRISE MATURITY LEVEL

Your pipeline now includes:

| Capability          | Status |
| ------------------- | ------ |
| CI                  | ✅      |
| Unit Testing        | ✅      |
| Code Quality        | ✅      |
| Security Scan       | ✅      |
| Artifact Management | ✅      |
| Containerization    | ✅      |
| Container Security  | ✅      |
| Registry Push       | ✅      |

This is already close to real enterprise DevSecOps pipelines.

---

# NEXT ENTERPRISE PHASE AFTER THIS

After Docker Push:

## Kubernetes Deployment

Then:

* Helm
* ArgoCD
* GitOps
* Blue/Green
* Canary Deployment
* Autoscaling
* Production Monitoring

---

# INTERVIEW QUESTION

> Why separate Docker build and push stages?

Strong answer:

In enterprise CI/CD pipelines, Docker build and Docker push stages are separated to enable independent validation, security scanning, and approval workflows. The image is first built and scanned for vulnerabilities before being pushed to the container registry. This improves governance, traceability, and supply chain security while preventing vulnerable images from entering deployment environments.
