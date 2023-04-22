pipeline {
    agent any
    tools {
        maven 'default'
        dockerTool 'default'
    }
    stages {
        stage('Check') {
            steps {
                sh 'java -version'
                sh 'mvn -v'
                sh 'docker version'
            }
        }
        stage('Build Maven') {
            steps {
                checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/eyakauleva/user-microservice']])
                sh 'mvn clean install'
            }
        }
        stage('Build Docker image') {
            steps {
                script {
                    sh "docker build -t eyakauleva/user-service:${params.TAG} ."
                }
            }
        }
        stage('Push Image to DockerHub') {
            steps {
                withCredentials([string(credentialsId: 'DockerHubPwd', variable: 'DockerHubPwd')]) {
                    sh 'docker login -u eyakauleva -p ${DockerHubPwd}'
                    sh "docker push eyakauleva/user-service:${params.TAG}"
                }
            }
        }
        stage('Deploy to k8s') {
            steps {
                dir('src/infra') {
                    sh 'kubectl apply -f app-configmap.yaml'
                    sh 'kubectl apply -f app-service.yaml'
                    sh "sed -i '' 's/\$TAG/${params.TAG}/' app-deployment.yaml"
                    sh 'cat app-deployment.yaml'
                    sh 'kubectl apply -f app-deployment.yaml'
                }
            }
        }
    }
}