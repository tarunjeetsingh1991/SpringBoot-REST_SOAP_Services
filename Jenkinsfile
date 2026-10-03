pipeline {

    agent any

    // ============================================================
    // ENVIRONMENT VARIABLES
    // ============================================================

    environment {
        APP_NAME = 'TestSpringPersistent'
        EMAIL_RECIPIENT = 'singh.tarunjeet1991@gmail.com'
    }


    // ============================================================
    // PIPELINE OPTIONS
    // ============================================================

    options {

        // Add timestamps to Jenkins console output
        timestamps()

        // Prevent multiple builds of this project
        // from running at the same time
        disableConcurrentBuilds()

        // Keep only the latest 10 Jenkins builds
        buildDiscarder(
            logRotator(numToKeepStr: '10')
        )
    }


    // ============================================================
    // STAGES
    // ============================================================

    stages {


        // ========================================================
        // STAGE 1 - CHECKOUT
        // ========================================================

        stage('Checkout') {

            steps {

                echo '========================================'
                echo 'Checking out source code from GitHub'
                echo '========================================'

                checkout scm
            }
        }


        // ========================================================
        // STAGE 2 - ENVIRONMENT CHECK
        // ========================================================

        stage('Environment Check') {

            steps {

                echo '========================================'
                echo 'Checking build environment'
                echo '========================================'

                sh '''
                    echo "Java Version:"
                    java -version

                    echo ""
                    echo "Git Version:"
                    git --version

                    echo ""
                    echo "Maven Wrapper Version:"
                    ./mvnw -version
                '''
            }
        }


        // ========================================================
        // STAGE 3 - COMPILE
        // ========================================================

        stage('Compile') {

            steps {

                echo '========================================'
                echo 'Compiling Spring Boot application'
                echo '========================================'

                sh '''
                    chmod +x mvnw
                    ./mvnw clean compile
                '''
            }
        }


        // ========================================================
        // STAGE 4 - UNIT + INTEGRATION TESTS
        // ========================================================

        stage('Unit & Integration Tests') {

            steps {

                echo '========================================'
                echo 'Running automated tests'
                echo '========================================'

                sh './mvnw test'
            }

            post {

                always {

                    echo 'Publishing JUnit test results'

                    junit allowEmptyResults: true,
                          testResults: 'target/surefire-reports/*.xml'
                }
            }
        }


        // ========================================================
        // STAGE 5 - JACOCO CODE COVERAGE
        // ========================================================

        stage('JaCoCo Coverage') {

            steps {

                echo '========================================'
                echo 'Generating JaCoCo coverage report'
                echo '========================================'

                sh './mvnw jacoco:report'
            }

            post {

                always {

                    archiveArtifacts(
                        artifacts: 'target/site/jacoco/**/*',
                        allowEmptyArchive: true
                    )
                }
            }
        }


        // ========================================================
        // STAGE 6 - PACKAGE APPLICATION
        // ========================================================

        stage('Package') {

            steps {

                echo '========================================'
                echo 'Packaging Spring Boot application'
                echo '========================================'

                // Tests already ran in the previous stage.
                sh './mvnw package -DskipTests'
            }
        }


        // ========================================================
        // STAGE 7 - VERIFY JAR
        // ========================================================

        stage('Verify Artifact') {

            steps {

                echo '========================================'
                echo 'Verifying generated JAR'
                echo '========================================'

                sh '''
                    echo "Generated artifacts:"
                    ls -lh target/*.jar
                '''
            }
        }


        // ========================================================
        // STAGE 8 - ARCHIVE JAR
        // ========================================================

        stage('Archive Artifact') {

            steps {

                echo '========================================'
                echo 'Archiving Spring Boot JAR'
                echo '========================================'

                archiveArtifacts(
                    artifacts: 'target/*.jar',
                    fingerprint: true
                )
            }
        }
    }


    // ============================================================
    // POST BUILD ACTIONS
    // ============================================================

    post {


        // ========================================================
        // ALWAYS
        // ========================================================

        always {

            echo ''
            echo '========================================'
            echo 'Jenkins Pipeline Finished'
            echo '========================================'

            echo "Project: ${env.APP_NAME}"
            echo "Job: ${env.JOB_NAME}"
            echo "Build Number: ${env.BUILD_NUMBER}"
            echo "Build URL: ${env.BUILD_URL}"
        }


        // ========================================================
        // SUCCESS
        // ========================================================

        success {

            echo ''
            echo '========================================'
            echo 'BUILD SUCCESSFUL'
            echo '========================================'


            emailext(

                to: "${EMAIL_RECIPIENT}",

                subject:
                    "SUCCESS: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",

                body: """
                    <html>

                    <body>

                        <h2>Jenkins Build Successful</h2>

                        <p>
                            The CI/CD pipeline completed successfully.
                        </p>

                        <table border="1"
                               cellpadding="8"
                               cellspacing="0">

                            <tr>
                                <td><b>Project</b></td>
                                <td>${env.APP_NAME}</td>
                            </tr>

                            <tr>
                                <td><b>Jenkins Job</b></td>
                                <td>${env.JOB_NAME}</td>
                            </tr>

                            <tr>
                                <td><b>Build Number</b></td>
                                <td>${env.BUILD_NUMBER}</td>
                            </tr>

                            <tr>
                                <td><b>Status</b></td>
                                <td>SUCCESS</td>
                            </tr>

                            <tr>
                                <td><b>Branch</b></td>
                                <td>${env.BRANCH_NAME ?: 'main'}</td>
                            </tr>

                        </table>

                        <h3>Pipeline Results</h3>

                        <ul>
                            <li>Source code checkout successful</li>
                            <li>Compilation successful</li>
                            <li>Unit tests passed</li>
                            <li>Integration tests passed</li>
                            <li>JaCoCo coverage report generated</li>
                            <li>Spring Boot JAR generated</li>
                            <li>JAR archived in Jenkins</li>
                        </ul>

                        <p>
                            <a href="${env.BUILD_URL}">
                                Open Jenkins Build
                            </a>
                        </p>

                    </body>

                    </html>
                """,

                mimeType: 'text/html'
            )
        }


        // ========================================================
        // FAILURE
        // ========================================================

        failure {

            echo ''
            echo '========================================'
            echo 'BUILD FAILED'
            echo '========================================'


            emailext(

                to: "${EMAIL_RECIPIENT}",

                subject:
                    "FAILURE: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",

                body: """
                    <html>

                    <body>

                        <h2>Jenkins Build Failed</h2>

                        <p>
                            The CI/CD pipeline encountered an error.
                        </p>

                        <table border="1"
                               cellpadding="8"
                               cellspacing="0">

                            <tr>
                                <td><b>Project</b></td>
                                <td>${env.APP_NAME}</td>
                            </tr>

                            <tr>
                                <td><b>Jenkins Job</b></td>
                                <td>${env.JOB_NAME}</td>
                            </tr>

                            <tr>
                                <td><b>Build Number</b></td>
                                <td>${env.BUILD_NUMBER}</td>
                            </tr>

                            <tr>
                                <td><b>Status</b></td>
                                <td>FAILURE</td>
                            </tr>

                        </table>

                        <p>
                            One or more pipeline stages failed.
                            Please review the Jenkins console output
                            to identify the problem.
                        </p>

                        <p>
                            <a href="${env.BUILD_URL}">
                                Open Failed Jenkins Build
                            </a>
                        </p>

                    </body>

                    </html>
                """,

                mimeType: 'text/html'
            )
        }


        // ========================================================
        // UNSTABLE
        // ========================================================

        unstable {

            echo 'Build completed with UNSTABLE status.'

            emailext(

                to: "${EMAIL_RECIPIENT}",

                subject:
                    "UNSTABLE: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",

                body: """
                    <html>

                    <body>

                        <h2>Jenkins Build Unstable</h2>

                        <p>
                            Jenkins Build #${env.BUILD_NUMBER}
                            completed with an UNSTABLE status.
                        </p>

                        <p>
                            Please check the test results and
                            Jenkins console output.
                        </p>

                        <p>
                            <a href="${env.BUILD_URL}">
                                Open Jenkins Build
                            </a>
                        </p>

                    </body>

                    </html>
                """,

                mimeType: 'text/html'
            )
        }
    }
}