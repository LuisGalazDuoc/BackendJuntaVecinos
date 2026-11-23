#!/bin/bash
# Permite al usuario de la aplicacion crear y administrar solo las bases bd_*
set -e
mysql -uroot -p"$MYSQL_ROOT_PASSWORD" <<SQL
GRANT ALL PRIVILEGES ON \`bd\_%\`.* TO '${MYSQL_USER}'@'%';
FLUSH PRIVILEGES;
SQL
