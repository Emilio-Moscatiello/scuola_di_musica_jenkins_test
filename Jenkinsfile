pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checkout del codice sorgente...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Compilazione del progetto...'
                sh 'mvn clean compile -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo 'Esecuzione dei test...'
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                echo 'Creazione del JAR...'
                sh 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'Pipeline completata con successo!'
        }
        failure {
            echo 'Pipeline fallita. Controlla i log.'
        }
        always {
            cleanWs()
        }
    }
}
