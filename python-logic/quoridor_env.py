import gym
from gym import spaces
from numpy import zeros


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

    def __init__(self, players, main_player_index):
        self.players = players
        self.main_player_index = main_player_index
        self.board = self.init_board()

        self.action_space = spaces.Tuple((
            # choosing either to move or put wall
            spaces.Discrete(2),
            # 4 directions to move - 0 = UP, 1 = DOWN, 2 = LEFT, 3 = RIGHT
            spaces.Discrete(4),
            # 64 walls to put horizontally and 64 walls to put vertically
            spaces.Discrete(8 * 8 * 2)
        ))

        self.observation_space = spaces.Tuple((
            # The player cell location
            spaces.Discrete(81),
            # The opponent cell location
            spaces.Discrete(81),
            # 64 horizontal walls possibilities - 0 is without wall and 1 is with wall
            spaces.MultiBinary([8, 8]),
            # 64 vertical walls possibilities - 0 is without wall and 1 is with wall
            spaces.MultiBinary([8, 8])
        ))

        self.seed()

        # Start the first game
        self.reset()

    def step(self, action):
        assert self.action_space.contains(action)

        self.update_board(self.players[self.main_player_index], action)
        reward, done = self.calculate_reward()
        return tuple(self.board), reward, done, self.get_info()

    def reset(self):
        # TODO: implement
        print("reset")

    def get_info(self):
        # TODO: implement
        return {}

    def init_board(self):
        return [
            self.players[0].start_location,
            self.players[1].start_location,
            zeros(shape=(8, 8)),
            zeros(shape=(8, 8))
        ]

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

    def update_board(self, player, action):
        if action[0] == 0:
            self.board[player.index] = self.get_new_cell_position( self.board[player.index], action[1])
        if action[0] == 1:
            assert 0 <= action[2] <= 64 * 2
            if action[2] < 64:
                self.board[len(self.players)][action[2] // 8][action[2] % 8] = 1
            else:
                self.board[len(self.players) + 1][(action[2] - 64) // 8][(action[2] - 64) % 8] = 1

    def get_new_cell_position(self, cur_location, direction):
        if direction == 0:
            cur_location -= 9  # UP
        elif direction == 1:
            cur_location += 9  # DOWN
        elif direction == 2:
            cur_location -= 1  # LEFT
        elif direction == 3:
            cur_location += 1  # RIGHT

        assert 0 <= cur_location <= 81
        return cur_location

    def print_board(self):
        self.print_matrix(self.board_to_print_matrix())

    def board_to_print_matrix(self):
        matrix = self.init_print_matrix()

        for i in range(10):
            for j in range(9):
                inew = i*2
                jnew = j*2 + 1
                matrix[inew][jnew] = '---'

        for i in range(9):
            for j in range(10):
                matrix[i*2 + 1][j*2] = ' | '

        self.add_location_to_print_matrix(matrix, self.board[0], ' P ')
        self.add_location_to_print_matrix(matrix, self.board[1], ' O ')

        for i in range(8):
            for j in range(8):
                if self.board[3][i, j] == 1:
                    matrix[i * 2 + 1][j*2 + 2] = '|||'
                    matrix[i * 2 + 3][j * 2 + 2] = '|||'

        for i in range(8):
            for j in range(8):
                if self.board[2][i, j] == 1:
                    matrix[i * 2 + 2][j*2 + 1] = '==='
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
        i = location // 9
        j = location % 9

        matrix[i*2 + 1][j*2 + 1] = symbol