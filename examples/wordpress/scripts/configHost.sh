#!/bin/sh
sudo sed -i "s/http.*'/http:\/\/$1'/g" /var/www/wordpress/wp-config.php