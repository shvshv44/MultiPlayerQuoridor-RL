cd /home/cs600/project/git/MultiPlayerQuoridor-RL
git checkout master
git pull | grep -q -v 'Already up to date.' && changed=1

if [ -z $changed ];
then
	echo "repository is already updated, nothing to do."
else 
        mvn clean package --file ./quoridor-logic-server/pom.xml
        npm install ./client
        /usr/bin/pip3 install -r ./python-logic/requirements.txt
        chown -R cs600:users .
        systemctl restart quoridor-server.service
        systemctl restart quoridor-client.service
        systemctl restart quoridor-agent.service
fi


