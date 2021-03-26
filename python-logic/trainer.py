import requests as http
import rest_api
from tcp import TCP
from quoridor_env import QuoridorEnv
import random
import utils


class Trainer:

    def __init__(self, agent):
        self.agent = agent
        self.name = "Trainer"

    def start_training_session(self):
        game_id = rest_api.create_game()
        print("Trainer created game with id: {}".format(game_id))
        self.tcp = TCP(game_id, self.name, self.on_recieved)
        self.start_game_with_agent(game_id)

    def start_game_with_agent(self, game_id):
        env = QuoridorEnv(game_id, "Agent")
        self.agent.train_agent(env)

    def on_recieved(self, json_message):
        if json_message["type"] == "NewTurnEvent":
            if json_message["nextPlayerToPlay"] == self.name:
                rand_action = random.randint(0,4)
                act_json = utils.convert_action_to_server(rand_action)
                self.tcp.write(act_json)

        elif json_message["type"] == "GameOverEvent":
            pass