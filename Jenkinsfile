def notificationBranch = 'develop'

pipeline {
	agent any 

	stages {
		stage('Preparation') {
      			steps {
				// Checkout code from repo
		  		checkout scm
			}
		}

		stage('Build') {
        		steps {

				// branch name from Jenkins environment variables
		  		echo "My branch is: ${env.BRANCH_NAME}"

				// send build started notifications
				script{
				   if (env.BRANCH_NAME == notificationBranch) {
					slackSend (channel:'#android', color: '#FFFF00', message: "STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
				   }
				}
  				// build command for Sproutling and Cartwheel 
				sh "./gradlew clean"
				sh "./gradlew :cartwheel:assembleCartwheelDevServerDevOTAServerAdhoc --exclude-task lint --exclude-task test"
				sh "./gradlew :cartwheel:assembleCartwheelChinaProdServerProdOTAServerAdhoc --exclude-task lint --exclude-task test"
			}
			post {
				success {
					//tell Jenkins to archive the apks
					archiveArtifacts artifacts: 'cartwheel/build/outputs/apk/cartwheelChinaProdServerProdOTAServer/adhoc/*.apk', fingerprint: true
					archiveArtifacts artifacts: 'cartwheel/build/outputs/apk/cartwheelDevServerDevOTAServer/adhoc/*.apk', fingerprint: true

					// send success message to slack 
					script {
					   if (env.BRANCH_NAME == notificationBranch) {
						slackSend (channel:'#android', color: '#00FF00', message: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
						slackSend (channel:'#android-builds', color: '#00FF00', message: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
					   }
					}

				}
				failure {
					// send failure message to slack
					script{
					   if (env.BRANCH_NAME == notificationBranch) {
						slackSend (channel:'#android', color: '#FF0000', message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
					   }
					}
				}
			}
		} //end stage
	} // end stages
} // end pipeline
