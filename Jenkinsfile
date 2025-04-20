pipeline {
    agent any
    tools{
        maven 'maven'
    }
    stages{
        stage('Build JAR File'){
            steps{
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/Ovejazo/Tingeso.2.git']])
                dir("payroll-backend"){
                    bat "mvn clean install"
                }
            }
        }

        stage('Tests') {
            steps {
                dir("payroll-backend"){
                // Run Maven 'test' phase. It compiles the test sources and runs the unit tests
                bat 'mvn test' // Use 'bat' for Windows agents or 'sh' for Unix/Linux agents
                }
            }
        }

        stage('Build and Push Docker Image'){
            steps{
                dir("payroll-backend"){
                    script{
                        withDockerRegistry(credentialsId: 'docker-credentials'){
                            bat "docker build -t polloh/payroll-backend:latest ."
                            bat "docker push polloh/payroll-backend:latest"
                        }
                    }
                }
            }
        }
    }
}   
