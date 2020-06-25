username=
password=
ip=
port=
curl -u "$username:$password" -H 'Content-Type: application/json' -XPOST -d @aliases.json "http://$ip:$port/_aliases"
