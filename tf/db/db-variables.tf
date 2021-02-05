#AWS AZ
variable "aws_az" {
  description = "AWS AZ"
  #default = "eu-west-1c"
}
#VPC CIDR
variable "aws_vpc_cidr" {
  description = "CIDR for the VPC"
  #default = "10.1.0.0/16"
}
#Subnet CIDR
variable "aws_subnet_cidr" {
  description = "CIDR for the subnet"
  #default = "10.1.1.0/24"
}
#Define application name
variable "app_name" {
  description = "Application name"
  default = "inha-multicloud"
}
#Define application environment
variable "app_environment" {
  description = "Application environment"
  default = "demo"
}