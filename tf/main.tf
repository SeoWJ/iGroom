############  AWS Provisioning Part  ############
#################################################
#################################################
provider "aws" {
  region = "us-west-2"
  access_key = "YOUR KEY"
  secret_key = "YOUR KEY"
}
provider "aws" {
  region = "eu-west-1"
  alias   = "island"
  access_key = "YOUR KEY"
  secret_key = "YOUR KEY"
}

module "db" {
  providers = {
    aws = "aws.island"
  }
  source          = "./db"
  aws_az          = "eu-west-1c"
  aws_vpc_cidr    = "10.1.0.0/16"
  aws_subnet_cidr = "10.1.1.0/24"
}
######  AWS VPC Configuration  ######
data "aws_availability_zones" "available" {}

resource "aws_vpc" "main" {
  count = "${var.enable_AWS}"
  cidr_block           = "${var.vpc_cidr}"
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name = "my-new-test-terraform-vpc"
  }
}

# Creating Internet Gateway
resource "aws_internet_gateway" "gw" {
  count = "${var.enable_AWS}"
  vpc_id = "${aws_vpc.main.id}"

  tags = {
    Name = "my-test-igw"
  }
}

# Public Route Table
resource "aws_route_table" "public_route" {
  count = "${var.enable_AWS}"
  vpc_id = "${aws_vpc.main.id}"

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = "${aws_internet_gateway.gw.id}"
  }

  tags = {
    Name = "my-test-public-route"
  }
}

# Private Route Table
resource "aws_default_route_table" "private_route" {
  count = "${var.enable_AWS}"
  default_route_table_id = "${aws_vpc.main.default_route_table_id}"

  route {
    nat_gateway_id = "${aws_nat_gateway.my-test-nat-gateway.id}"
    cidr_block     = "0.0.0.0/0"
  }

  tags = {
    Name = "my-private-route-table"
  }
}

# Public Subnet
resource "aws_subnet" "public_subnet" {
  //count = 2
  count = "${var.enable_AWS == 1 ? 2 : 0}"
  cidr_block              = "${var.public_cidrs[count.index]}"
  vpc_id                  = "${aws_vpc.main.id}"
  map_public_ip_on_launch = true
  availability_zone       = "${data.aws_availability_zones.available.names[count.index]}"

  tags = {
    Name = "my-test-public-subnet.${count.index + 1}"
  }
}

# Private Subnet
resource "aws_subnet" "private_subnet" {
  //count=2
  count = "${var.enable_AWS == 1 ? 2 : 0}"
  cidr_block        = "${var.private_cidrs[count.index]}"
  vpc_id            = "${aws_vpc.main.id}"
  availability_zone = "${data.aws_availability_zones.available.names[count.index]}"

  tags = {
    Name = "my-test-private-subnet.${count.index + 1}"
  }
}

# Associate Public Subnet with Public Route Table
resource "aws_route_table_association" "public_subnet_assoc" {
  //count          = 2
  count = "${var.enable_AWS == 1 ? 2 : 0}"
  route_table_id = "${aws_route_table.public_route.id}"
  subnet_id      = "${aws_subnet.public_subnet.*.id[count.index]}"
  depends_on     = ["aws_route_table.public_route", "aws_subnet.public_subnet"]
}

# Associate Private Subnet with Private Route Table
resource "aws_route_table_association" "private_subnet_assoc" {
  //count          = 2
  count = "${var.enable_AWS == 1 ? 2 : 0}"
  route_table_id = "${aws_default_route_table.private_route.id}"
  subnet_id      = "${aws_subnet.private_subnet.*.id[count.index]}"
  depends_on     = ["aws_default_route_table.private_route", "aws_subnet.private_subnet"]
}

# Security Group Creation
resource "aws_security_group" "test_sg" {
  count = "${var.enable_AWS}"
  name   = "my-test-sg"
  vpc_id = "${aws_vpc.main.id}"
}

