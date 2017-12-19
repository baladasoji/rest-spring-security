pipeline {
  agent any
  stages {
    stage('Maven') {
      agent {
        docker {
          image 'maven:3-jdk-8-alpine'
          args '-v /var/jenkins_home/.m2:/root/.m2'
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
      withCredentials([usernamePassword(credentialsId: 'dockerdasoji', passwordVariable: 'DKR_PASSWORD', usernameVariable: 'DKR_USERNAME')]) {
      steps {
        sh 'docker build . -t dasoji/rest-spring-security'
         sh 'docker login -u $DKR_USERNAME -p $DKR_PASSWORD'
        sh 'docker push dasoji/rest-spring-security:latest'
      }
      }
    }
  }
}
