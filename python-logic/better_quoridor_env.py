import json
import os
from enum import Enum

import gym
import numpy as np
from gym import spaces

import utils
from bfs import build_graph, BFS_SP
from globals import Global
from rest_api import get_board
from tcp import TCP


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
        self.opponent_winning_points_dim = np.zeros(shape=(9, 9), dtype=int)
        self.player_winning_points = []
        self.opponent_winning_points = []
        self.player_location = (-1, -1)
        self.opponent_location = (-1, -1)
        self.player_start_location = (-1, -1)
        self.opponent_start_location = (-1, -1)
        self.winner_name = ""
        self.last_move_type = MoveType.MOVE

        # join_game(self.game_id, self.player_name)

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
        self.reset()

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
        done = False

        reward = -0.001
        # To prefer be close to goal and keep opponent far from goal
        graph = self.create_graph()
        reward += - 0.0005 * self.calculate_closest_goal_distance_bfs(graph, self.player_location,
                                                                      self.player_winning_points)
        reward += - 0.0005 * (40 - self.calculate_closest_goal_distance_bfs(graph, self.opponent_location,
                                                                            self.opponent_winning_points))

        # To prefer saving the walls for good moments
        if self.last_move_type == MoveType.WALL:
            reward -= 0.2

        # To prefer to win
        if self.winner_status != GameWinnerStatus.NoWinner:
            done = True
            if self.winner_status == GameWinnerStatus.EnvWinner:
                reward = 0.7
            elif self.winner_status == GameWinnerStatus.EnvLoser:
                reward = -0.7

        return reward, done

    def update_board(self, action):
        if 0 <= action <= 3:
            self.last_move_type = MoveType.MOVE
        else:
            self.last_move_type = MoveType.WALL

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
        dim1 = np.zeros((9, 9), dtype=int)
        dim2 = np.zeros((9, 9), dtype=int)
        pi = self.find_player_index(board["players"])
        oi = self.find_opponent_index(board["players"])
        dim1[board["players"][pi]["position"]["y"]][board["players"][pi]["position"]["x"]] = 1
        dim2[board["players"][oi]["position"]["y"]][board["players"][oi]["position"]["x"]] = 1
        self.player_location = (int(board["players"][pi]["position"]["y"]), int(board["players"][pi]["position"]["x"]))
        self.opponent_location = (int(board["players"][oi]["position"]["y"]), int(board["players"][oi]["position"]["x"]))

        self.horizontal_walls = board["horizontalWalls"]
        self.vertical_walls = board["verticalWalls"]

        all_dims = np.dstack((dim1, dim2, board["horizontalWalls"], board["verticalWalls"],
                              self.player_winning_points_dim, self.opponent_winning_points_dim))
        return all_dims

    def send_to_server(self, operation):
        self.tcp.write(operation)

    def on_recieved(self, json_message):
        if json_message["type"] == "NewTurnEvent":
            if json_message["nextPlayerToPlay"] == self.player_name:
                self.board = self.get_and_convert_board()
                self.update_action_options(json_message)
                self.is_my_turn = True
        elif json_message["type"] == "GameOverEvent":
            self.winner_name = json_message["winnerName"]
            if json_message["winnerName"] == self.player_name:
                print("Agent win")
                self.winner_status = GameWinnerStatus.EnvWinner
            else:
                print("Trainer win")
                self.winner_status = GameWinnerStatus.EnvLoser
            self.is_my_turn = True
            # self.tcp.close_connection()
        elif json_message["type"] == "StartGameMessage":
            self.update_winning_locations(json_message["players"])
            self.update_winning_locations_for_opponent(json_message["players"])
            self.player_start_location = self.get_start_player_location(json_message["players"])
            self.opponent_start_location = self.get_start_opponent_location(json_message["players"])

    def action_shape(self):
        return action_shape()

    def observation_shape(self):
        return observation_shape()

    def update_action_options(self, moves_json):
        self.action_options = utils.convert_moves_to_action_options(moves_json)

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

    def update_winning_locations_for_opponent(self, players):
        for player in players:
            if player["name"] != self.player_name:
                self.opponent_winning_points = player["endLine"]
                for loc in player["endLine"]:
                    x = int(loc["x"])
                    y = int(loc["y"])
                    self.opponent_winning_points_dim[y, x] = 1

    def get_start_player_location(self, players):
        loc = (-1, -1)
        for player in players:
            if player["name"] == self.player_name:
                x = int(player["position"]["x"])
                y = int(player["position"]["y"])
                loc = (y, x)

        return loc

    def get_start_opponent_location(self, players):
        loc = (-1, -1)
        for player in players:
            if player["name"] != self.player_name:
                x = int(player["position"]["x"])
                y = int(player["position"]["y"])
                loc = (y, x)

        return loc

    def calculate_closest_goal_distance(self, winning_points, location):
        closest = 1000000
        for point in winning_points:
            distance = abs(int(point["x"]) - location[1]) + abs(int(point["y"]) - location[0])
            if distance < closest:
                closest = distance

        return closest

    def create_graph(self):
        return build_graph(self.vertical_walls, self.horizontal_walls)

    def calculate_closest_goal_distance_bfs(self, graph, location, target):
        shortest_road = BFS_SP(graph, location, target)
        return shortest_road

    def find_player_index(self, players):
        index = 0
        found = 0
        for player in players:
            if player["name"] == self.player_name:
                found = index
            index += 1

        return found

    def find_opponent_index(self, players):
        index = 0
        found = 0
        for player in players:
            if player["name"] != self.player_name:
                found = index
            index += 1

        return found
