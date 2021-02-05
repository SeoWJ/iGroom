#! /bin/bash
apt-get update
apt-get install -y apache2
systemctl start apache2
systemctl enable apache2
echo "<h1>Connection between CloudFlare - GCP</h1>" | sudo tee /var/www/html/index.html
apt install -y mysql-server
echo mysql is installed > /tmp/script.log
systemctl start mysql
echo mysql started running > /tmp/script.log
mysql -h ${db_address} -ugcp -p1234
echo connected to db server > /tmp/script.log

sudo apt install -y openjdk-8-jre-headless

export GCSFUSE_REPO=gcsfuse-`lsb_release -c -s`
echo "deb http://packages.cloud.google.com/apt $GCSFUSE_REPO main" | sudo tee /etc/apt/sources.list.d/gcsfuse.list
sudo curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
sudo apt-get update
sudo apt-get install gcsfuse
sudo mkdir /mount
sudo gcsfuse inha-multicloud-storage-a /mount
sudo cp /mount/cpu-send.jar /bin/cpu-send.jar
sudo java -jar /bin/cpu-send.jar &
