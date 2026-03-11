pipeline {
    agent any

    tools {
        jdk 'jdk21'
    }

    environment {
        REPO_URL = 'https://github.com/D7S5/discord.git'
        BRANCH = 'main'

        REMOTE_USER = 'ubuntu'
        REMOTE_HOST = '54.116.132.182'
        REMOTE_DIR = '/home/ubuntu/discord'

        JAR_FILE = 'build/libs/discord-0.0.1-SNAPSHOT.jar'
        REMOTE_JAR = '/home/ubuntu/discord/app.jar'
        REMOTE_ENV = '/home/ubuntu/discord/.env'
        REMOTE_LOG = '/home/ubuntu/discord/app.log'
        PID_FILE = '/home/ubuntu/discord/app.pid'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: "${BRANCH}", url: "${REPO_URL}"
            }
        }

        stage('Prepare') {
            steps {
                sh 'chmod +x gradlew'
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean bootJar -x test'
            }
        }

        stage('Verify Artifact') {
            steps {
                sh '''
                    ls -al build/libs
                    test -f ${JAR_FILE}
                '''
            }
        }

        stage('Create .env') {
            steps {
                withCredentials([
                    string(credentialsId: 'DISCORD_DB_URL', variable: 'DB_URL'),
                    string(credentialsId: 'DISCORD_DB_USERNAME', variable: 'DB_USERNAME'),
                    string(credentialsId: 'DISCORD_DB_PASSWORD', variable: 'DB_PASSWORD'),
                    string(credentialsId: 'DISCORD_GOOGLE_CLIENT_ID', variable: 'GOOGLE_CLIENT_ID'),
                    string(credentialsId: 'DISCORD_GOOGLE_CLIENT_SECRET', variable: 'GOOGLE_CLIENT_SECRET'),
                    string(credentialsId: 'DISCORD_JWT_SECRET', variable: 'JWT_SECRET'),
                    string(credentialsId: 'DISCORD_DEFAULT_SERVER_ICON', variable: 'DEFAULT_SERVER_ICON'),
                    string(credentialsId: 'DISCORD_AWS_ACCESS_KEY', variable: 'AWS_ACCESS_KEY'),
                    string(credentialsId: 'DISCORD_AWS_SECRET_KEY', variable: 'AWS_SECRET_KEY'),
                    string(credentialsId: 'DISCORD_TOSS_SECRET_KEY', variable: 'TOSS_SECRET_KEY')
                ]) {
                    sh '''
                        cat > .env <<EOF
DB_URL=${DB_URL}
DB_USERNAME=${DB_USERNAME}
DB_PASSWORD=${DB_PASSWORD}

GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}

JWT_SECRET=${JWT_SECRET}

DEFAULT_SERVER_ICON=${DEFAULT_SERVER_ICON}

AWS_ACCESS_KEY=${AWS_ACCESS_KEY}
AWS_SECRET_KEY=${AWS_SECRET_KEY}

TOSS_SECRET_KEY=${TOSS_SECRET_KEY}
EOF
                    '''
                }
            }
        }

        stage('Copy Files to EC2') {
            steps {
                sshagent(credentials: ['discord-prod-ssh']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} "mkdir -p ${REMOTE_DIR}"

                        scp -o StrictHostKeyChecking=no ${JAR_FILE} ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_JAR}
                        scp -o StrictHostKeyChecking=no .env ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_ENV}
                    '''
                }
            }
        }

        stage('Run App') {
            steps {
                sshagent(credentials: ['discord-prod-ssh']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} "
                            mkdir -p ${REMOTE_DIR} &&
                            cd ${REMOTE_DIR} &&

                            if [ -f ${PID_FILE} ]; then
                                OLD_PID=\$(cat ${PID_FILE});
                                if ps -p \$OLD_PID > /dev/null 2>&1; then
                                    kill \$OLD_PID || true;
                                    sleep 5;
                                fi
                                rm -f ${PID_FILE};
                            fi &&

                            pkill -f 'java -jar ${REMOTE_JAR}' || true &&
                            sleep 3 &&

                            nohup java -jar ${REMOTE_JAR} > ${REMOTE_LOG} 2>&1 < /dev/null &
                            echo \$! > ${PID_FILE}
                            sleep 5

                            echo '=== app.pid ==='
                            cat ${PID_FILE}

                            echo '=== process check ==='
                            ps -ef | grep app.jar | grep -v grep || true

                            echo '=== recent log ==='
                            tail -n 50 ${REMOTE_LOG} || true
                        "
                    '''
                }
            }
        }
    }

    post {
        success {
            echo '배포 성공'
        }
        failure {
            echo '배포 실패'
        }
        always {
            sh 'rm -f .env || true'
            archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
        }
    }
}