import json
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
        self.game_id = rest_api.create_game(self.name).content.decode("utf-8")
        print("Trainer created game with id: {}".format(self.game_id))
        self.tcp = TCP(self.game_id, self.name, self.on_recieved)
        self.start_game_with_agent(self.game_id)

    def wait_join_game(self, api):
        self.game_id = api.wait_to_adding_game()
        rest_api.join_game(self.game_id, self.name).content.decode("utf-8")
        print("Trainer join game with id: {}".format(self.game_id))
        self.tcp = TCP(self.game_id, self.name, self.on_recieved)
        self.start_game_with_agent(self.game_id)

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
        elif json_message["type"] == "RoomStateResponse":
            if len(json_message["players"]) == 2:
                rest_api.start_game(self.game_id)