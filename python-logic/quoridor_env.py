from enum import Enum

import gym
import numpy as np
from gym import spaces
from rest_api import get_board
import json
from tcp import TCP
import utils
from globals import Global
from utils import convert_board
import os


class GameWinnerStatus(Enum):
    NoWinner = 0
    EnvWinner = 1
    EnvLoser = 2


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

    def __init__(self, game_id, player_name, is_not_tcp):
        self.game_id = game_id
        self.player_name = player_name
        self.is_my_turn = False
        self.winner_status = GameWinnerStatus.NoWinner
        self.last_turn_illegal = False
        self.action_options = []
        self.winning_points_dim = np.zeros(shape=(9, 9), dtype=int)

        # join_game(self.game_id, self.player_name)

        if not is_not_tcp:
            self.tcp = TCP(game_id, player_name, self.on_recieved)
            self.wait_for_my_turn()

        self.action_space = spaces.Discrete(Global.num_of_actions)

        self.observation_space = spaces.Tuple((
            spaces.MultiBinary([9, 9]),
            spaces.MultiBinary([9, 9]),
            spaces.MultiBinary([9, 9]),
            spaces.MultiBinary([9, 9]),
            spaces.MultiBinary([9, 9])
        ))

        self.seed()

        # Start the first game
        #self.reset()

    def step(self, action):
        assert self.action_space.contains(action)

        action = int(action)
        self.update_board(action)
        self.wait_for_my_turn()
        reward, done = self.calculate_reward()
        self.is_my_turn = False

        # self.print_board()
        return self.board, reward, done, {}

    def wait_for_my_turn(self):
        while not self.is_my_turn:
            pass

    def reset(self):
        # self.board = self.init_board()
        return self.board

    def calculate_reward(self):
        reward = -1
        done = False

        if self.last_turn_illegal:
            reward = -10
            self.last_turn_illegal = False

        if self.winner_status != GameWinnerStatus.NoWinner:
            done = True
            if self.winner_status == GameWinnerStatus.EnvWinner:
                reward = 200
            elif self.winner_status == GameWinnerStatus.EnvLoser:
                reward = -200

        return reward, done

    def update_board(self, action):
        operation = utils.convert_action_to_server(action)
        self.send_to_server(operation)  # WAITING

    def print_board(self):
        os.system('cls')
        print('------- TEAM 600 --------')
        arrays = np.dsplit(self.board, 4)
        for y in range(9):
            for x in range(9):
                print('|', end='')
                if arrays[0][y][x] != 0:
                    print(1, end='')
                elif arrays[1][y][x] != 0:
                    print(2, end='')
                else:
                    print(' ', end='')
                if arrays[3][y][x] != 0:
                    print('|', end='')
                elif y != 0 & arrays[3][y - 1][x] != 0:
                    print('|', end='')
                else:
                    print(' ', end='')

            print('')
            for x_wall in range(9):
                if arrays[2][y][x_wall] != 0:
                    print(' ==', end='')
                elif x_wall != 0 & arrays[2][y][x_wall - 1] != 0:
                    print(' ==', end='')
                else:
                    print(' __', end='')
            print('')

    def get_and_convert_board(self):
        board = json.loads(get_board(self.game_id).content)
        return convert_board(board, self.winning_points_dim)

    def send_to_server(self, operation):
        self.tcp.write(operation)

    def on_recieved(self, json_message):
        if json_message["type"] == "IllegalMove":
            # self.is_my_turn = True
            self.last_turn_illegal = True
        elif json_message["type"] == "NewTurnEvent":
            if json_message["nextPlayerToPlay"] == self.player_name:
                self.board = self.get_and_convert_board()
                self.is_my_turn = True
                self.update_action_options(json_message)
        elif json_message["type"] == "GameOverEvent":
            self.is_my_turn = True
            if json_message["winnerName"] == self.player_name:
                self.winner_status = GameWinnerStatus.EnvWinner
            else:
                self.winner_status = GameWinnerStatus.EnvLoser
        elif json_message["type"] == "StartGameMessage":
            self.update_winning_locations(json_message["players"])

    def action_shape(self):
        return action_shape()

    def observation_shape(self):
        return observation_shape()

    def update_action_options(self, moves_json):
        self.action_options = []

        myLoc = moves_json["currentPosition"]
        for move in moves_json["avialiableMoves"]:
            if int(move["x"]) > int(myLoc["x"]):
                self.action_options.append(3)  # Move Right
            elif int(move["x"]) < int(myLoc["x"]):
                self.action_options.append(2)  # Move Left
            elif int(move["y"]) > int(myLoc["y"]):
                self.action_options.append(1)  # Move Down
            elif int(move["y"]) < int(myLoc["y"]):
                self.action_options.append(0)  # Move Up

        for wall in moves_json["availableWalls"]:
            wall_action = 4 + wall["position"]["x"] + (8 * wall["position"]["y"])
            if wall["wallDirection"] == "Down":
                wall_action += 64
            self.action_options.append(wall_action)

    def get_action_options(self):
        return self.action_options

    def update_winning_locations(self, players):
        for player in players:
            if player["name"] == self.player_name:
                for loc in player["endLine"]:
                    x = int(loc["x"])
                    y = int(loc["y"])
                    self.winning_points_dim[y, x] = 1
        print(self.winning_points_dim)
