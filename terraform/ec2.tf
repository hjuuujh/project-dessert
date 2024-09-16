resource "aws_instance" "backend" {
  ami = "ami-05d2438ca66594916"

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
  root_block_device {
    volume_type = "gp3"
    volume_size = 20 # Adjust volume size as needed
  }
  depends_on = [aws_security_group.public-sg]
  tags = {
    Name = "Jenkins"
  }
}

# resource "aws_instance" "Jenkins" {
#   ami = "ami-05d2438ca66594916"
#
#   instance_type          = "t3.micro"
#   subnet_id              = aws_subnet.public-subnet.0.id
#   key_name               = var.key_pair_name
#   vpc_security_group_ids = [aws_security_group.public-sg.id]
#   depends_on = [aws_security_group.public-sg]
#   tags = {
#     Name = "Jenkins"
#   }
# }
#
# resource "aws_volume_attachment" "volume_attachement" {
#   volume_id   = "vol-09cc1ced3cc885058"
#   instance_id = aws_instance.Jenkins.id
#   device_name = "/dev/sdg"
# }