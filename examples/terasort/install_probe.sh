#!/bin/bash
sudo apt-get install -y git
wget https://storage.googleapis.com/golang/go1.5.1.linux-amd64.tar.gz
sudo tar -C /usr/local -xzf go1.5.1.linux-amd64.tar.gz
echo 'export PATH=$PATH:/usr/local/go/bin' >> $HOME/.profile
echo 'export GOPATH=$HOME/go' >> $HOME/.profile
source $HOME/.profile
go get github.com/mathcunha/go-probe
go get github.com/mathcunha/amon
exit 0