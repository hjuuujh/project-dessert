resource "aws_instance" "backend" {
  ami = "ami-05d2438ca66594916" // Ubuntu Server 24.04 LTS (HVM), SSD Volume Type 의 AMI ID

  instance_type          = "t2.micro"
  subnet_id              = aws_subnet.private-subnet.0.id
  key_name               = var.key_pair_name
  vpc_security_group_ids = [aws_security_group.private-sg.id]
  depends_on             = [aws_security_group.private-sg]

  tags = {
    Name = "Dessert Spring Boot"
  }
}

resource "aws_instance" "jenkins" {
  ami = "ami-05d2438ca66594916"

  instance_type          = "t3.micro"
  subnet_id              = aws_subnet.public-subnet.0.id
  key_name               = var.key_pair_name
  vpc_security_group_ids = [aws_security_group.public-sg.id]

  root_block_device { // 설정 안하면 기본사이즈 8
    volume_type = "gp3"
    volume_size = 20 # 원하는 볼륨 사이즈
  }

  depends_on = [aws_security_group.public-sg]
  tags = {
    Name = "Jenkins"
  }
}