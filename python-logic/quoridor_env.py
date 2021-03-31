from enum import Enum

import gym
import numpy as np
from gym import spaces
from numpy import zeros
from rest_api import join_game, get_board
import json
from tcp import TCP
import utils
from globals import Global


class GameWinnerStatus(Enum):
    NoWinner = 0
    EnvWinner = 1
    EnvLoser = 2


def action_shape():
    return 1


def observation_shape():
    return (1,) + (9, 9, 4) # window length + board shape


class QuoridorEnv(gym.Env):
    """
    players: {
        index: number,
        start_location: number,
        name: string,
        targets: [number]
    }

    """

    def __init__(self, game_id, player_name):
        self.game_id = game_id
        self.player_name = player_name
        self.is_my_turn = False
        self.winner_status = GameWinnerStatus.NoWinner
        self.last_turn_illegal = False

        # join_game(self.game_id, self.player_name)

        self.tcp = TCP(game_id, player_name, self.on_recieved)
        self.wait_for_my_turn()

        self.action_space = spaces.Discrete(Global.num_of_actions)

        self.observation_space = spaces.Tuple((
            spaces.MultiBinary([9, 9]),
            spaces.MultiBinary([9, 9]),
            spaces.MultiBinary([9, 9]),
            spaces.MultiBinary([9, 9])
        ))

        self.seed()

        # Start the first game
        self.reset()

    def step(self, action):
        assert self.action_space.contains(action)

        action = int(action)

        self.update_board(action)
        self.wait_for_my_turn()

        reward, done = self.calculate_reward()
        self.is_my_turn = False
        return self.board, reward, done, {}

    def wait_for_my_turn(self):
        while not self.is_my_turn:
            pass

    def reset(self):
        # self.board = self.init_board()
        return self.board

    def calculate_reward(self):
        reward = 0
        done = False

        if self.last_turn_illegal:
            reward = -1
            self.last_turn_illegal = False

        if self.winner_status != GameWinnerStatus.NoWinner:
            done = True
            if self.winner_status == GameWinnerStatus.EnvWinner:
                reward = 10
            elif self.winner_status == GameWinnerStatus.EnvLoser:
                reward = -10

        return reward, done

    def update_board(self, action):
        operation = utils.convert_action_to_server(action)
        self.send_to_server(operation)  # WAITING

    def get_and_convert_board(self):
        board = json.loads(get_board(self.game_id).content)
        dim1 = np.zeros((9, 9), dtype=int)
        dim2 = np.zeros((9, 9), dtype=int)
        dim1[board["players"][0]["y"]][board["players"][0]["x"]] = 1
        dim2[board["players"][1]["y"]][board["players"][1]["x"]] = 1

        all_dims = np.dstack((dim1, dim2, board["horizontalWalls"], board["verticalWalls"]))
        return all_dims
        # return np.asarray(all_dims)

    def send_to_server(self, operation):
        self.tcp.write(operation)

    def on_recieved(self, json_message):

        if json_message["type"] == "IllegalMove":
            #self.is_my_turn = True
            self.last_turn_illegal = True
        elif json_message["type"] == "NewTurnEvent":
            if json_message["nextPlayerToPlay"] == self.player_name:
                self.board = self.get_and_convert_board()
                self.is_my_turn = True
        elif json_message["type"] == "GameOverEvent":
            self.is_my_turn = True
            if json_message["winnerName"] == self.player_name:
                self.winner_status = GameWinnerStatus.EnvWinner
            else:
                self.winner_status = GameWinnerStatus.EnvLoser

    def action_shape(self):
        return action_shape()

    def observation_shape(self):
        return observation_shape()