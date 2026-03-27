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
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploy del WAR su Tomcat tramite Manager API...'
                sh '''
                    curl -s -u jenkins:jenkins123 \
                        "http://host.docker.internal:8088/manager/text/deploy?path=/scuola-di-musica&update=true" \
                        --upload-file target/scuola-di-musica-0.0.1-SNAPSHOT.war
                '''
            }
        }

        stage('Health Check') {
            steps {
                echo 'Verifica che l\'app sia attiva...'
                sh '''
                    for i in 1 2 3 4 5; do
                        STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://host.docker.internal:8088/scuola-di-musica/swagger-ui/index.html)
                        echo "Tentativo $i: HTTP $STATUS"
                        if [ "$STATUS" = "200" ]; then
                            echo "App attiva!"
                            exit 0
                        fi
                        sleep 20
                    done
                    echo "Health check fallito dopo 5 tentativi"
                    exit 1
                '''
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
