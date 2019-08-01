pipeline {
 
  agent {
        docker {
            image 'maven:3-alpine'
            args '-v $HOME/.m2:/root/.m2'
        }
    }

  stages {

    stage('Test') {

      steps{
       
        sh 'mvn --version'

       // sh 'mvn clean com.smartbear.soapui:soapui-maven-plugin:test'
       sh 'mvn test -P runAllocator site'
        
      }

    }



}
}
