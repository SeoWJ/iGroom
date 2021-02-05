#! /bin/bash
apt-get update
apt-get install -y apache2
systemctl start apache2
systemctl enable apache2
echo "<h1>Connection between CloudFlare - AWS</h1>" | sudo tee /var/www/html/index.html
apt install -y mysql-server
echo mysql is installed > /tmp/script.log
systemctl start mysql
echo mysql started running > /tmp/script.log
mysql -h ${db_address} -uaws -p1234
echo connected to db server > /tmp/script.log

sudo apt install -y openjdk-8-jre-headless
sudo apt install -y awscli

# aws IAM정책에 AdministratorAccess가 포함되어 있어야 한다.
sudo aws configure set default.region us-west-2
sudo aws configure set aws_access_key_id AKIAWGPIG3XXAQQ5AEXJ
sudo aws configure set aws_secret_access_key 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
sudo aws ecr get-login

sudo aws s3 cp s3://aws-cpu-send/cpu-send.jar /bin/cpu-send.jar

sudo java -jar /bin/cpu-send.jar &