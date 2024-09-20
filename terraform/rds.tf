# spring boot 접근위한 타겟 그룹
# port는 spring boot api gateway의 포트
resource "aws_alb_target_group" "backend-8080" {
  name     = "spring-target-group-8080"
  port     = 8080
  protocol = "HTTP"
  vpc_id   = aws_vpc.dessert-vpc.id

  health_check {
    interval            = 300
    path                = "/api/member/signup/health-check"
    healthy_threshold   = 3
    unhealthy_threshold = 3
  }

  tags = { Name = "Spring Boot Target Group" }
}

# spring boot ec2 instance와 target group 연결
resource "aws_alb_target_group_attachment" "backend-8080" {
  target_group_arn = aws_alb_target_group.backend-8080.arn
  target_id        = aws_instance.backend.id
  depends_on       = [aws_alb_target_group.backend-8080]
  port             = 8080
}

# swagger-ui 접근위한 타겟 그룹
# port는 spring boot swagger-ui의 포트
resource "aws_alb_target_group" "swagger-8000" {
  name     = "swagger-target-group-8000"
  port     = 8000
  protocol = "HTTP"
  vpc_id   = aws_vpc.dessert-vpc.id

  health_check {
    interval            = 300
    path                = "/"
    healthy_threshold   = 3
    unhealthy_threshold = 3
  }

  tags = { Name = "Swagger Target Group" }
}

# spring boot ec2 instance와 target group 연결
resource "aws_alb_target_group_attachment" "swagger-8000" {
  target_group_arn = aws_alb_target_group.swagger-8000.arn
  target_id        = aws_instance.backend.id
  depends_on       = [aws_alb_target_group.swagger-8000]
  port             = 8000
}

# spring boot eureka server 접근위한 타겟 그룹
resource "aws_alb_target_group" "eureka-8761" {
  name     = "eureka-target-group-8761"
  port     = 8761
  protocol = "HTTP"
  vpc_id   = aws_vpc.dessert-vpc.id

  health_check {
    interval            = 300
    path                = "/"
    healthy_threshold   = 3
    unhealthy_threshold = 3
  }

  tags = { Name = "Spring Boot Eureka Server" }
}

# spring boot ec2 instance와 target group 연결
resource "aws_alb_target_group_attachment" "eureka-8761" {
  target_group_arn = aws_alb_target_group.eureka-8761.arn
  target_id        = aws_instance.backend.id
  depends_on       = [aws_alb_target_group.eureka-8761]
  port             = 8761
}

# application load balancer 생성
resource "aws_alb" "dessert-alb" {
  name               = "dessert-alb"
  internal           = false
  security_groups    = [aws_security_group.alb-sg.id]
  load_balancer_type = "application"
  ip_address_type    = "ipv4"
  subnets            = [aws_subnet.public-subnet.0.id, aws_subnet.public-subnet.1.id]

  tags = {
    Name = "Dessert Alb"
  }


  lifecycle { create_before_destroy = true }
}

# alb의 리스너
# 포트와 포워딩할 타겟그룹 지정
resource "aws_alb_listener" "listner-8080" {
  load_balancer_arn = aws_alb.dessert-alb.arn
  port              = 8080
  protocol          = "HTTP"
  default_action {
    type             = "forward"
    target_group_arn = aws_alb_target_group.backend-8080.arn
  }
}

resource "aws_alb_listener" "listner-8000" {
  load_balancer_arn = aws_alb.dessert-alb.arn
  port              = 8000
  protocol          = "HTTP"
  default_action {
    type             = "forward"
    target_group_arn = aws_alb_target_group.swagger-8000.arn
  }
}

resource "aws_alb_listener" "listner-8761" {
  load_balancer_arn = aws_alb.dessert-alb.arn
  port              = 8761
  protocol          = "HTTP"
  default_action {
    type             = "forward"
    target_group_arn = aws_alb_target_group.eureka-8761.arn
  }
}
