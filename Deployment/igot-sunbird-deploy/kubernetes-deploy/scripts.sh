find "templates/" -iname "*.env" -exec /bin/bash -c "ls {}; kubectl create configmap lex-core-test-config --from-env-file={} -n dev --dry-run -o=yaml | kubectl apply -f - " \;
