pipeline {
    agent any

    tools {
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

        stage('Deploy') {
            steps {
                echo 'Build immagine Docker e deploy...'
                sh 'docker build -t scuola-di-musica .'
                sh 'docker stop scuola-app || true'
                sh 'docker rm scuola-app || true'
                sh 'docker run -d --name scuola-app -p 8081:8080 scuola-di-musica'
            }
        }

        stage('Health Check') {
            steps {
                echo 'Verifica che l\'app sia attiva...'
                sleep(time: 20, unit: 'SECONDS')
                sh 'curl -f http://host.docker.internal:8081/swagger-ui/index.html || (echo "Health check fallito" && exit 1)'
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
