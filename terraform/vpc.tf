# vpc B클래스로 생성
resource "aws_vpc" "dessert-vpc" {
  cidr_block           = "10.1.0.0/16"
  instance_tenancy     = "default"
  enable_dns_hostnames = true
  enable_dns_support   = true
  tags = {
    "Name" = "dessert-vpc"
  }
}

# public subnet 2개 생성, count사용한 반복
resource "aws_subnet" "public-subnet" {
  count             = length(var.aws_public_subnets)
  vpc_id            = aws_vpc.dessert-vpc.id
  cidr_block        = var.aws_public_subnets[count.index]
  availability_zone = var.aws_azs[count.index]

  map_public_ip_on_launch = true
  tags = {
    "Name" = "public-subnet-${count.index}"
  }
}

# private subnet 2개 생성, count사용한 반복
resource "aws_subnet" "private-subnet" {
  count             = length(var.aws_private_subnets)
  vpc_id            = aws_vpc.dessert-vpc.id
  cidr_block        = var.aws_private_subnets[count.index]
  availability_zone = var.aws_azs[count.index]

  map_public_ip_on_launch = true
  tags = {
    "Name" = "private-subnet-${count.index}"
  }
}

# db subnet 2개 생성, count사용한 반복
resource "aws_subnet" "db-subnet" {
  count             = length(var.aws_db_subnets)
  vpc_id            = aws_vpc.dessert-vpc.id
  cidr_block        = var.aws_db_subnets[count.index]
  availability_zone = var.aws_azs[count.index]

  map_public_ip_on_launch = true
  tags = {
    "Name" = "db-subnet-${count.index}"
  }
}

# internet gatway생성, vpc연결
resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.dessert-vpc.id

  tags = {
    Name = "igw"
  }
}

# public subnet용 routing table 규칙 생성
resource "aws_route_table" "public-route-table" {
  vpc_id = aws_vpc.dessert-vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = {
    Name = "public_route_table"
  }
}

resource "aws_route_table_association" "public-routing" {
  count = length(var.aws_public_subnets)

  route_table_id = aws_route_table.public-route-table.id
  subnet_id      = aws_subnet.public-subnet.*.id[count.index]
}

// =======================================================
# NAT Gateway에 부여할 Elastic IP를 생성
resource "aws_eip" "eip" {
  domain = "vpc"
}

# private subnet의 출구역할인 nat gateway 생성
resource "aws_nat_gateway" "nat" {
  allocation_id     = aws_eip.eip.id
  subnet_id         = aws_subnet.public-subnet.0.id
  depends_on        = [aws_eip.eip]
  connectivity_type = "public"
}

# private subnet용 routing table 규칙 생성
resource "aws_route_table" "private-route-table" {
  vpc_id = aws_vpc.dessert-vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_nat_gateway.nat.id
  }

  tags = {
    Name = "private_route_table"
  }
}

# private subnet에 생성한 routing table 연결
resource "aws_route_table_association" "private-routing" {
  count = length(var.aws_private_subnets)

  route_table_id = aws_route_table.private-route-table.id
  subnet_id      = aws_subnet.private-subnet.*.id[count.index]

}

resource "aws_route_table_association" "db-routing" {
  count = length(var.aws_db_subnets)

  route_table_id = aws_route_table.private-route-table.id
  subnet_id      = aws_subnet.db-subnet.*.id[count.index]

}