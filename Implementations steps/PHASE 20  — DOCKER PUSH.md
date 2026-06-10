Excellent.
Now your pipeline is entering:

# PHASE 20 — CONTAINER REGISTRY PUBLISHING

This is where enterprise CI/CD becomes deployment-ready.

You now have:

* application artifact
* Docker image
* security scan
* quality gate

Next step:

* push container images to registries

---

# ENTERPRISE USE CASE


In enterprise:

```text id="ph20a"
Developer Code
↓
Jenkins Build
↓
Docker Image
↓
Security Scan
↓
Container Registry
↓
Kubernetes Deployment
```

Kubernetes NEVER builds images directly.

It always pulls from:

* DockerHub
* ECR
* Harbor
* Artifactory
* ACR
* GCR

---

# ENTERPRISE DESIGN

You want BOTH:

| Registry  | Purpose                   |
| --------- | ------------------------- |
| DockerHub | Public/shared/lab         |
| ECR       | Enterprise AWS production |

This is excellent architecture.

---

# PHASE 20 — DOCKER PUSH TO DOCKERHUB OR ECR

---

# STEP 1 — CREATE DOCKERHUB ACCESS TOKEN

Go to:

```text id="ph20b"
DockerHub
→ Account Settings
→ Security
→ Access Tokens
```

Create token:

* Name: `jenkins-token`

Copy token.

---

# STEP 2 — ADD JENKINS CREDENTIAL

Jenkins:

```text id="ph20c"
Manage Jenkins
→ Credentials
→ Global
→ Add Credentials
```

Choose:

```text id="ph20d"
Username with password
```

Add:

* Username = DockerHub username
* Password = DockerHub token

ID:

```text id="ph20e"
dockerhub-creds
```

---

# STEP 3 — ADD DOCKER PUSH STAGE

Add AFTER:

```text id="ph20f"
Trivy Docker Image Scan
```

---

# ENTERPRISE DOCKER PUSH STAGE

```groovy id="0r2g5u"
stage('Docker Push to DockerHub') {

    steps {

        script {

            withCredentials([usernamePassword(
                credentialsId: 'dockerhub-creds',
                usernameVariable: 'DOCKER_USER',
                passwordVariable: 'DOCKER_PASS'
            )]) {

                sh '''
                echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                docker push $DOCKER_USER/devsecops-app:${BUILD_NUMBER}
                '''
            }
        }
    }
}
```

---

# WHAT THIS DOES

## LOGIN

Secure login using Jenkins credentials.

---

## PUSH IMAGE

Pushes:

```text id="ph20g"
parimal1984/devsecops-app:${BUILD_NUMBER}
```

to DockerHub.

---

# VERIFY IMAGE

Check:

```text id="ph20h"
DockerHub
→ Repositories
→ devsecops-app
→ Tags
```

You should see:

* 1
* 2
* 3
* 33
  etc.

---

# PHASE 20 — PUSH TO AWS ECR

Now enterprise-grade production registry.

---

# WHY ECR IN ENTERPRISE

DockerHub:

* public internet
* rate limits
* shared registry

ECR:

* private AWS registry
* IAM integrated
* production secure
* Kubernetes native

Real enterprises prefer:

* ECR
* ACR
* GCR

---

# STEP 1 — CREATE ECR REPOSITORY

AWS Console:

```text id="ph20i"
ECR
→ Create Repository
```

Repository name:

```text id="ph20j"
devsecops-app
```

---

# STEP 2 — INSTALL AWS CLI ON JENKINS SERVER

Run:

```bash id="ph20k"
sudo apt update

sudo apt install awscli -y
```

Verify:

```bash id="ph20l"
aws --version
```

---

# STEP 3 — CONFIGURE AWS CLI

Run:

```bash id="ph20m"
aws configure
```

Provide:

* Access Key
* Secret Key
* Region

Example:

```text id="ph20n"
ap-south-1
```

---

# STEP 4 — GET ECR URL

Example:

```text id="ph20o"
123456789012.dkr.ecr.ap-south-1.amazonaws.com/devsecops-app
```

---

# STEP 5 — ADD ECR PUSH STAGE

Add AFTER DockerHub push.

---

# ENTERPRISE ECR PUSH STAGE

Replace:

* account id
* region

```groovy id="mjqicn"
stage('Docker Push to ECR') {

    steps {

        sh '''
        aws ecr get-login-password --region ap-south-1 | \
        docker login --username AWS \
        --password-stdin 123456789012.dkr.ecr.ap-south-1.amazonaws.com

        docker tag \
        parimal1984/devsecops-app:${BUILD_NUMBER} \
        123456789012.dkr.ecr.ap-south-1.amazonaws.com/devsecops-app:${BUILD_NUMBER}

        docker push \
        123456789012.dkr.ecr.ap-south-1.amazonaws.com/devsecops-app:${BUILD_NUMBER}
        '''
    }
}
```

---

# ENTERPRISE FLOW NOW

```text id="ph20p"
Build
↓
Scan
↓
Push DockerHub
↓
Push ECR
↓
Deploy Kubernetes
```

---

# WHAT ENTERPRISES DO NEXT

After ECR:

# Kubernetes Deployment

Usually:

* EKS
* Helm
* ArgoCD
* GitOps

Because Kubernetes pulls image from ECR.

---

