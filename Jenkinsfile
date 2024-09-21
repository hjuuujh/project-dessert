def getBranch() {
    ref = "$ref" // 푸시한 브랜치로 checkout위해 파라미터에서 브랜치 가져옴
    st = ref.split('/')
    branch = st[st.size() - 1]
    return branch
}

pipeline {
    agent any
    options {
        // 푸시한 브랜치로 checkout 해야하므로 scm에서 설정한 branch로 checkout하는 과정 생략
        skipDefaultCheckout()
    }
    environment {
        // 여기서 설정하면 stage 전체에서 사용가능
        branch = getBranch()
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    // branch 확인
                    echo "$branch"

                    // 푸시한 브랜치로 checkout, credential 에서 설정한 아이디
                    git branch: "dev/$branch", credentialsId: 'github', url: 'https://github.com/hjuuujh/project-dessert'
//                    sh 'git pull origin '+"dev/$branch"
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    sh 'ls -al'

//                    // build위한 권한 변경
//                    sh 'chmod +x gradlew'
//
//                    // clean
//                    sh './gradlew clean'
//
//                    // 푸시한 서비스만 빌드
//                    sh './gradlew ' + "$branch" + ':build'
                }

            }
        }

//        stage('Openapi3') {
//            // when 이용해 서비스 재배포시에만 openapi3 yaml 생성
//            when { expression { return "$branch".contains('-api') } }
//            steps {
//                sh './gradlew ' + "$branch" + ':openapi3'
//
//            }
//        }
//
//        stage('Build Image and Docker Hub Push') {
//            steps {
//                dir("$branch") {
//                    script {
//                        sh 'ls -al'
//
//                        // 빌드한것 도커 이미지로 빌드
//                        dockerImage = docker.build "hjuuujh/" + "$branch"
//
//                        // credential에서 설정한 id 이용
//                        docker.withRegistry('', 'dockerhub') {
//                            // 버전 지정
//                            dockerImage.push("2.0")
//                        }
//                    }
//                }
//            }
//        }
//
//        stage('Copy openapi3.yaml') {
//            // when 이용해 서비스 재배포시에만 openapi3 yaml 배포 서버로 전송
//            when { expression { return "$branch".contains('-api') } }
//            steps {
//                sh 'scp -o StrictHostKeyChecking=no -i /var/lib/jenkins/workspace/dessert-key-pair.pem -r ./' + "$branch" + '/src/main/resources/static/docs/openapi/openapi3.yaml ubuntu@ec2-43-201-61-191.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/spring/openapi/' + "$branch" + '-openapi3.yaml'
//            }
//        }
//
//        stage('Deploy') {
//            steps {
//                sh 'ls -al'
//                // 배포서버로 docker-compose.yml 전송
//                sh 'scp -o StrictHostKeyChecking=no -i /var/lib/jenkins/workspace/dessert-key-pair.pem -r docker-compose.yml ubuntu@ec2-43-201-61-191.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/spring'
//
//                // deploy 명령 변경된 경우 전송
//                sh 'scp -o StrictHostKeyChecking=no -i /var/lib/jenkins/workspace/dessert-key-pair.pem -r ./' + "$branch" + '/deploy.sh ubuntu@ec2-43-201-61-191.ap-northeast-2.compute.amazonaws.com:/home/ubuntu/spring/' + "$branch" + '-deploy.sh'
//
//                // 쉘스크립트 이용해 배포
//                sh 'ssh -o StrictHostKeyChecking=no -i /var/lib/jenkins/workspace/dessert-key-pair.pem ubuntu@ec2-43-201-61-191.ap-northeast-2.compute.amazonaws.com -T "sh ./spring/' + "$branch" + '-deploy.sh" '
//            }
//        }
    }
}
