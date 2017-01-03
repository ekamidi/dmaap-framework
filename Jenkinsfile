node {
    // Get the maven tool.
    // ** NOTE: This 'M3' maven tool must be configured
    // **       in the Jenkins global configuration.
    def mvnHome = tool 'M3'
    sh "echo ${mvnHome}"
    
    
    // Mark the code checkout 'stage'....
    stage 'Checkout'
    // Get some code from a GitHub repository
    checkout scm    
   
    // Mark the code build 'stage'....
    stage 'Build dmaap-framework'
    // Run the maven build
    //sh for unix bat for windows
	
    sh "${mvnHome}/bin/mvn -f Msgrtr/pom.xml clean deploy"
    sh "${mvnHome}/bin/mvn -f dmaap/pom.xml clean deploy"
    sh "${mvnHome}/bin/mvn -f dmaap/pom.xml docker:build docker:push"
    sh "${mvnHome}/bin/mvn -f dmaapClient/pom.xml clean deploy"
	
    sh "${mvnHome}/bin/mvn -f CambriaClient/satoolkit/pom.xml clean deploy"
    sh "${mvnHome}/bin/mvn -f CambriaClient/saclientlibrary/pom.xml clean deploy"
    sh "${mvnHome}/bin/mvn -f CambriaClient/cambriaclients/pom.xml clean deploy"  
   
}
