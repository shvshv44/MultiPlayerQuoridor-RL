import json
import rest_api
from tcp import TCP
from better_quoridor_env import QuoridorEnv
import random
import utils
import numpy as np



class Manager:
    def __init__(self, agent):
        self.agent = agent

    def start_game_with_agent(self, game_id, is_start):
        env = QuoridorEnv(game_id, self.agent.name, is_start)

        cur_state = env.reset()
        start_location = env.player_start_location
        done = False
        steps_num = 0
        location_label = utils.define_location_label(start_location)

        while not done:
            steps_num += 1
            action = self.agent.act(cur_state, env, location_label)
            new_state, reward, done, _ = env.step(action)
            self.agent.remember(cur_state, action, reward, new_state, done)
            self.agent.replay(env, location_label)  # internally iterates default (prediction) model
            self.agent.target_train()  # iterates target model
            cur_state = new_state

        print("\n\nAGENT STARTS IN LOCATION ({},{}) - LABEL {} - GAME FINISHED IN {} STEPS!\n\n".format(start_location[0], start_location[1], location_label, steps_num))

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
