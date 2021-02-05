#! /bin/bash
apt-get update
apt-get install -y apache2
systemctl start apache2
systemctl enable apache2
echo "<h1>Connection between CloudFlare - Azure</h1>" | sudo tee /var/www/html/index.html

sudo apt install -y mysql-server
sudo echo mysql is installed > /tmp/script.log
sudo systemctl start mysql
sudo echo mysql started running > /tmp/script.log
sudo mysql -h ${db_address} -uazure -p1234
sudo echo connected to db server > /tmp/script.log

sudo apt install -y openjdk-8-jre-headless

sudo wget https://aka.ms/downloadazcopy-v10-linux
sudo tar -xvf downloadazcopy-v10-linux
sudo cp ./azcopy_linux_amd64_*/azcopy /usr/bin
sudo azcopy login --identity
sudo azcopy copy 'https://inhamulticloudstorage.blob.core.windows.net/azure-storage-container/cpu-send.jar' '/bin/cpu-send.jar' --recursive
sudo java -jar /bin/cpu-send.jar &

