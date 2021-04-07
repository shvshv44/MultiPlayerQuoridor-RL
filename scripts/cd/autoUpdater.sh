cd /home/cs600/project/git/MultiPlayerQuoridor-RL
git checkout master
git pull | grep -q -v 'Already up to date.' && changed=1

if [ -z $changed ];
then
	echo "repository is already updated, nothing to do."
else 
	echo "repository updated, repackaging and restarting services"
	mvn clean package --file ./quoridor-logic-server/pom.xml
	npm install ./client 
	systemctl restart quoridor-server.service
	systemctl restart quoridor-client.service
fi


