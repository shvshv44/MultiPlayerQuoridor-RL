import gym
import numpy as np
from gym import spaces
from numpy import zeros
from rest_api import RestApi
import json
from tcp import TCP


class Player:
    def __init__(self, index, start_location, name, targets):
        self.index = index
        self.start_location = start_location
        self.name = name
        self.targets = targets


class QuoridorEnv(gym.Env):
    """
    players: {
        index: number,
        start_location: number,
        name: string,
        targets: [number]
    }

    """

    def  __init__(self, gameId):
        self.gameId = gameId
        self.tcp = TCP(gameId)
        self.board = self.get_and_convert_board()

        self.action_space = spaces.Discrete(4 + 8 * 8 * 2)

        self.observation_space = spaces.Tuple((
            spaces.MultiBinary([9, 9]),
            spaces.MultiBinary([9, 9]),
            spaces.MultiBinary([9, 9]),
            spaces.MultiBinary([9, 9])
        ))

        sample = self.observation_space.sample()
        g = self.sample_to_input(sample)

        #self.observation_space.shape = self.to_shape()



        self.seed()

        # Start the first game
        self.reset()

    def step(self, action):
        assert self.action_space.contains(action)

        self.update_board(action)
        #JAVA
        reward, done = self.calculate_reward()
        return tuple(self.board), reward, done, self.get_info(action)

    def reset(self):
        # self.board = self.init_board()
        pass

    def get_info(self, action):
        action_details = ""

        if action[0] == 0:
            i, j = self.cell_location_to_indexes(self.board[self.main_player_index])
            _, direction_name = self.move_direction_to_data(action[1])
            action_details = "Player moved " + direction_name + " and its new location is (" + str(i) + "," + str(j) + ")"
        elif action[0] == 1:
            if action[2] < 64:
                i, j = self.wall_location_to_indexes(action[2])
                wall_type = "HORIZONTAL"
            else:
                i, j = self.wall_location_to_indexes(action[2] - 64)
                wall_type = "VERTICAL"
            action_details = "Player put wall of type " + wall_type + " and its location is (" + str(i) + "," + str(j) + ")"

        return {"action:": action_details}

    def calculate_reward(self):
        reward = 0
        done = False

        for player in self.players:
            if self.board[player.index] in player.targets:
                done = True
                if player.index == self.main_player_index:
                    reward = 1
                else:
                    reward = -1
                break

        return reward, done

    def update_board(self, action):
        operation = self.convert_action_to_server(action)
        self.send_to_server(operation) # WAITING
        self.board = self.get_and_convert_board()



    def get_new_cell_position(self, cur_location, direction):
        addition, _ = self.move_direction_to_data(direction)
        cur_location += addition
        assert 0 <= cur_location <= 81
        return cur_location

    def print_board(self):
        self.print_matrix(self.board_to_print_matrix())

    def board_to_print_matrix(self):
        matrix = self.init_print_matrix()

        for i in range(10):
            for j in range(9):
                inew = i * 2
                jnew = j * 2 + 1
                matrix[inew][jnew] = '---'

        for i in range(9):
            for j in range(10):
                matrix[i * 2 + 1][j * 2] = ' | '

        self.add_location_to_print_matrix(matrix, self.board[0], ' P ')
        self.add_location_to_print_matrix(matrix, self.board[1], ' O ')

        for i in range(8):
            for j in range(8):
                if self.board[3][i, j] == 1:
                    matrix[i * 2 + 1][j * 2 + 2] = '|||'
                    matrix[i * 2 + 3][j * 2 + 2] = '|||'

        for i in range(8):
            for j in range(8):
                if self.board[2][i, j] == 1:
                    matrix[i * 2 + 2][j * 2 + 1] = '==='
                    matrix[i * 2 + 2][j * 2 + 3] = '==='

        return matrix

    def print_matrix(self, matrix):
        for row in matrix:
            for col in row:
                print(col, end='')
            print('')

    def init_print_matrix(self):
        rows, cols = (19, 19)
        matrix = []
        for i in range(rows):
            col = []
            for j in range(cols):
                col.append('   ')
            matrix.append(col)
        return matrix

    def add_location_to_print_matrix(self, matrix, location, symbol):
        i, j = self.cell_location_to_indexes(location)
        matrix[i * 2 + 1][j * 2 + 1] = symbol

    def cell_location_to_indexes(self, location):
        return location // 9, location % 9

    def wall_location_to_indexes(self, location):
        return location // 9, location % 9

    def move_direction_to_data(self, direction):
        # return the location addition + direction name
        if direction == 0:
            return -9, "UP"
        elif direction == 1:
            return 9, "DOWN"
        elif direction == 2:
            return -1, "LEFT"
        elif direction == 3:
            return 1, "RIGHT"

        raise ValueError(direction + " is not a valid action!")

    def to_shape(self):
        return np.ndarray(9,9,4)

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
        board = json.loads(RestApi.get_board(self.gameId))
        dim1 = np.zeros((9, 9, 1))
        dim2 = np.zeros((9, 9, 1))
        dim1[board["players"][0]["x"],board["players"][0]["y"]] = 1
        dim2[board["players"][1]["x"],board["players"][1]["y"]] = 1
        return np.ndarray(dim1, dim2, board["verticalWalls"], board["horizontalWalls"])

    def convert_action_to_server(self, action):
        output = json.dumps({})
        if 0 <= action <= 3:
            output = json.dumps({'direction': self.movements[action]})
        elif 4 <= action <= 131:
            dir = "Right"
            value = action - 4

            if value > 64 - 1:
                dir = "Down"
                value -= 64

            x, y = value % 8, value // 8

            output = json.dumps({
                'wall': {
                    "position":{
                        "x":x,
                        "y":y
                    },
                    "wallDirection": dir
                }
            }
        )

        return output

    def send_to_server(self, operation):
        self.tcp.write(operation)

