pipeline {
     agent {
        docker {
            image 'maven:3-alpine'
            args '-v $HOME/.m2:/root/.m2'
        }
    }
    stages {

	
        stage("build") {
            steps {
                sh 'mvn clean install -Dmaven.test.failure.ignore=true'
		            sh 'mvn test -P runAllocator site'
		           //sh 'MAVEN_OPTS="-Xmx12g"'
            }
        }
		
		
				
    }

    post {
        always {
            archive "target/**/*"
            junit 'target/surefire-reports/*.xml'
			
			
        }
    }
	
	
}