# Ingress Security Port 22
resource "aws_security_group_rule" "ssh_inbound_access" {
  count = "${var.enable_AWS}"
  from_port         = 22
  protocol          = "tcp"
  security_group_id = "${aws_security_group.test_sg.id}"
  to_port           = 22
  type              = "ingress"
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "http_inbound_access" {
  count = "${var.enable_AWS}"
  from_port         = 80
  protocol          = "tcp"
  security_group_id = "${aws_security_group.test_sg.id}"
  to_port           = 80
  type              = "ingress"
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "mysql_inbound_access" {
  count = "${var.enable_AWS}"
  from_port         = 3306
  protocol          = "tcp"
  security_group_id = "${aws_security_group.test_sg.id}"
  to_port           = 3306
  type              = "ingress"
  cidr_blocks       = ["0.0.0.0/0"]
}

# All OutBound Access
resource "aws_security_group_rule" "all_outbound_access" {
  count = "${var.enable_AWS}"
  from_port         = 0
  protocol          = "-1"
  security_group_id = "${aws_security_group.test_sg.id}"
  to_port           = 0
  type              = "egress"
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_eip" "my-test-eip" {
  count = "${var.enable_AWS}"
  vpc = true
}

resource "aws_nat_gateway" "my-test-nat-gateway" {
  count = "${var.enable_AWS}"
  allocation_id = "${aws_eip.my-test-eip.id}"
  subnet_id     = "${aws_subnet.public_subnet.0.id}"
}

# Adding Route for Transit Gateway
resource "aws_route" "my-tgw-route" {
  count = "${var.enable_AWS}"
  route_table_id         = "${aws_route_table.public_route.id}"
  destination_cidr_block = "172.16.0.0/16"
  transit_gateway_id     = "${aws_ec2_transit_gateway.my-test-tgw.id}"
}

######  AWS ALB Configuration  ######
resource "aws_lb_target_group" "my-target-group" {
  count = "${var.enable_AWS}"
  health_check {
    interval            = 10
    path                = "/"
    protocol            = "HTTP"
    timeout             = 5
    healthy_threshold   = 5
    unhealthy_threshold = 2
  }

  name        = "my-test-tg"
  port        = 80
  protocol    = "HTTP"
  target_type = "instance"
  vpc_id      = "${aws_vpc.main.id}"
}

resource "aws_lb" "my-aws-alb" {
  count = "${var.enable_AWS}"
  name     = "my-test-alb"
  internal = false

  security_groups = [
    "${aws_security_group.my-alb-sg.id}",
  ]

  subnets = [
    "${element(aws_subnet.public_subnet.*.id, 1 )}",
    "${element(aws_subnet.public_subnet.*.id, 2 )}",
  ]

  tags = {
    Name = "my-test-alb"
  }

  ip_address_type    = "ipv4"
  load_balancer_type = "application"
}

resource "aws_lb_listener" "my-test-alb-listner" {
  count = "${var.enable_AWS}"
  load_balancer_arn = "${aws_lb.my-aws-alb.arn}"
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = "${aws_lb_target_group.my-target-group.arn}"
  }
}

resource "aws_security_group" "my-alb-sg" {
  count = "${var.enable_AWS}"
  name   = "my-alb-sg"
  vpc_id = "${aws_vpc.main.id}"
}

resource "aws_security_group_rule" "inbound_alb_ssh" {
  count = "${var.enable_AWS}"
  from_port         = 22
  protocol          = "tcp"
  security_group_id = "${aws_security_group.my-alb-sg.id}"
  to_port           = 22
  type              = "ingress"
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "inbound_alb_http" {
  count = "${var.enable_AWS}"
  from_port         = 80
  protocol          = "tcp"
  security_group_id = "${aws_security_group.my-alb-sg.id}"
  to_port           = 80
  type              = "ingress"
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "inbound_alb_mysql" {
  count = "${var.enable_AWS}"
  from_port         = 3306
  protocol          = "tcp"
  security_group_id = "${aws_security_group.my-alb-sg.id}"
  to_port           = 3306
  type              = "ingress"
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "outbound_alb_all" {
  count = "${var.enable_AWS}"
  from_port         = 0
  protocol          = "-1"
  security_group_id = "${aws_security_group.my-alb-sg.id}"
  to_port           = 0
  type              = "egress"
  cidr_blocks       = ["0.0.0.0/0"]
}

######  AWS Autoscaling Configuration  ######
resource "aws_launch_configuration" "my-test-launch-config" {
  count = "${var.enable_AWS}"
  image_id        = "${data.aws_ami.ubuntu-18_04.id}"
  instance_type   = "${var.AWS_instance}"
  security_groups = ["${aws_security_group.my-asg-sg.id}"]
  key_name = "inha-multicloud"
  user_data = "${data.template_file.aws-user-data.rendered}"

  lifecycle {
    create_before_destroy = true
  }
  depends_on = ["module.db"]
  depends_on = ["aws_s3_bucket_object.aws-cpu-send"]
}
data "template_file" "aws-user-data" {
    template = "${file("aws-user-data.sh")}"

    vars {
    db_address  = "${module.db.Database-IP}"
    }
}

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

resource "aws_autoscaling_group" "example" {
  count = "${var.enable_AWS}"
  launch_configuration = "${aws_launch_configuration.my-test-launch-config.name}"
  vpc_zone_identifier  = ["${element(aws_subnet.public_subnet.*.id, 1 )}","${element(aws_subnet.public_subnet.*.id, 2 )}"]
  target_group_arns    = ["${aws_lb_target_group.my-target-group.arn}"]
  health_check_type    = "ELB"

  min_size = "${var.min_instance}"
  max_size = "${var.max_instance}"

  tag {
    key                 = "Name"
    value               = "my-test-asg"
    propagate_at_launch = true
  }
}

resource "aws_security_group" "my-asg-sg" {
  count = "${var.enable_AWS}"
  name   = "my-asg-sg"
  vpc_id = "${aws_vpc.main.id}"
}

resource "aws_security_group_rule" "inbound_8080" {
  count = "${var.enable_AWS}"
    from_port = 8080
    to_port = 8080
    protocol = "tcp"
    security_group_id = "${aws_security_group.my-asg-sg.id}"
    type = "ingress"
    cidr_blocks = [ "0.0.0.0/0" ]
}

resource "aws_security_group_rule" "inbound_8082" {
  count = "${var.enable_AWS}"
    from_port = 8082
    to_port = 8082
    protocol = "tcp"
    security_group_id = "${aws_security_group.my-asg-sg.id}"
    type = "ingress"
    cidr_blocks = [ "0.0.0.0/0" ]
}

resource "aws_security_group_rule" "inbound_asg_ssh" {
  count = "${var.enable_AWS}"
  from_port         = 22
  protocol          = "tcp"
  security_group_id = "${aws_security_group.my-asg-sg.id}"
  to_port           = 22
  type              = "ingress"
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "inbound_asg_http" {
  count = "${var.enable_AWS}"
  from_port         = 80
  protocol          = "tcp"
  security_group_id = "${aws_security_group.my-asg-sg.id}"
  to_port           = 80
  type              = "ingress"
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "inbound_asg_mysql" {
  count = "${var.enable_AWS}"
  from_port         = 3306
  protocol          = "tcp"
  security_group_id = "${aws_security_group.my-asg-sg.id}"
  to_port           = 3306
  type              = "ingress"
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "outbound_asg_all" {
  count = "${var.enable_AWS}"
  from_port         = 0
  protocol          = "-1"
  security_group_id = "${aws_security_group.my-asg-sg.id}"
  to_port           = 0
  type              = "egress"
  cidr_blocks       = ["0.0.0.0/0"]
}

############  AWS Creating Public IP  ############
resource "aws_ec2_transit_gateway" "my-test-tgw" {
  count = "${var.enable_AWS}"
  description                     = "my-test-transit-gateway"
  amazon_side_asn                 = 64512
  auto_accept_shared_attachments  = "disable"
  default_route_table_association = "enable"
  default_route_table_propagation = "enable"
  dns_support                     = "enable"
  vpn_ecmp_support                = "enable"

  tags = {
    Name = "my-test-transit-gateway"
  }
}

resource "aws_ec2_transit_gateway_vpc_attachment" "my-test-transit-gateway-attachment" {
  count = "${var.enable_AWS}"
  transit_gateway_id = "${aws_ec2_transit_gateway.my-test-tgw.id}"
  vpc_id             = "${aws_vpc.main.id}"
  dns_support        = "enable"

  subnet_ids = [
    "${element(aws_subnet.public_subnet.*.id, 1 )}",
    "${element(aws_subnet.public_subnet.*.id, 2 )}",
  ]

  tags =  {
    Name = "my-test-tgw-vpc-attachment"
  }
}

resource "aws_globalaccelerator_accelerator" "static-ip" {
  count = "${var.enable_AWS}"
  name            = "static-ip"
  ip_address_type = "IPV4"
  enabled         = true
}

resource "aws_globalaccelerator_listener" "acc-listener" {
  count = "${var.enable_AWS}"
  accelerator_arn = "${aws_globalaccelerator_accelerator.static-ip.id}"
  client_affinity = "SOURCE_IP"
  protocol        = "TCP"

  port_range {
    from_port = 80
    to_port   = 80
  }
}

resource "aws_globalaccelerator_listener" "mysql-listener" {
  count = "${var.enable_AWS}"
  accelerator_arn = "${aws_globalaccelerator_accelerator.static-ip.id}"
  client_affinity = "SOURCE_IP"
  protocol        = "TCP"

  port_range {
    from_port = 3306
    to_port   = 3306
  }
}

resource "aws_globalaccelerator_endpoint_group" "acc-epg" {
  count = "${var.enable_AWS}"
  listener_arn = "${aws_globalaccelerator_listener.acc-listener.id}"
  endpoint_group_region = "us-west-2"

  endpoint_configuration {
    endpoint_id = "${aws_lb.my-aws-alb.arn}"
    weight      = 100
  }
}

resource "aws_s3_bucket_object" "aws-cpu-send" {
  count = "${var.enable_AWS}"
    bucket = "aws-cpu-send"
    key = "cpu-send.jar"
    source = "cpu-send.jar"
    etag = "${md5(file("cpu-send.jar"))}"
}

############  GCP Provisioning Part  ############
#################################################
#################################################
provider "google" {
    project = "${var.project}"
    credentials = "${file("credentials.json")}"
    region = "${var.region}"      // US region의 f1-micro 인스턴스는 always free tier
    zone = "${var.zone}"
}

resource "google_compute_firewall" "GCF" {
  count = "${var.enable_GCP}"
    name = "google-compute-firewall"
    network = "default"
    allow {
        protocol = "all"
    }
}

data "template_file" "metadata_startup_script" {
    template = "${file("gcp-user-data.sh")}"

    vars {
    db_address  = "${module.db.Database-IP}"
    }
    depends_on = ["module.db"]
}

resource "google_compute_instance_template" "GCIT" {
  count = "${var.enable_GCP}"
    name = "google-compute-instance-template"
    machine_type = "${var.GCP_instance}"
    can_ip_forward = false

    scheduling {
        automatic_restart = true
        on_host_maintenance = "MIGRATE"
    }

    disk {
        source_image = "ubuntu-os-cloud/ubuntu-1804-lts"
        boot = true
        auto_delete = true
    }

    network_interface {
        network = "default"
        subnetwork = "default"
        access_config {}    // 이게 있어야 외부ip생성
    }

    service_account {
        scopes = ["storage-ro"] // google cloud storage 접근권한 부여
    }

    lifecycle {
        create_before_destroy = true
    }

    metadata_startup_script = "${data.template_file.metadata_startup_script.rendered}"
    depends_on = ["aws_globalaccelerator_endpoint_group.acc-epg"]
    depends_on = ["module.db"]
}

resource "google_compute_instance_group_manager" "GCIGM" {
  count = "${var.enable_GCP}"
    name = "google-compute-instance-group-manager"
    project = "${var.project}"
    base_instance_name = "google-vm"
    zone = "${var.zone}"

    version {
        instance_template = "${google_compute_instance_template.GCIT.self_link}"
    }

    named_port {
        name = "http"
        port = 80
    }
}

resource "google_compute_health_check" "GCHC" {
  count = "${var.enable_GCP}"
    name = "google-compute-health-check"
    timeout_sec = 1
    check_interval_sec = 1

    http_health_check {
        port = 80
    }
}

resource "google_compute_backend_service" "GCBS" {
  count = "${var.enable_GCP}"
    name = "google-compute-backend-service"
    project = "${var.project}"
    port_name = "http"
    protocol = "HTTP"
    load_balancing_scheme = "EXTERNAL"
    health_checks = ["${google_compute_health_check.GCHC.self_link}"]

    backend {
        group = "${google_compute_instance_group_manager.GCIGM.instance_group}"
        balancing_mode = "RATE"
        max_rate_per_instance = 100
    }
}

resource "google_compute_url_map" "GCUM" {
  count = "${var.enable_GCP}"
    name = "google-compute-url-map"
    project = "${var.project}"
    default_service = "${google_compute_backend_service.GCBS.self_link}"
}

resource "google_compute_target_http_proxy" "GCTHP" {
  count = "${var.enable_GCP}"
    name = "google-compute-target-http-proxy"
    project = "${var.project}"
    url_map = "${google_compute_url_map.GCUM.self_link}"
}

resource "google_compute_global_forwarding_rule" "GCGFR" {
  count = "${var.enable_GCP}"
    name = "google-compute-global-forwarding-rule"
    project = "${var.project}"
    target = "${google_compute_target_http_proxy.GCTHP.self_link}"
    port_range = "80"
}

resource "google_compute_autoscaler" "GCA" {
  count = "${var.enable_GCP}"
    name = "google-compute-autoscaler"
    project = "${var.project}"
    zone = "${var.zone}"
    target = "${google_compute_instance_group_manager.GCIGM.self_link}"

    autoscaling_policy {
        max_replicas = "${var.max_instance}"
        min_replicas = "${var.min_instance}"
        cooldown_period = 600

        cpu_utilization {
            target = 0.7
        }
    }
}

#Google Cloud Storage
resource "google_storage_bucket" "GSB" {
  count = "${var.enable_GCP}"
    name = "inha-multicloud-storage-a"
    location = "US"
}

resource "google_storage_bucket_object" "cpu-send" {
  count = "${var.enable_GCP}"
    name = "cpu-send.jar"
    source = "cpu-send.jar"
    bucket = "${google_storage_bucket.GSB.name}"
}

############  Azure Provisioning Part  ############
###################################################
###################################################
provider "azurerm" {
    version = "=2.3.0" 
    subscription_id = "YOUR KEY"
    tenant_id = "YOUR KEY"
    client_id = "YOUR KEY"
    client_secret = "YOUR KEY"
    features {}
}

resource "azurerm_resource_group" "ARG" {
  count = "${var.enable_Azure}"
    name = "inha-multicloud"
    location = "eastus"

    tags {
        Owner = "Seowj"
    }
}

resource "azurerm_availability_set" "AAS" {
  count = "${var.enable_Azure}"
    name = "Azure_availiability_set"
    location = "${azurerm_resource_group.ARG.location}"
    resource_group_name = "${azurerm_resource_group.ARG.name}"
}

resource "azurerm_virtual_network" "AVN" {
  count = "${var.enable_Azure}"
    name = "Azure_virtual_network"
    address_space = ["10.0.0.0/16"]
    location = "${azurerm_resource_group.ARG.location}"
    resource_group_name = "${azurerm_resource_group.ARG.name}"
}

resource "azurerm_subnet" "AS" {
  count = "${var.enable_Azure}"
    name = "Azure_subnet_internal"
    resource_group_name = "${azurerm_resource_group.ARG.name}"
    virtual_network_name = "${azurerm_virtual_network.AVN.name}"
    address_prefix = "10.0.2.0/24"
}

resource "azurerm_public_ip" "API" {
  count = "${var.enable_Azure}"
    name = "Azure_public_ip"
    location = "${azurerm_resource_group.ARG.location}"
    resource_group_name = "${azurerm_resource_group.ARG.name}"
    allocation_method = "Static"
}

resource "azurerm_network_security_group" "ANSG" {
  count = "${var.enable_Azure}"
    name = "Azure_network_security_group"
    location = "${azurerm_resource_group.ARG.location}"
    resource_group_name = "${azurerm_resource_group.ARG.name}"

    security_rule {
        name                       = "AllowHTTP"
        priority                   = 100
        direction                  = "Inbound"
        access                     = "Allow"
        protocol                   = "Tcp"
        source_port_range          = "*"
        destination_port_range     = "80"
        source_address_prefix      = "*"
        destination_address_prefix = "*"
    }

    security_rule {
        name                       = "AllowSSH-in"
        priority                   = 101
        direction                  = "Inbound"
        access                     = "Allow"
        protocol                   = "Tcp"
        source_port_range          = "*"
        destination_port_range     = "22"
        source_address_prefix      = "*"
        destination_address_prefix = "*"
    }

    security_rule {
        name                       = "AllowSSH-out"
        priority                   = 102
        direction                  = "Outbound"
        access                     = "Allow"
        protocol                   = "Tcp"
        source_port_range          = "*"
        destination_port_range     = "22"
        source_address_prefix      = "*"
        destination_address_prefix = "*"
    }

    security_rule {
        name                       = "AllowMysql-in"
        priority                   = 105
        direction                  = "Inbound"
        access                     = "Allow"
        protocol                   = "Tcp"
        source_port_range          = "*"
        destination_port_range     = "3306"
        source_address_prefix      = "*"
        destination_address_prefix = "*"
    }

    security_rule {
        name                       = "AllowMysql-out"
        priority                   = 106
        direction                  = "Outbound"
        access                     = "Allow"
        protocol                   = "Tcp"
        source_port_range          = "*"
        destination_port_range     = "3306"
        source_address_prefix      = "*"
        destination_address_prefix = "*"
    }

    security_rule {
        name                       = "AllowCPU-send-in"
        priority                   = 103
        direction                  = "Inbound"
        access                     = "Allow"
        protocol                   = "Tcp"
        source_port_range          = "*"
        destination_port_range     = "8082"
        source_address_prefix      = "*"
        destination_address_prefix = "*"
    }

    security_rule {
        name                       = "AllowCPU-send-out"
        priority                   = 104
        direction                  = "Outbound"
        access                     = "Allow"
        protocol                   = "Tcp"
        source_port_range          = "*"
        destination_port_range     = "8082"
        source_address_prefix      = "*"
        destination_address_prefix = "*"
    }
}

resource "azurerm_subnet_network_security_group_association" "ASNSGA" {
  count = "${var.enable_Azure}"
    subnet_id = "${azurerm_subnet.AS.id}"
    network_security_group_id = "${azurerm_network_security_group.ANSG.id}"
}

resource "azurerm_network_interface" "ANI" {
  count = "${var.enable_Azure}"
    name = "Azure_network_interface_nic"
    location = "${azurerm_resource_group.ARG.location}"
    resource_group_name = "${azurerm_resource_group.ARG.name}"

    ip_configuration {
        name = "internal"
        subnet_id = "${azurerm_subnet.AS.id}"
        private_ip_address_allocation = "Dynamic"
    }
}

resource "azurerm_lb" "AL" {
  count = "${var.enable_Azure}"
    name = "Azure_Load_Balancer"
    location = "${azurerm_resource_group.ARG.location}"
    resource_group_name = "${azurerm_resource_group.ARG.name}"

    frontend_ip_configuration {
        name = "Public_IP_Address"
        public_ip_address_id = "${azurerm_public_ip.API.id}"
    }
}

resource "azurerm_lb_backend_address_pool" "ALBAP" {
  count = "${var.enable_Azure}"
    resource_group_name = "${azurerm_resource_group.ARG.name}"
    loadbalancer_id = "${azurerm_lb.AL.id}"
    name = "Backend_Address_Pool"
}

resource "azurerm_network_interface_backend_address_pool_association" "ANIBAPA" {
  count = "${var.enable_Azure}"
    ip_configuration_name = "internal"  // azurerm_network_interface 리소스의 ip_configuration의 name과 일치해야 함.
    network_interface_id = "${azurerm_network_interface.ANI.id}"
    backend_address_pool_id = "${azurerm_lb_backend_address_pool.ALBAP.id}"
}

resource "azurerm_lb_probe" "ALP" {
  count = "${var.enable_Azure}"
    resource_group_name = "${azurerm_resource_group.ARG.name}"
    loadbalancer_id = "${azurerm_lb.AL.id}"
    name = "Azure_Load_Balancer_Probe"
    port = 80
}

resource "azurerm_lb_rule" "ALR" {
  count = "${var.enable_Azure}"
    resource_group_name = "${azurerm_resource_group.ARG.name}"
    loadbalancer_id = "${azurerm_lb.AL.id}"
    name = "HTTP"
    protocol = "TCP"
    frontend_port = 80
    backend_port = 80
    backend_address_pool_id = "${azurerm_lb_backend_address_pool.ALBAP.id}"
    frontend_ip_configuration_name = "Public_IP_Address"
    probe_id = "${azurerm_lb_probe.ALP.id}"
}

resource "azurerm_virtual_machine_scale_set" "AVMSS" {
  count = "${var.enable_Azure}"
    name = "Azure_VM_Scale_Set"
    location = "${azurerm_resource_group.ARG.location}"
    resource_group_name = "${azurerm_resource_group.ARG.name}"
    upgrade_policy_mode = "Manual"

    sku {
        name = "${var.Azure_instance}"
        tier = "Standard"
        capacity = 2
    }

    identity {
        type = "SystemAssigned"
    }   // azcopy 접속을 위해 identity enable

    storage_profile_image_reference {
        publisher = "Canonical"
        offer = "UbuntuServer"
        sku = "18.04-LTS"
        version = "latest"
    }

    storage_profile_os_disk {
        name = ""
        caching = "ReadWrite"
        create_option = "FromImage"
        managed_disk_type = "Standard_LRS"
    }

    storage_profile_data_disk {
        lun = 0
        caching = "ReadWrite"
        create_option = "Empty"
        disk_size_gb = 10
    }

    os_profile {
        computer_name_prefix = "linuxVM"
        custom_data = "${data.template_file.azure-user-data.rendered}"
        admin_username = "VMadmin"
        admin_password = "${var.admin_password}"
    }

    os_profile_linux_config {
        disable_password_authentication = false      // 비밀번호 인증 해제

        ssh_keys {
            path = "/home/VMadmin/.ssh/authorized_keys"
            key_data = "${file("id_rsa.pub")}"
        }
    }

    network_profile {
        name = "Azure_Network_profile"
        primary = true
        
        ip_configuration {
            name = "IPConfiguration"
            subnet_id = "${azurerm_subnet.AS.id}"
            load_balancer_backend_address_pool_ids = ["${azurerm_lb_backend_address_pool.ALBAP.id}"]
            primary = true

            public_ip_address_configuration {
                name = "Azure_Public_Ip_Configuration"
                idle_timeout = 30
                domain_name_label = "db-test5"
            }
        }
    }
    depends_on = ["aws_globalaccelerator_endpoint_group.acc-epg"]
    depends_on = ["module.db"]
}

data "template_file" "azure-user-data" {
    template = "${file("azure-user-data.sh")}"

    vars {
    db_address  = "${module.db.Database-IP}"
    }
    depends_on = ["module.db"]
}

resource "azurerm_monitor_autoscale_setting" "AMAS" {
  count = "${var.enable_Azure}"
    name = "Azure_Monitor_Autoscale_Setting"
    resource_group_name = "${azurerm_resource_group.ARG.name}"
    location = "${azurerm_resource_group.ARG.location}"
    target_resource_id = "${azurerm_virtual_machine_scale_set.AVMSS.id}"

    profile {
        name = "Azure_Scale_Rule"

        capacity {
            default = "${var.min_instance}"
            minimum = "${var.min_instance}"
            maximum = "${var.max_instance}"     // 프리 티어 맥시멈 제한 4
        }

        rule {
            metric_trigger {
                metric_name = "Percentage CPU"
                metric_resource_id = "${azurerm_virtual_machine_scale_set.AVMSS.id}"
                time_grain = "PT1M"
                statistic = "Average"
                time_window = "PT5M"
                time_aggregation = "Average"
                operator = "GreaterThan"
                threshold = 75
            }
            
            scale_action {
                direction = "Increase"
                type = "ChangeCount"
                value = "1"
                cooldown = "PT1M"
            }
        }

        rule {
            metric_trigger {
                metric_name = "Percentage CPU"
                metric_resource_id = "${azurerm_virtual_machine_scale_set.AVMSS.id}"
                time_grain         = "PT1M"
                statistic          = "Average"
                time_window        = "PT5M"
                time_aggregation   = "Average"
                operator           = "LessThan"
                threshold          = 25
            }

            scale_action {
                direction = "Decrease"
                type      = "ChangeCount"
                value     = "1"
                cooldown  = "PT1M"
            }       
        }
    }
}

resource "azurerm_storage_account" "ASA" {
    count = "${var.enable_Azure}"
    name = "inhamulticloudstorage"       // storage이름은 본인계정에서만 unique해야하는것 만이 아니라, 전체 다른 사용자 storage 이름 중에서도 unique해야 한다.
    resource_group_name = "${azurerm_resource_group.ARG.name}"
    location = "${azurerm_resource_group.ARG.location}"
    account_replication_type = "LRS"
    account_tier = "Standard"
}

resource "azurerm_storage_container" "ASC" {
    count = "${var.enable_Azure}"
    name = "azure-storage-container"
    storage_account_name = "${azurerm_storage_account.ASA.name}"
    container_access_type = "private"
}

resource "azurerm_storage_blob" "cpu-send" {
    count = "${var.enable_Azure}"
    name = "cpu-send.jar"
    storage_account_name = "${azurerm_storage_account.ASA.name}"
    storage_container_name = "${azurerm_storage_container.ASC.name}"
    type = "Block"
    source = "cpu-send.jar"
}

resource "azurerm_role_assignment" "ARA" {
    count = "${var.enable_Azure}"
    scope = "${azurerm_storage_account.ASA.id}"
    role_definition_name = "Storage Blob Data Reader"
    principal_id = "${lookup(azurerm_virtual_machine_scale_set.AVMSS.identity[0], "principal_id")}"
}

############  CloudFlare Provisioning Part  ############
########################################################
########################################################

#Define Cloudflare provider
provider "cloudflare" {
  version = "~> 2.0"
  email = "${var.cloudflare_email}"
  api_key = "${var.cloudflare_api_key}"
}

#Create www record for Amazon Web Services

resource "cloudflare_record" "aws-www" {
  zone_id = "${var.cloudflare_zone_id}"
  name = "www"
  value = "${aws_globalaccelerator_accelerator.static-ip.ip_sets.0.ip_addresses.0}"
  type = "A"
  proxied = true 
  depends_on = ["aws_globalaccelerator_accelerator.static-ip"]
}


#Create www record for Azure
resource "cloudflare_record" "azure-www" {
  count = "${var.enable_Azure}"
  zone_id = "${var.cloudflare_zone_id}"
  name = "www"
  value = "${azurerm_public_ip.API.ip_address}"
  type = "A"
  proxied = true
  depends_on = ["azurerm_public_ip.API"]
}

#Create www record for GCP
resource "cloudflare_record" "gcp-www" {
  count = "${var.enable_GCP}"
  zone_id = "${var.cloudflare_zone_id}"
  name = "www"
  value = "${google_compute_global_forwarding_rule.GCGFR.ip_address}"
  type = "A"
  proxied = true
  depends_on = ["google_compute_global_forwarding_rule.GCGFR"]
}

#Create root record for Amazon Web Services

resource "cloudflare_record" "aws-root" {
  zone_id = "${var.cloudflare_zone_id}"
  name = "@"
  value = "${aws_globalaccelerator_accelerator.static-ip.ip_sets.0.ip_addresses.0}"
  type = "A"
  proxied = true 
  depends_on = ["aws_globalaccelerator_accelerator.static-ip"]
}


#Create root record for Azure
resource "cloudflare_record" "azure-root" {
  count = "${var.enable_Azure}"
  zone_id = "${var.cloudflare_zone_id}"
  name = "@"
  value = "${azurerm_public_ip.API.ip_address}"
  type = "A"
  proxied = true
  depends_on = ["azurerm_public_ip.API"]
}

#Create root record for GCP
resource "cloudflare_record" "gcp-root" {
  count = "${var.enable_GCP}"
  zone_id = "${var.cloudflare_zone_id}"
  name = "@"
  value = "${google_compute_global_forwarding_rule.GCGFR.ip_address}"
  type = "A"
  proxied = true
  depends_on = ["google_compute_global_forwarding_rule.GCGFR"]
}
