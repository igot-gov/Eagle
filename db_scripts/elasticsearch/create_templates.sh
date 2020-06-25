echo "Creating ES indices"
SNAPSHOT=$(date +%Y_%m_%d)
echo "$SNAPSHOT"
es_ip=
es_port=
es_username=elastic
es_password=
folder_name=templates
for file in $folder_name/*; do
	#echo "$(basename "$file")"
	#echo "$file"
	script_name=$(echo $(basename "$file") | sed -e "s/.json//g")
	#echo "The index name to be created is $index_name"
	echo "curl -u '$es_username:$es_password' -H 'Content-Type: application/json' -XPOST -d @$(basename "$file") http://$es_ip:$es_port/_scripts/$script_name"
done
