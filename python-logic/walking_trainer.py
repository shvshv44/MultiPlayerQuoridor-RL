import json
import rest_api
from tcp import TCP
from walking_quoridor_env import QuoridorEnv
import random
import utils
import numpy as np



class Trainer:
    def __init__(self, agent):
        self.agent = agent
        self.name = "WalkingTrainer"
        self.history = []

    def start_training_session(self, num_of_episodes):
        for episode_num in range(num_of_episodes):
            self.headline_print("Start game of episode number {}".format(episode_num + 1))
            self.game_id = rest_api.create_game(self.name).content.decode("utf-8")
            print("Trainer created game with id: {}".format(self.game_id))
            self.tcp = TCP(self.game_id, self.name, self.on_recieved)
            self.start_game_with_agent(self.game_id)
            self.headline_print("End of episode number {}".format(episode_num + 1))
        return self.history

    def start_game_with_agent(self, game_id):
        env = QuoridorEnv(game_id, "Agent")

        cur_state = env.reset()
        start_location = env.player_start_location
        done = False
        steps_num = 0

        while not done:
            steps_num += 1
            action = self.agent.act(cur_state, env)
            new_state, reward, done, _ = env.step(action)
            self.agent.remember(cur_state, action, reward, new_state, done)
            self.agent.replay(env, self.history)  # internally iterates default (prediction) model
            self.agent.target_train()  # iterates target model
            cur_state = new_state

        print("\n\nAGENT STARTS IN LOCATION ({},{}) - GAME FINISHED IN {} STEPS!\n\n".format(start_location[0], start_location[1], steps_num))

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


class RandomWalkingTrainer(Trainer):

    def on_recieved(self, json_message):
        if json_message["type"] == "NewTurnEvent":
            if json_message["nextPlayerToPlay"] == self.name:
                actions = utils.convert_moves_to_action_options(json_message)
                choices_len = len(actions)
                if choices_len == 0:
                    print("\nSOMEHOW THERE ARE NO CHOICES!\n")

                # always do first available action
                act_json = utils.convert_action_to_server(actions[0])
                print("Walking trainer action: " + act_json)
                self.tcp.write(act_json)

        elif json_message["type"] == "RoomStateResponse":
            if len(json_message["players"]) == 2:
                rest_api.start_generated_game(self.game_id)
