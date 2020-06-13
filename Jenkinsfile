pipeline {
    agent any
    options {
        buildDiscarder logRotator(numToKeepStr: '10')
    }
    stages {
        stage('Clean') {
            steps {
                sh 'chmod +x ./gradlew';
                sh './gradlew clean';
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test';
                junit '**/build/test-results/test/*.xml';
            }
        }
        stage('Build') {
            steps {
                sh './gradlew jar';
            }
        }
        stage('Create zip') {
            steps {
                sh 'mkdir -p temp'
                sh 'mkdir temp/modules'
                sh 'cp simplecloud-modules/**/build/libs/*.jar temp/modules/';
                sh 'cp simplecloud-launcher/build/libs/launcher.jar temp/launcher.jar';
                zip archive: true, dir: 'temp', glob: '', zipFile: 'SimpleCloud-Latest.zip';
                sh 'rm -r temp/';
            }
        }
        stage('Sources') {
            steps {
                sh './gradlew sourceJar';
            }
        }
    }
}