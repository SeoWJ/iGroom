#Create the VPC
resource "aws_vpc" "aws-vpc" {
  cidr_block = "${var.aws_vpc_cidr}"
  enable_dns_hostnames = true
  tags = {
    Name = "${var.app_name}-${var.app_environment}-vpc"
    Environment = "${var.app_environment}"
  }
}
#Define the subnet
resource "aws_subnet" "aws-subnet" {
  vpc_id = "${aws_vpc.aws-vpc.id}"
  cidr_block = "${var.aws_subnet_cidr}"
  availability_zone = "${var.aws_az}"
  tags = {
    Name = "${var.app_name}-${var.app_environment}-subnet"
    Environment = "${var.app_environment}"
  }
}
#Define the internet gateway
resource "aws_internet_gateway" "aws-gw" {
  vpc_id = "${aws_vpc.aws-vpc.id}"
  tags = {
    Name = "${var.app_name}-${var.app_environment}-igw"
    Environment = "${var.app_environment}"
  }
}
#Define the route table to the internet
resource "aws_route_table" "aws-route-table" {
  vpc_id = "${aws_vpc.aws-vpc.id}"
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = "${aws_internet_gateway.aws-gw.id}"
  }
  tags = {
    Name = "${var.app_name}-${var.app_environment}-route-table"
    Environment = "${var.app_environment}"
  }
}
#Assign the public route table to the subnet
resource "aws_route_table_association" "aws-route-table-association"{
  subnet_id = "${aws_subnet.aws-subnet.id}"
  route_table_id = "${aws_route_table.aws-route-table.id}"
}
#Define the security group for HTTP web server
resource "aws_security_group" "aws-web-sg" {
  name = "${var.app_name}-${var.app_environment}-web-sg"
  description = "Allow incoming HTTP connections"
  vpc_id = "${aws_vpc.aws-vpc.id}"
  ingress {
    from_port = 80
    to_port = 80
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = {
    Name = "${var.app_name}-${var.app_environment}-web-sg"
    Environment = "${var.app_environment}"
  }
}

resource "aws_security_group" "aws-ssh-sg" {
  name = "${var.app_name}-${var.app_environment}-ssh-sg"
  description = "Allow incoming ssh connections"
  vpc_id = "${aws_vpc.aws-vpc.id}"
  ingress {
    from_port = 22
    to_port = 22
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = {
    Name = "${var.app_name}-${var.app_environment}-ssh-sg"
    Environment = "${var.app_environment}"
  }
}

resource "aws_security_group" "aws-mysql-sg" {
  name = "${var.app_name}-${var.app_environment}-mysql-sg"
  description = "Allow incoming mysql connections"
  vpc_id = "${aws_vpc.aws-vpc.id}"
  ingress {
    from_port = 3306
    to_port = 3306
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = {
    Name = "${var.app_name}-${var.app_environment}-mysql-sg"
    Environment = "${var.app_environment}"
  }
}
#Get latest Ubuntu 18.04 AMI

data "aws_ami" "ubuntu-18_04" {
  most_recent = true
  owners = ["099720109477"]
  filter {
    name = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-bionic-18.04-amd64-server-*"]
  }
  filter {
    name = "virtualization-type"
    values = ["hvm"]
  }
}


#Create Elastic IP for web server
resource "aws_eip" "aws-db-eip" {
  vpc = true
  tags = {
    Name = "${var.app_name}-${var.app_environment}-elastic-ip"
    Environment = "${var.app_environment}"
  }
}
#Create EC2 Instances for Web Server
resource "aws_instance" "aws-db-server" {
  ami = "${data.aws_ami.ubuntu-18_04.id}"
  instance_type = "t2.micro"
  subnet_id = "${aws_subnet.aws-subnet.id}"
  vpc_security_group_ids = ["${aws_security_group.aws-web-sg.id}"]
  vpc_security_group_ids = ["${aws_security_group.aws-ssh-sg.id}"]
  vpc_security_group_ids = ["${aws_security_group.aws-mysql-sg.id}"]
  associate_public_ip_address = true
  source_dest_check = false
  key_name = "inha-db"
  #user_data = "${file("db-user-data.sh")}"
  user_data = <<-EOF
              #! /bin/bash
              sudo apt-get update
              sudo apt-get install -y mysql-server
              cd /etc/mysql/mysql.conf.d
              sed -i 's/127.0.0.1/0.0.0.0/g' mysqld.cnf
              sudo service mysql restart
              echo "CREATE USER 'aws'@'%' IDENTIFIED BY '1234';" | sudo mysql
              echo "CREATE USER 'azure'@'%' IDENTIFIED BY '1234';" | sudo mysql
              echo "CREATE USER 'gcp'@'%' IDENTIFIED BY '1234';" | sudo mysql
              echo "CREATE USER 'client'@'%' IDENTIFIED BY '1234';" | sudo mysql
              echo "GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;" | sudo mysql;
              echo "FLUSH PRIVILEGES;" | sudo mysql
              echo "set global general_log = on;" | sudo mysql
              echo "set global log_output = 'TABLE';" | sudo mysql
              echo "GRANT ALL privileges ON mysql.general_log TO client@'%' IDENTIFIED BY '1234';" | sudo mysql
              EOF
  tags = {
    Name = "${var.app_name}-${var.app_environment}-db-server"
    Environment = "${var.app_environment}"
  }
}
#Associate Elastic IP to db Server
resource "aws_eip_association" "aws-db-eip-association" {
  instance_id = "${aws_instance.aws-db-server.id}"
  allocation_id = "${aws_eip.aws-db-eip.id}"
}

output "Database-IP" {
  value = "${aws_eip.aws-db-eip.public_ip}"
}

output "db-server" {
  value = "${aws_instance.aws-db-server.instance_state}"
}