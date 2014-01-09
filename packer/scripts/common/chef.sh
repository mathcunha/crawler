#!/bin/bash -eux

apt-get -y install vim
apt-get -y install git
apt-get -y install ruby-dev
apt-get -y install maven

#https://tickets.opscode.com/browse/KNIFE-372
gem install em-winrm -- --with-cflags=\"-O2 -pipe -march=native -w\"

#https://github.com/pry/pry/issues/855
gem install --no-rdoc --no-ri pry -V

gem install knife-ec2

#no error report
apt-get update