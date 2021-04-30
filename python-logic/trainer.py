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
        self.overfitting_limitation_winning_games = 7 # how many winning games will be overfitting
        self.overfitting_limitation_winning_steps = 60 # how many steps will be considered good game

    def start_training_session(self, num_of_episodes):
        num_of_agent_good_winning = 0
        for episode_num in range(num_of_episodes):
            self.headline_print("start game of episode number {}".format(episode_num + 1))
            self.game_id = rest_api.create_game(self.name).content.decode("utf-8")
            print("Trainer created game with id: {}".format(self.game_id))
            self.tcp = TCP(self.game_id, self.name, self.on_recieved)
            isGoodWin = self.start_game_with_agent(self.game_id)
            self.headline_print("end of episode number {}".format(episode_num + 1))

            # Consider overfitting
            if isGoodWin:
                num_of_agent_good_winning += 1
            else:
                num_of_agent_good_winning = 0
            if num_of_agent_good_winning >= self.overfitting_limitation_winning_games:
                print("\n\nSTOPS TRAINING BECAUSE OF OVERFITTING!\n\n")
                break

    def start_game_with_agent(self, game_id):
        env = QuoridorEnv(game_id, "Agent")

        cur_state = env.reset()
        start_location = env.player_start_location
        done = False
        steps_num = 0
        num_of_agent_good_winning = 0

        while not done:
            steps_num += 1
            action = self.agent.act(cur_state, env)
            new_state, reward, done, _ = env.step(action)
            self.agent.remember(cur_state, action, reward, new_state, done)
            self.agent.replay(env)  # internally iterates default (prediction) model
            self.agent.target_train()  # iterates target model
            cur_state = new_state

        print("\n\nAGENT STARTS IN LOCATION ({},{}) - GAME FINISHED IN {} STEPS!\n\n".format(start_location[0], start_location[1], steps_num))
        return env.winner_name == env.player_name and steps_num <= self.overfitting_limitation_winning_steps

    def headline_print(self, text):
        print("====================================================")
        print(text)
        print("====================================================")

    def find_start_location(self, player_location_dim):
        location = (-1, -1)
        for i in range(0, player_location_dim.shape[0]):
            for j in range(0, player_location_dim.shape[1]):
                if player_location_dim[i, j] == 1:
                    location = (i, j)

        return location

    def on_recieved(self, json_message):
        raise NotImplementedError("This is abstract class you must implement the method!")


class WalkingTrainer(Trainer):

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


class RandomTrainer(Trainer):

    def on_recieved(self, json_message):
        if json_message["type"] == "NewTurnEvent":
            if json_message["nextPlayerToPlay"] == self.name:
                actions = utils.convert_moves_to_action_options(json_message)
                choices_len = len(actions)
                if choices_len == 0:
                    print("\nSOMEHOW THERE ARE NO CHOICES!\n")

                random_i = np.random.randint(0, choices_len)
                act_json = utils.convert_action_to_server(actions[random_i])
                self.tcp.write(act_json)

        elif json_message["type"] == "RoomStateResponse":
            if len(json_message["players"]) == 2:
                rest_api.start_game(self.game_id)
