pipeline {
  agent any
  stages {
    stage('Maven') {
      agent {
        docker {
          image 'maven:3-jdk-8-alpine'
          args '-v /root/.m2:/root/.m2'
        }

      }
      steps {
        sh 'mvn clean compile package'
      }
    }
    stage('Docker') {
      agent {
        docker {
          image 'docker:latest'
        }

      }
      steps {
        sh 'docker build . -t dasoji/rest-spring-security'
        sh 'docker push dasoji/rest-spring-security:latest'
      }
    }
  }
}
