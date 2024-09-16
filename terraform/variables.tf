variable "my_ip" {
  type    = string
  default = "121.88.31.13/32"
}

variable "key_pair_name" {
  type    = string
  default = "dessert-key-pair"
}
#
# variable "key_pair_name" {
#   type    = string
#   default = "dessert-key-pair"
# }


# public subnet의 cidr 목록
variable "aws_public_subnets" {
  type    = list(string)
  default = ["10.1.1.0/26", "10.1.1.64/26"]
}

# private subnet의 cidr 목록
variable "aws_private_subnets" {
  type    = list(string)
  default = ["10.1.1.128/27", "10.1.1.160/27"]
}

# db subnet의 cidr 목록
variable "aws_db_subnets" {
  type    = list(string)
  default = ["10.1.1.192/27", "10.1.1.224/27"]
}

# vpc 가용영역 목록
variable "aws_azs" {
  type    = list(string)
  default = ["ap-northeast-2a", "ap-northeast-2c"]
}