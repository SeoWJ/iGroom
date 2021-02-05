output "Database-address" {
  value = "${module.db.Database-IP}"
}

output "Amazon-Web-Service-IP" {
  value = "${aws_globalaccelerator_accelerator.static-ip.ip_sets.0.ip_addresses.0}"
}

output "Microsoft-Azure-IP" {
    value = "${azurerm_public_ip.API.*.ip_address}"
}

output "Google-Cloud-Platform-IP" {
    value = "${google_compute_global_forwarding_rule.GCGFR.*.ip_address}"
}