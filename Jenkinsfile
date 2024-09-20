def getBranch() {
    ref = "$ref"
    st = ref.split('/')
    branch = st[st.size() - 1]
    return branch
}

pipeline {
    agent any
    options { skipDefaultCheckout() }
    environment {
        branch = getBranch()
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    echo "$ref"
                    echo "$branch"
                    git branch: "dev/$branch", credentialsId: 'github', url: 'https://github.com/hjuuujh/project-dessert'
                }
            }
        }

//        stage('Build') {
//            steps {
//                echo "$branch"
//                sh 'ls -al'
//                sh 'chmod +x gradlew'
//                sh './gradlew ' + "$branch" + ':build'
//
//            }
//        }
//
//        stage('Openapi3') {
//            when { expression { return "$branch".contains('api') } }
//            steps {
//                sh './gradlew ' + "$branch" + ':openapi3'
//
//            }
//        }
//
//        stage('Build Image And Docker Hub Push') {
//            steps {
//                dir("$branch") {
//                    script {
//                        sh 'ls -al'
//
//                        dockerImage = docker.build "hjuuujh/" + "$branch"
//                        docker.withRegistry('', 'dockerhub') {
//                            dockerImage.push("2.0")
//                        }
//                    }
//                }
//            }
//        }

        stage('Copy openapi3.yaml') {
            when { expression { return "$branch".contains('-api') } }
            steps {
                sh 'scp -o StrictHostKeyChecking=no -i /var/lib/jenkins/workspace/dessert-key-pair.pem -r ./' + "$branch" + '/src/main/resources/static/docs/openapi/openapi3.yaml ubuntu@ec2-43-201-61-191.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/spring/openapi/' + "$branch" + '-openapi3.yaml'
            }
        }

//        stage('Deploy') {
//            steps {
//                sh 'ls -al'
//                sh 'scp -o StrictHostKeyChecking=no -i /var/lib/jenkins/workspace/dessert-key-pair.pem -r docker-compose.yml ubuntu@ec2-43-201-61-191.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/spring'
//                sh 'scp -o StrictHostKeyChecking=no -i /var/lib/jenkins/workspace/dessert-key-pair.pem -r ./' + "$branch" + '/deploy.sh ubuntu@ec2-43-201-61-191.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/spring/' + "$branch" + '-deploy.sh'
//                sh 'ssh -o StrictHostKeyChecking=no -i /var/lib/jenkins/workspace/dessert-key-pair.pem ubuntu@ec2-43-201-61-191.ap-northeast-2.compute.amazonaws.com -T "sh ./spring/' + "$branch" + '-deploy.sh" '
//            }
//        }

    }
}