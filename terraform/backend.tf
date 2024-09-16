# tfstate를 저장소
resource "aws_s3_bucket" "dessert_state_bucket" {
  bucket = "dessert-state-bucket"

  # 리소스 삭제 시 테라폼 오류와 함께 종료됨
  lifecycle {
    prevent_destroy = true
  }
}

# S3 버킷에서 버전 관리를 제어하기 위한 리소스를 제공합니다.
resource "aws_s3_bucket_versioning" "dessert_s3bucket_versioning" {
  bucket = aws_s3_bucket.dessert_state_bucket.id
  # 버킷이 파일을 업데이트 마다 새버전을 생성합니다.
  versioning_configuration {
    status = "Enabled"
  }
}

# S3 버킷에 기록된 모든 데이터에 서버 측 암호화을 설정합니다
resource "aws_s3_bucket_server_side_encryption_configuration" "dessert_s3bucket_encryption" {
  bucket = aws_s3_bucket.dessert_state_bucket.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

# S3 버킷 퍼블릭 액세스 차단 구성을 관리합니다.
resource "aws_s3_bucket_public_access_block" "dessert_s3bucket_public_access" {
  bucket                  = aws_s3_bucket.dessert_state_bucket.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

output "s3_bucket_arn" {
  value       = aws_s3_bucket.dessert_state_bucket.arn
  description = "The ARN of the S3 bucket"
}

# 위 실행해서 버켓먼저생성 후 실행
terraform {
  backend "s3" {
    bucket = "dessert-state-bucket"
    key    = "terraform.tfstate"
    region = "ap-northeast-2"
  }
}