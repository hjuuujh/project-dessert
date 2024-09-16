resource "aws_alb_target_group" "backend-8080" {
  name     = "backend-target-group-8080"
  port     = 8080
  protocol = "HTTP"
  vpc_id   = aws_vpc.dessert-vpc.id

  health_check {
    interval            = 300
    path                = "/api/member/signup/health_check"
    healthy_threshold   = 3
    unhealthy_threshold = 3
  }

  tags = { Name = "Spring Boot Target Group" }
}

resource "aws_alb_target_group_attachment" "backend-8080" {
  target_group_arn = aws_alb_target_group.backend-8080.arn
  target_id        = aws_instance.backend.id
  depends_on       = [aws_alb_target_group.backend-8080]
  port             = 8080
}

resource "aws_alb_target_group" "eureka-8761" {
  name     = "eureka-target-group-8761"
  port     = 8761
  protocol = "HTTP"
  vpc_id   = aws_vpc.dessert-vpc.id

  health_check {
    interval            = 30
    path                = "/"
    healthy_threshold   = 3
    unhealthy_threshold = 3
  }

  tags = { Name = "Spring Boot Target Group" }
}

resource "aws_alb_target_group_attachment" "eureka-8761" {
  target_group_arn = aws_alb_target_group.eureka-8761.arn
  target_id        = aws_instance.backend.id
  depends_on       = [aws_alb_target_group.eureka-8761]
  port             = 8761
}

# resource "aws_alb_target_group" "backend-80" {
#   name     = "backend-target-group-80"
#   port     = 80
#   protocol = "HTTP"
#   vpc_id   = aws_vpc.dessert-vpc.id
#
#   health_check {
#     interval            = 30
#     path                = "/hello"
#     healthy_threshold   = 3
#     unhealthy_threshold = 3
#   }
#
#   tags = { Name = "Backend Target Group" }
# }
#
# resource "aws_alb_target_group_attachment" "backend-8080" {
#   target_group_arn = aws_alb_target_group.backend-80.arn
#   target_id        = aws_instance.backend.id
#   depends_on       = [aws_alb_target_group.backend-80]
#   port             = 8080
# }

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
# resource "aws_alb_listener" "listner-8080" {
#   load_balancer_arn = aws_alb.dessert-alb.arn
#   port              = 8080
#   protocol          = "HTTP"
#
#   default_action {
#     type = "redirect"
#
#     redirect {
#       port        = "443"
#       protocol    = "HTTPS"
#       status_code = "HTTP_301"
#     }
#   }
# }

resource "aws_alb_listener" "listner-8080" {
  load_balancer_arn = aws_alb.dessert-alb.arn
  port              = 8080
  protocol          = "HTTP"
  default_action {
    type             = "forward"
    target_group_arn = aws_alb_target_group.backend-8080.arn
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
