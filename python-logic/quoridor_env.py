import gym
from gym import spaces


class QuoridorEnv(gym.Env):

    def __init__(self, players_names, players_locations, players_targets, player_name):
        self.players_targets = players_targets
        self.players_locations = players_locations
        self.players_names = players_names
        self.player_name = player_name

        self.current_board = self.init_board(players_locations)

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

        self.update_board(action)
        reward, done = self.calculate_reward()
        return self.current_board, reward, done, self.get_info()

    def reset(self):
        # TODO: implement
        print("reset")

    def get_info(self):
        # TODO: implement
        return {}

    def init_board(self, players_names, locations):
        return (
            locations[players_names[0]],
            locations[players_names[1]],
            [[0, 0, 0, 0, 0, 0, 0, 0],
             [0, 0, 0, 0, 0, 0, 0, 0],
             [0, 0, 0, 0, 0, 0, 0, 0],
             [0, 0, 0, 0, 0, 0, 0, 0],
             [0, 0, 0, 0, 0, 0, 0, 0],
             [0, 0, 0, 0, 0, 0, 0, 0],
             [0, 0, 0, 0, 0, 0, 0, 0],
             [0, 0, 0, 0, 0, 0, 0, 0]],
            [[0, 0, 0, 0, 0, 0, 0, 0],
             [0, 0, 0, 0, 0, 0, 0, 0],
             [0, 0, 0, 0, 0, 0, 0, 0],
             [0, 0, 0, 0, 0, 0, 0, 0],
             [0, 0, 0, 0, 0, 0, 0, 0],
             [0, 0, 0, 0, 0, 0, 0, 0],
             [0, 0, 0, 0, 0, 0, 0, 0],
             [0, 0, 0, 0, 0, 0, 0, 0]]
        )

    def calculate_reward(self):
        reward = 0
        done = False

        for player in self.players_names:
            if self.players_targets[player].contains(self.players_locations[player]):
                done = True
                if player == self.player_name:
                    reward = 1
                else:
                    reward = -1
                break

        return reward, done

    def update_board(self, action):
        print("TODO!: update the board!")
