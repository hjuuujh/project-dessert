# tfstate를 저장소
# s3먼저 생성한다음 설정해야 에러안남
terraform {
  backend "s3" {
    bucket = "dessert-tf-state"
    key    = "terraform.state"
    region = "ap-northeast-2"
  }
}