pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building..'
                sh 'mill -i __.compile'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
                sh 'mill -i __.test'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}
