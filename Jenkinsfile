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

        // Prevent multiple deployments from running simultaneously
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


        // ========================================================
        // STAGE 9 - DEPLOY APPLICATION
        // ========================================================

        stage('Deploy') {

            steps {

                echo '========================================'
                echo 'Deploying Spring Boot application'
                echo '========================================'

                withCredentials([
                    usernamePassword(
                        credentialsId: 'mysql-local',
                        usernameVariable: 'DB_USERNAME',
                        passwordVariable: 'DB_PASSWORD'
                    )
                ]) {

                    sh '''
                        DEPLOY_DIR="$HOME/jenkins-deploy/TestSpringPersistent"

                        echo "Creating deployment directory..."

                        mkdir -p "$DEPLOY_DIR"


                        echo ""
                        echo "========================================"
                        echo "Stopping previous application"
                        echo "========================================"


                        if [ -f "$DEPLOY_DIR/application.pid" ]; then

                            OLD_PID=$(cat "$DEPLOY_DIR/application.pid")

                            echo "Previous PID: $OLD_PID"

                            if kill -0 "$OLD_PID" 2>/dev/null; then

                                echo "Stopping application..."

                                kill "$OLD_PID"

                                sleep 5

                            else

                                echo "Previous application is not running."

                            fi

                            rm -f "$DEPLOY_DIR/application.pid"

                        else

                            echo "No previous deployment PID found."

                        fi


                        echo ""
                        echo "========================================"
                        echo "Finding generated JAR"
                        echo "========================================"


                        JAR_FILE=$(find target \
                            -maxdepth 1 \
                            -type f \
                            -name "*.jar" \
                            ! -name "*.original" \
                            | head -1)


                        if [ -z "$JAR_FILE" ]; then

                            echo "ERROR: No deployable JAR found."

                            exit 1

                        fi


                        echo "JAR found:"
                        echo "$JAR_FILE"


                        echo ""
                        echo "========================================"
                        echo "Copying JAR to deployment directory"
                        echo "========================================"


                        cp "$JAR_FILE" \
                           "$DEPLOY_DIR/TestSpringPersistent.jar"


                        echo "JAR copied successfully."


                        echo ""
                        echo "========================================"
                        echo "Starting Spring Boot application"
                        echo "========================================"


                        nohup java \
                            -Dspring.datasource.username="$DB_USERNAME" \
                            -Dspring.datasource.password="$DB_PASSWORD" \
                            -jar "$DEPLOY_DIR/TestSpringPersistent.jar" \
                            > "$DEPLOY_DIR/application.log" 2>&1 &


                        NEW_PID=$!


                        echo "$NEW_PID" \
                            > "$DEPLOY_DIR/application.pid"


                        echo "Application started."

                        echo "PID: $NEW_PID"

                        echo "Deployment directory:"
                        echo "$DEPLOY_DIR"
                    '''
                }
            }
        }


        // ========================================================
        // STAGE 10 - VERIFY DEPLOYMENT
        // ========================================================

        stage('Verify Deployment') {

            steps {

                echo '========================================'
                echo 'Verifying Spring Boot deployment'
                echo '========================================'

                sh '''
                    DEPLOY_DIR="$HOME/jenkins-deploy/TestSpringPersistent"

                    echo "Waiting for Spring Boot to start..."

                    sleep 15


                    # ============================================
                    # VERIFY PID FILE
                    # ============================================

                    if [ ! -f "$DEPLOY_DIR/application.pid" ]; then

                        echo "ERROR: application.pid does not exist."

                        echo ""
                        echo "Application log:"

                        cat "$DEPLOY_DIR/application.log" || true

                        exit 1

                    fi


                    PID=$(cat "$DEPLOY_DIR/application.pid")


                    echo "Checking application PID: $PID"


                    # ============================================
                    # VERIFY JAVA PROCESS
                    # ============================================

                    if ! kill -0 "$PID" 2>/dev/null; then

                        echo ""
                        echo "========================================"
                        echo "DEPLOYMENT FAILED"
                        echo "========================================"

                        echo "Spring Boot process is not running."

                        echo ""
                        echo "Application log:"
                        echo "----------------------------------------"

                        cat "$DEPLOY_DIR/application.log" || true

                        echo "----------------------------------------"

                        exit 1

                    fi


                    echo "Spring Boot process is running."


                    # ============================================
                    # VERIFY REST ENDPOINT
                    # ============================================

                    echo ""
                    echo "Checking REST endpoint:"
                    echo "http://localhost:8081/home"


                    RESPONSE=$(curl \
                        --silent \
                        --show-error \
                        --fail \
                        http://localhost:8081/home)


                    echo ""
                    echo "Response from /home:"
                    echo "$RESPONSE"


                    # ============================================
                    # VERIFY EXPECTED RESPONSE
                    # ============================================

                    if [ "$RESPONSE" != "Welcome" ]; then

                        echo ""
                        echo "========================================"
                        echo "DEPLOYMENT FAILED"
                        echo "========================================"

                        echo "Unexpected response from /home."

                        echo "Expected: Welcome"
                        echo "Actual: $RESPONSE"

                        echo ""
                        echo "Application log:"
                        echo "----------------------------------------"

                        cat "$DEPLOY_DIR/application.log" || true

                        echo "----------------------------------------"

                        exit 1

                    fi


                    # ============================================
                    # DEPLOYMENT SUCCESS
                    # ============================================

                    echo ""
                    echo "========================================"
                    echo "DEPLOYMENT SUCCESSFUL"
                    echo "========================================"

                    echo "Application process is running."
                    echo "REST endpoint responded successfully."

                    echo ""
                    echo "PID: $PID"

                    echo "JAR:"
                    echo "$DEPLOY_DIR/TestSpringPersistent.jar"

                    echo "Log:"
                    echo "$DEPLOY_DIR/application.log"
                '''
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

                        <h2>Jenkins CI/CD Pipeline Successful</h2>

                        <p>
                            The complete CI/CD pipeline completed successfully.
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


                        <h3>Continuous Integration Results</h3>

                        <ul>
                            <li>Source code checkout successful</li>
                            <li>Compilation successful</li>
                            <li>Unit tests passed</li>
                            <li>Integration tests passed</li>
                            <li>JaCoCo coverage report generated</li>
                            <li>Spring Boot JAR generated</li>
                            <li>JAR archived in Jenkins</li>
                        </ul>


                        <h3>Continuous Deployment Results</h3>

                        <ul>
                            <li>Previous application instance stopped</li>
                            <li>New Spring Boot JAR deployed</li>
                            <li>Spring Boot application started</li>
                            <li>Application process verified</li>
                            <li>REST /home endpoint verified</li>
                            <li>HTTP deployment verification passed</li>
                        </ul>


                        <p>
                            <b>Deployment Status:</b>
                            SUCCESS
                        </p>


                        <p>
                            <b>Application Port:</b>
                            8081
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

                        <h2>Jenkins CI/CD Pipeline Failed</h2>

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
                            One or more CI/CD pipeline stages failed.
                            This may include compilation, testing,
                            packaging, deployment, or deployment
                            verification.
                        </p>

                        <p>
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