pipeline {
    agent { label 'docker' }

    environment {
        REPO                    = 'KTB-CI-17/cruming-server'
        GIT_BRANCH              = 'product'
        GIT_CREDENTIALS_ID      = 'github_account' // 매니페스트 저장소 접근을 위한 크리덴셜 ID
        AWS_CRED_ID             = 'aws_credentials' // AWS Access Key/Secret Key 저장한 Jenkins Credentials
        AWS_REGION              = 'ap-northeast-2'
        AWS_ACCOUNT_ID          = credentials('aws_account_id')
        ECR_REPO        = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/ktb-cruming-server"
        IMAGE_TAG               = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Git Checkout') {
            steps {
                git branch: "${GIT_BRANCH}",
                    credentialsId: "${GIT_CREDENTIALS_ID}",
                    url: "https://github.com/${REPO}.git"
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh """
                        export DOCKER_BUILDKIT=1
                        docker build --cache-from ${ECR_REPO}:${IMAGE_TAG} -t ${ECR_REPO}:${IMAGE_TAG} .
                        docker tag ${ECR_REPO}:${IMAGE_TAG} ${ECR_REPO}:latest
                    """
                }
            }
        }

        stage('Push to ECR') {
            steps {
                script {
                    docker.withRegistry("https://${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com", "${AWS_CRED_ID}") {
                        docker.image("${ECR_REPO}:${IMAGE_TAG}").push()
                    }
                }
            }
        }



        stage('Debug Environment Variables') {
            steps {
                script {
                    echo "REPO: ${REPO}"
                    echo "GIT_BRANCH: ${GIT_BRANCH}"
                    echo "GIT_CREDENTIALS_ID: ${GIT_CREDENTIALS_ID}"
                    echo "AWS_CRED_ID: ${AWS_CRED_ID}"
                    echo "AWS_REGION: ${AWS_REGION}"
                    echo "AWS_ACCOUNT_ID: ${AWS_ACCOUNT_ID}"
                    echo "ECR_REPO: ${ECR_REPO}"
                    echo "IMAGE_TAG: ${IMAGE_TAG}"
                }
            }
        }

//         stage('Deploy to BackEnd Server') {
//             steps {
//                 script {
//                     withCredentials([
//                         string(credentialsId: 'back_ip', variable: 'PRIVATE_IP'),
//                         string(credentialsId: 'fluentd_address', variable: 'FLUENTD_ADDRESS')
//                     ]) {
//                         echo "PRIVATE_IP: ${PRIVATE_IP}"
//                         echo "FLUENTD_ADDRESS: ${FLUENTD_ADDRESS}"
//                         sshagent(['ec2_ssh']) {
//                             sh """
//                             ssh -v -o StrictHostKeyChecking=no ubuntu@${PRIVATE_IP} \\
//                             "sudo docker pull ${DOCKER_IMAGE_NAME}:${IMAGE_TAG} && \\
//                             sudo docker stop ${CONTAINER_NAME} || true && \\
//                             sudo docker rm ${CONTAINER_NAME} || true && \\
//                             sudo docker run -d \\
//                               --name ${CONTAINER_NAME} \\
//                               --log-driver=fluentd \\
//                               --log-opt fluentd-address=${FLUENTD_ADDRESS} \\
//                               --log-opt tag=${CONTAINER_NAME} \\
//                               -p ${REMOTE_PORT}:${REMOTE_PORT} \\
//                               ${DOCKER_IMAGE_NAME}:${IMAGE_TAG}"
//                             """
//                         }
//                     }
//                 }
//             }
//         }

//         stage('Health Check') {
//             steps {
//                 script {
//                     withCredentials([
//                         string(credentialsId: 'back_ip', variable: 'PRIVATE_IP')
//                     ]) {
//                         sshagent(['ec2_ssh']) {
//                             sh """
//                             ssh -v -o StrictHostKeyChecking=no ubuntu@${PRIVATE_IP} \\
//                             'for i in {1..12}; do \\
//                                 curl -sf http://localhost:${REMOTE_PORT}/health && echo "Health check succeeded" && exit 0 || \\
//                                 (echo "Attempt \$i: Health check failed, retrying in 5 seconds..." && sleep 5); \\
//                             done; \\
//                             echo "Health check failed after all attempts" && exit 1'
//                             """
//                         }
//                     }
//                 }
//             }
//         }
    }

    post {
        always {
            cleanWs()
            script {
                sh """
                    docker ps -a -q --filter ancestor=moby/buildkit:buildx-stable-1 | xargs -r docker stop
                    docker ps -a -q --filter ancestor=moby/buildkit:buildx-stable-1 | xargs -r docker rm
                    docker system prune -a -f --volumes
                """
            }
        }
    }
}
