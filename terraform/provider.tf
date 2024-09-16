# aws provider 설정
terraform {
  required_version = "~> 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 5.59.0"
    }
  }
}

provider "aws" {
  region = "ap-northeast-2"
}