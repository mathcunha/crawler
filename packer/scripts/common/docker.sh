#!/bin/bash -eux

apt-get update

apt-get -y install linux-image-extra-`uname -r`

wget -qO- https://get.docker.io/gpg | apt-key add -
echo deb http://get.docker.io/ubuntu docker main > /etc/apt/sources.list.d/docker.list

apt-get update

apt-get -y install lxc-docker	
docker pull ubuntu

apt-get update