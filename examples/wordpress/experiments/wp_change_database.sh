#/bin/bash
sed "s;define('DB_HOST', .*;define('DB_HOST', '$1')\;;g" /var/www/wp-config.php
