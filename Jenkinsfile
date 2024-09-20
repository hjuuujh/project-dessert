def getBranch(){
    ref = "$ref"
    st = ref.split('/')
    branch = st[st.size()-1]
    return branch
}

pipeline {
    agent any
    options { skipDefaultCheckout() }
    environment {
        branch = getBranch()
    }

    stages {
        stage('checkout') {
            steps {
                script{
                    echo "$ref"
                    echo "$branch"
                    git branch: "dev/$branch" , credentialsId: 'github', url: 'https://github.com/hjuuujh/project-dessert'
                }
            }
        }

        stage('build') {
            steps {
                script{
                    echo "$branch"
                    sh 'ls -al'
                    sh 'chmod +x gradlew'
                    sh './gradlew '+"$branch"+':build'
                    sh './gradlew '+"api-gateway"+':build'
//                    sh './gradlew '+"$branch"+':openapi3'

                }

            }
        }

        stage('build image and docker hub push') {
            steps {
//                dir("$branch") {
                dir("api-gateway") {
                    script {
                        sh 'ls -al'

//                        dockerImage = docker.build "hjuuujh/"+"$branch"
                        dockerImage = docker.build "hjuuujh/"+"api-gateway"
                        docker.withRegistry('', 'dockerhub') {
                            dockerImage.push("2.0")
                        }
                    }
                }
            }
        }

        stage('deploy') {
            steps {
                sh 'ls -al'
                sh 'scp -o StrictHostKeyChecking=no -i /var/lib/jenkins/workspace/dessert-key-pair.pem -r docker-compose.yml ubuntu@ec2-43-201-61-191.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/spring'
//                sh 'scp -o StrictHostKeyChecking=no -i /var/lib/jenkins/workspace/dessert-key-pair.pem -r ./'+"$branch"+'/deploy.sh ubuntu@ec2-43-201-61-191.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/spring/'+"$branch"+'-deploy.sh'
                sh 'scp -o StrictHostKeyChecking=no -i /var/lib/jenkins/workspace/dessert-key-pair.pem -r ./'+"api-gateway"+'/deploy.sh ubuntu@ec2-43-201-61-191.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/spring/'+"api-gateway"+'-deploy.sh'
//                sh 'scp -o StrictHostKeyChecking=no -i /var/lib/jenkins/workspace/dessert-key-pair.pem -r ./'+"$branch"+'/src/main/resources/static/docs/openapi/openapi3.yaml ubuntu@ec2-43-201-61-191.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/spring/openapi/'+"$branch"+'-openapi3.yml'
                sh 'scp -o StrictHostKeyChecking=no -i /var/lib/jenkins/workspace/dessert-key-pair.pem -r ./'+"api-gateway"+'/src/main/resources/static/docs/openapi/openapi3.yaml ubuntu@ec2-43-201-61-191.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/spring/openapi/'+"api-gateway"+'-openapi3.yml'
//                sh 'ssh -o StrictHostKeyChecking=no -i /var/lib/jenkins/workspace/dessert-key-pair.pem ubuntu@ec2-43-201-61-191.ap-northeast-2.compute.amazonaws.com -T "sh ./spring/'+"$branch"+'-deploy.sh" '
                sh 'ssh -o StrictHostKeyChecking=no -i /var/lib/jenkins/workspace/dessert-key-pair.pem ubuntu@ec2-43-201-61-191.ap-northeast-2.compute.amazonaws.com -T "sh ./spring/'+"api-gateway"+'-deploy.sh" '
            }
        }

    }
}