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
        stage('Build') {
            steps {
                sh './gradlew jar';
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test';
                junit '**/build/test-results/test/*.xml';
            }
        }
        stage('Create zip') {
            steps {
                sh 'rm -f SimpleCloud-Latest.zip';
                sh 'mkdir -p temp'
                sh 'mkdir temp/modules/'
                sh 'cp start-files/*.* temp/';
                sh 'cp simplecloud-modules/**/build/libs/*.jar temp/modules/';
                sh 'cp simplecloud-runner/build/libs/runner.jar temp/runner.jar';
                sh 'rm temp/modules/SimpleCloud-CloudFlare.jar';
                zip archive: true, dir: 'temp', glob: '', zipFile: 'SimpleCloud-Latest.zip';
                sh 'rm -r temp/';
            }
        }
        stage('Sources') {
            steps {
                sh './gradlew sourceJar';
            }
        }
        stage('Publish') {
           steps {
                sh './gradlew publish';
           }
        }
    }
}