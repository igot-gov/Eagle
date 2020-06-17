echo "Creating ES indices"
SNAPSHOT=$(date +%Y_%m_%d)
echo "$SNAPSHOT"
es_ip=
es_port=
es_username=elastic
es_password=
for file in index/*; do
	#echo "$(basename "$file")"
	#echo "$file"
	index_name=$(echo $file | sed -e "s/.json/_${SNAPSHOT}/g")
	#echo "The index name to be created is $index_name"
	echo "curl -u '$es_username:$es_password' -H 'Content-Type: application/json' -XPUT -d @$(basename "$file") http://$es_ip:$es_port/$index_name"
done
