from enum import Enum

import gym
import numpy as np
from gym import spaces
from numpy import zeros
from rest_api import join_game, get_board
import json
from tcp import TCP


class GameWinnerStatus(Enum):
    NoWinner = 0
    EnvWinner = 1
    EnvLoser = 2


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

        # join_game(self.game_id, self.player_name)

        self.tcp = TCP(game_id, player_name, self.on_recieved)
        self.wait_for_my_turn()

        self.action_space = spaces.Discrete(4 + 8 * 8 * 2)

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

        self.update_board(action)
        self.wait_for_my_turn()

        reward, done = self.calculate_reward()
        self.is_my_turn = False
        return tuple(self.board), reward, done, {}

    def wait_for_my_turn(self):
        while not self.is_my_turn:
            pass

    def reset(self):
        # self.board = self.init_board()
        pass

    def calculate_reward(self):
        reward = 0
        done = False

        if self.winner_status != GameWinnerStatus.NoWinner:
            done = True
            if self.winner_status == GameWinnerStatus.EnvWinner:
                reward = 1
            elif self.winner_status == GameWinnerStatus.EnvLoser:
                reward = -1

        return reward, done

    def update_board(self, action):
        operation = self.convert_action_to_server(action)
        self.send_to_server(operation)  # WAITING

    def get_new_cell_position(self, cur_location, direction):
        addition, _ = self.move_direction_to_data(direction)
        cur_location += addition
        assert 0 <= cur_location <= 81
        return cur_location

    def print_board(self):
        self.print_matrix(self.board_to_print_matrix())

    def to_shape(self):
        return np.ndarray(9, 9, 4)

    # NOT WORKING
    #     def sample_to_input(self, sample):
    #         dim1 = np.zeros((9,9,1))
    #         i1,j1 = self.cell_location_to_indexes(sample[0])
    #         i2,j2 = self.cell_location_to_indexes(sample[1])
    #         dim1[i1,j1] = 1
    #         dim1[i2,j2] = 2
    #         dim2 = sample[2]
    #         dim3 = sample[3]
    #         return np.ndarray(dim1, dim2, dim3)

    def get_and_convert_board(self):
        board = json.loads(get_board(self.game_id).content)
        print(board)
        dim1 = np.zeros((9, 9), dtype=int)
        dim2 = np.zeros((9, 9), dtype=int)
        dim1[board["players"][0]["y"]][board["players"][0]["x"]] = 1
        dim2[board["players"][1]["y"]][board["players"][1]["x"]] = 1

        all_dims = []
        all_dims.append(dim1)
        all_dims.append(dim2)
        all_dims.append(board["horizontalWalls"])
        all_dims.append(board["verticalWalls"])

        return np.asarray(all_dims)

    def convert_action_to_server(self, action):
        output = json.dumps({})
        if 0 <= action <= 3:
            output = json.dumps({'direction': self.tcp.movements[action].value})
        elif 4 <= action <= 131:
            dir = "Right"
            value = action - 4

            if value > 64 - 1:
                dir = "Down"
                value -= 64

            x, y = value % 8, value // 8

            output = json.dumps({
                'wall': {
                    "position": {
                        "x": x,
                        "y": y
                    },
                    "wallDirection": dir
                }
            }
            )

        return output

    def send_to_server(self, operation):
        self.tcp.write(operation)

    def on_recieved(self, json_message):
        if json_message["type"] == "NewTurnEvent":
            if json_message["nextPlayerToPlay"] == self.player_name:
                self.board = self.get_and_convert_board()
                self.is_my_turn = True
        elif json_message["type"] == "GameOverEvent":
            self.is_my_turn = True
            if json_message["winnerName"] == self.player_name:
                self.winner_status = GameWinnerStatus.EnvWinner
            else:
                self.winner_status = GameWinnerStatus.EnvLoser
