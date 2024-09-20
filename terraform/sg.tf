# public 용 sg생성, 관리자용 ssh 22
resource "aws_security_group" "public-sg" {
  name   = "public-sg"
  vpc_id = aws_vpc.dessert-vpc.id

  ingress {
    from_port = 22
    to_port   = 22
    protocol  = "tcp"

    cidr_blocks = [
      var.my_ip,
      "192.30.252.0/22",
      "185.199.108.0/22",
      "140.82.112.0/20",
      "143.55.64.0/20"
    ]
    ipv6_cidr_blocks = [
      "2a0a:a440::/29",
      "2606:50c0::/32"
    ]
  }

  ingress {
    from_port = 8080
    to_port   = 8080
    protocol  = "tcp"

    cidr_blocks = [
      var.my_ip,
      "192.30.252.0/22",
      "185.199.108.0/22",
      "140.82.112.0/20",
      "143.55.64.0/20"
    ]
    ipv6_cidr_blocks = [
      "2a0a:a440::/29",
      "2606:50c0::/32"
    ]
  }

  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "public-sg"
  }
}


# private용 sg생성, 관리자용 ssh 22, 프론트엔드에서 요청받을 8080필요 (public sg에서만 접근가능)
resource "aws_security_group" "private-sg" {
  name   = "private-sg"
  vpc_id = aws_vpc.dessert-vpc.id

  ingress {
    from_port = 22
    to_port   = 22
    protocol = "tcp"

    security_groups = ["${aws_security_group.public-sg.id}"]
  }


  ingress {
    from_port = 8080
    to_port   = 8080
    protocol  = "tcp"
    security_groups = [
      "${aws_security_group.public-sg.id}",
      "${aws_security_group.alb-sg.id}"
    ]
  }


  ingress {
    from_port = 8000
    to_port   = 8000
    protocol  = "tcp"
    security_groups = [
      "${aws_security_group.public-sg.id}",
      "${aws_security_group.alb-sg.id}"
    ]
  }


  ingress {
    from_port = 8761
    to_port   = 8761
    protocol  = "tcp"
    security_groups = [
      "${aws_security_group.alb-sg.id}"
    ]
  }


  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "private-sg"
  }
}

# db용 sg생성, 관리자용 ssh 22, 백엔드에서 db 요청받을 3306필요 (private sg에서만 접근가능)
resource "aws_security_group" "db-sg" {
  name   = "db-sg"
  vpc_id = aws_vpc.dessert-vpc.id

  ingress {
    from_port = 3306
    to_port   = 3306
    protocol = "tcp"
    /* cidr_blocks = ["0.0.0.0/0"] */

    security_groups = ["${aws_security_group.private-sg.id}"]
  }

  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "db-sg"
  }
}

resource "aws_security_group" "alb-sg" {
  name   = "alb-sg"
  vpc_id = aws_vpc.dessert-vpc.id


  ingress {
    from_port = 8080
    to_port   = 8080
    protocol  = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port = 8000
    to_port   = 8000
    protocol  = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port = 8761
    to_port   = 8761
    protocol  = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "alb-sg"
  }
}