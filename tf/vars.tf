#AWS VPC Cidr
variable "vpc_cidr" {
  default = "10.0.0.0/16"
}

#AWS Public Cidrs
variable "public_cidrs" {
  type = "list"
  default = ["10.0.1.0/24", "10.0.2.0/24"]
}

#AWS Private Cidrs
variable "private_cidrs" {
  type = "list"
  default = ["10.0.3.0/24", "10.0.4.0/24"]
}

#GCP project name
variable "project" {
    default = "terraform-multicloud-294907"
}

#GCP region
variable "region" {
    default = "us-central1"
}

#GCP zone
variable "zone" {
    default = "us-central1-a"
}

#Azure vm admin password
variable "admin_password" {
    description = "Set windows virtual machine administrator password"
    default = "Capstone0303"
}

#Cloudflare email
variable "cloudflare_email" {
  description = "Cloudflare Email Address"
  default = "dkw_01209@naver.com"
}

#Cloudflare API key
variable "cloudflare_api_key" {
  description = "Cloudflare API Key"
  default = "YOUR KEY"
}

#Cloudflare zone id
variable "cloudflare_zone_id" {
  description = "Cloudflare Zone ID"
  default = "YOUR KEY"
}

variable "enable_AWS" {
  description = "If set 1, enable AWS"
}

variable "enable_Azure" {
  description = "If set 1, enable Azure"
}

variable "enable_GCP" {
  description = "If set 1, enable GCP"
}

variable "min_instance" {}

variable "max_instance" {}

variable "AWS_instance" {}

variable "Azure_instance" {}

variable "GCP_instance" {}