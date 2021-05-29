from enum import Enum

import gym
import numpy as np
from gym import spaces

import rest_api
from rest_api import get_board
import json
from tcp import TCP
import utils
from globals import Global
import os


class GameWinnerStatus(Enum):
    NoWinner = 0
    EnvWinner = 1
    EnvLoser = 2


class MoveType(Enum):
    MOVE = 0
    WALL = 1


def action_shape():
    return 1


def observation_shape():
    return Global.observation_shape


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
        self.action_options = []
        self.player_winning_points_dim = np.zeros(shape=(9, 9), dtype=int)
        self.player_winning_points = []
        self.player_location = (-1, -1)
        self.player_start_location = (-1, -1)
        self.winner_name = ""
        self.shortest_path_size = 1000000

        # join_game(self.game_id, self.player_name)

        self.tcp = TCP(game_id, player_name, self.on_recieved)
        self.wait_for_my_turn()

        self.action_space = spaces.Discrete(4) # 4 moves

        # location, horizontal walls, vertical walls, winning location
        self.observation_space = spaces.Tuple((
            spaces.Discrete(9*9),
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
        print("This turn reward: {}".format(reward))
        self.is_my_turn = False

        # self.print_board()
        return self.board, reward, done, {}

    def wait_for_my_turn(self):
        while not self.is_my_turn:
            pass

    def reset(self):
        return self.board

    def calculate_reward(self):
        done = False

        reward = - self.shortest_path_size / 20

        # To prefer to win
        if self.winner_status != GameWinnerStatus.NoWinner:
            done = True
            if self.winner_status == GameWinnerStatus.EnvWinner:
                reward = 1

        return reward, done

    def update_board(self, action):
        operation = utils.convert_action_to_server(action)
        self.send_to_server(operation)  # WAITING

    def get_and_convert_board(self):
        board = json.loads(get_board(self.game_id).content)
        pi = self.find_player_index(board["players"])
        self.player_location = (int(board["players"][pi]["position"]["y"]), int(board["players"][pi]["position"]["x"]))

        loc = utils.location_to_index(self.player_location)
        all_dims = np.dstack((board["horizontalWalls"], board["verticalWalls"], self.player_winning_points_dim))
        return [loc, all_dims]

    def send_to_server(self, operation):
        self.tcp.write(operation)

    def on_recieved(self, json_message):

        if json_message["type"] == "NewTurnEvent":
            if json_message["nextPlayerToPlay"] == self.player_name:
                self.board = self.get_and_convert_board()
                self.shortest_path_size = int(json_message["playerShortestPaths"][self.player_name])
                self.is_my_turn = True
                self.update_action_options(json_message)
        elif json_message["type"] == "GameOverEvent":
            self.is_my_turn = True
            self.winner_name = json_message["winnerName"]
            if json_message["winnerName"] == self.player_name:
                self.winner_status = GameWinnerStatus.EnvWinner
            else:
                self.winner_status = GameWinnerStatus.EnvLoser
        elif json_message["type"] == "StartGameMessage":
            self.update_winning_locations(json_message["players"])
            self.player_start_location = self.get_start_player_location(json_message["players"])


    def action_shape(self):
        return action_shape()

    def observation_shape(self):
        return observation_shape()

    def update_action_options(self, moves_json):
        self.action_options = utils.convert_moves_to_walking_action_options(moves_json)

    def get_action_options(self):
        return self.action_options

    def update_winning_locations(self, players):
        for player in players:
            if player["name"] == self.player_name:
                self.player_winning_points = player["endLine"]
                for loc in player["endLine"]:
                    x = int(loc["x"])
                    y = int(loc["y"])
                    self.player_winning_points_dim[y, x] = 1

    def get_start_player_location(self, players):
        loc = (-1, -1)
        for player in players:
            if player["name"] == self.player_name:
                x = int(player["position"]["x"])
                y = int(player["position"]["y"])
                loc = (y, x)

        return loc

    def find_player_index(self, players):
        index = 0
        found = 0
        for player in players:
            if player["name"] == self.player_name:
                found = index
            index += 1

        return found
