import json
import rest_api
from tcp import TCP
from quoridor_env import QuoridorEnv
import random
import utils
import numpy as np


class Trainer:

    def __init__(self, agent):
        self.agent = agent
        self.name = "Trainer"

    def start_training_session(self, num_of_episodes):
        for episode_num in range(num_of_episodes):
            self.headline_print("start game of episode number {}".format(episode_num + 1))
            self.game_id = rest_api.create_game(self.name).content.decode("utf-8")
            print("Trainer created game with id: {}".format(self.game_id))
            self.tcp = TCP(self.game_id, self.name, self.on_recieved)
            self.start_game_with_agent(self.game_id)
            self.headline_print("end of episode number {}".format(episode_num + 1))

    def start_game_with_agent(self, game_id):
        env = QuoridorEnv(game_id, "Agent")

        cur_state = env.reset()
        done = False
        steps_num = 0

        while not done:
            steps_num += 1
            action = self.agent.act(cur_state, env)
            new_state, reward, done, _ = env.step(action)
            self.agent.remember(cur_state, action, reward, new_state, done)
            self.agent.replay(env)  # internally iterates default (prediction) model
            self.agent.target_train()  # iterates target model
            cur_state = new_state

        print("\n\nGAME FINISHED IN {} STEPS!\n\n".format(steps_num))

    def on_recieved(self, json_message):
        if json_message["type"] == "NewTurnEvent":
            if json_message["nextPlayerToPlay"] == self.name:
                myLoc = json_message["currentPosition"]
                actions = []
                for move in json_message["avialiableMoves"]:
                    if int(move["x"]) > int(myLoc["x"]):
                        actions.append(3)  # Move Right
                    elif int(move["x"]) < int(myLoc["x"]):
                        actions.append(2)  # Move Left
                    elif int(move["y"]) > int(myLoc["y"]):
                        actions.append(1)  # Move Down
                    elif int(move["y"]) < int(myLoc["y"]):
                        actions.append(0)  # Move Up

                actions_len = len(actions)
                random_i = np.random.randint(0, actions_len)
                act_json = utils.convert_action_to_server(actions[random_i])
                self.tcp.write(act_json)

        elif json_message["type"] == "GameOverEvent":
            pass
        elif json_message["type"] == "RoomStateResponse":
            if len(json_message["players"]) == 2:
                rest_api.start_game(self.game_id)

    def headline_print(self, text):
        print("====================================================")
        print(text)
        print("====================================================")