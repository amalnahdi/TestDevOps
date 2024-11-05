pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                echo 'Starting to clone the Git repository...'
                // Shallow clone: clone only the latest commit
                git branch: 'Amal',
                    url: 'https://github.com/amalnahdi/TestDevOps.git'
            }
        }

        stage('Clean') {
            steps {
                echo 'Cleaning the workspace...'
                sh 'mvn clean'
            }
        }

        stage('Verify Maven Setup') {
            steps {
                echo 'Verifying Maven installation...'
                // Run the Maven version command
                script {
                    def mvnVersion = sh(script: 'mvn -version', returnStdout: true).trim()
                    echo "Maven version: ${mvnVersion}"
                }
            }
        }

        stage('Test') {
            steps {
                echo 'Running unit tests...'
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging the application...'
                sh 'mvn package -Dmaven.test.skip=true'
            }
        }

        stage('Build') {
            steps {
                echo 'Building the application...'
                sh 'mvn install -Dmaven.test.skip=true'
            }
        }
    }

    post {
        success {
            echo 'Build finished successfully!'
        }
        failure {
            echo 'Build failed!'
        }
        always {
            echo 'Cleaning up workspace...'
            cleanWs() // Clean workspace after build, regardless of success or failure
        }
    }
}
