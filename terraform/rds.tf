module "db" {
  source = "terraform-aws-modules/rds/aws"

  identifier = "dessert-db"

  engine = "mysql"
  /* engine_version    = "8.0.31" */
  instance_class    = "db.t3.micro"
  allocated_storage = 10

  db_name                     = "dessertDB"
  username                    = "admin"
  manage_master_user_password = false
  password                    = "qwer1234"
  port                        = "3306"

  /* iam_database_authentication_enabled = true */

  vpc_security_group_ids = [aws_security_group.db-sg.id]

  publicly_accessible = true

  tags = {
    Owner       = "user"
    Environment = "dev"
  }

  # DB subnet group
  create_db_subnet_group = true
  subnet_ids             = [aws_subnet.db-subnet.0.id, aws_subnet.db-subnet.1.id]
  # DB parameter group
  family = "mysql8.0"

  # DB option group
  major_engine_version = "8.0"

  # Database Deletion Protection
  /* deletion_protection = true */

  parameters = [
    {
      name  = "character_set_client"
      value = "utf8mb4"
    },
    {
      name  = "character_set_server"
      value = "utf8mb4"
    }
  ]

  options = [
    {
      option_name = "MARIADB_AUDIT_PLUGIN"

      option_settings = [
        {
          name  = "SERVER_AUDIT_EVENTS"
          value = "CONNECT"
        },
        {
          name  = "SERVER_AUDIT_FILE_ROTATIONS"
          value = "37"
        },
      ]
    },
  ]
}