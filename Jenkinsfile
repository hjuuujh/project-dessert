pipeline {
    agent any

    stages {
        stage('checkout') {
            steps {
                script{
                    echo "$ref"
                    def ref = "$ref"
                    def st = ref.split('/')
                    def branch = st[st.size()-1]
                    echo "$branch"
                    git branch: "$branch" , credentialsId: 'github', url: 'https://github.com/hjuuujh/project-dessert'
                }
            }
        }

//        stage('build') {
//            steps {
//                echo 'Clone'
//                sh 'ls -al'
//                sh 'chmod +x gradlew'
//                sh './gradlew member-api:build'
//
//            }
//        }

//        stage('build image and docker hub push') {
//            steps {
//                dir('member-api') {
//                    script {
//                        sh 'ls -al'
//                        dockerImage = docker.build "hjuuujh/member-api"
//                        docker.withRegistry('', 'dockerhub') {
//                            dockerImage.push("5.0")
//                        }
//                    }
//                }
//            }
//        }

//        stage('deploy') {
//            steps {
//                sh 'ls -al'
//                sh 'scp -o StrictHostKeyChecking=no -i /var/lib/jenkins/workspace/dessert-key-pair.pem -r docker-compose.yml ubuntu@ec2-43-201-61-191.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/spring'
//                sh 'scp -o StrictHostKeyChecking=no -i /var/lib/jenkins/workspace/dessert-key-pair.pem -r conf.d ubuntu@ec2-43-201-61-191.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/spring'
//                sh 'scp -o StrictHostKeyChecking=no -i /var/lib/jenkins/workspace/dessert-key-pair.pem -r ./member-api/deploy.sh ubuntu@ec2-43-201-61-191.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/spring/member-api-deploy.sh'
//                sh 'ssh -o StrictHostKeyChecking=no -i /var/lib/jenkins/workspace/dessert-key-pair.pem ubuntu@ec2-43-201-61-191.ap-northeast-2.compute.amazonaws.com -T "sh ./spring/member-api-deploy.sh" '
//
//
//            }
//        }

    }
}