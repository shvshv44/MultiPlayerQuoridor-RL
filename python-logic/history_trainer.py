import numpy as np
import random
import rest_api
from quoridor_env import QuoridorEnv
from utils import convert_board
import json

class HistoryTrainer:

    def __init__(self, agent):
        self.agent = agent

    def start(self):
        history_game_ids = json.loads(rest_api.get_history_game_ids())
        for game_id in history_game_ids:
            self.my_number = random.randint(0, 2)
            self.history = json.loads(rest_api.history_game(game_id["gameId"]))
            self.player1Name = self.history["playOrder"][0]["uuid"]
            self.player2Name = self.history["playOrder"][1]["uuid"]
            self.player1Position = self.history["startingPosition"][self.player1Name]
            self.player2Position = self.history["startingPosition"][self.player2Name]
            self.horizontalWalls = np.zeros(shape=(9, 9), dtype=int)
            self.verticalWalls = np.zeros(shape=(9, 9), dtype=int)
            self.reward = -1
            self.done = False
            self.my_name = self.player1Name if self.my_number == 0 else self.player2Name
            self.winning_points_dim = np.zeros(shape=(9, 9), dtype=int)

            if self.history["winner"] == self.my_number:
                self.game_and_reward = 200
            else:
                self.game_and_reward = -200

            for loc in self.history["pawnEndLine"][self.my_name]:
                x = int(loc["x"])
                y = int(loc["y"])
                self.winning_points_dim[y, x] = 1

            if self.my_number == 0: # check if i am the first player
                self.index = 0
            else:
                self.index = 1
                history = self.history["history"]
                if history[0]["action"]["actionType"] == "MOVE_PAWN":
                    self.player1Position = history[0]["details"]["position"]
                else:
                    position = history[0]["action"]["wall"]["position"]
                    if history[0]["action"]["wall"]["wallDirection"] == "Right":
                        self.horizontalWalls[position["y"]][position["x"]] = 1
                        self.horizontalWalls[position["y"]][position["x"] + 1] = 1
                    else:
                        self.verticalWalls[position["y"]][position["x"]] = 1
                        self.verticalWalls[position["y"] + 1][position["x"]] = 1

        self.start_game_with_agent()

    def start_game_with_agent(self):
        env = QuoridorEnv('history', "Agent", True)

        cur_state = self.convert_to_board()
        steps_num = 0

        while not self.done:
            steps_num += 1
            action = self.act(self.index)
            new_state = self.convert_to_board()
            self.agent.remember(cur_state, action, self.reward, new_state, self.done)
            self.agent.replay(env)  # internally iterates default (prediction) model
            self.agent.target_train()  # iterates target model
            cur_state = new_state
            self.index += 2

        print("\n\nGAME FINISHED IN {} STEPS!\n\n".format(steps_num))

    def act(self, index):
        history = self.history["history"]
        action = 0
        for curr_index in range(index, index + 2): # TODO: check if i am the second player
            if len(history) > curr_index:
                if history[curr_index]["action"]["actionType"] == "MOVE_PAWN":
                    if curr_index % (2 if self.my_number == 0 else 1):
                        action = self.calc_action_move_pawn(history[curr_index]["action"]["actionType"])
                        self.player1Position = history[curr_index]["details"]["position"]
                    else:
                        self.player2Position = history[curr_index]["details"]["position"]
                else:
                    position = history[curr_index]["action"]["wall"]["position"]
                    if curr_index % (2 if self.my_number == 0 else 1):
                        action = self.calc_action_walls(history[curr_index]["action"]["wall"])
                    if history[curr_index]["action"]["wall"]["wallDirection"] == "Right":
                        self.horizontalWalls[position["y"]][position["x"]] = 1
                        self.horizontalWalls[position["y"]][position["x"] + 1] = 1
                    else:
                        self.verticalWalls[position["y"]][position["x"]] = 1
                        self.verticalWalls[position["y"] + 1][position["x"]] = 1
            else:
                self.done = True
                self.game_and_reward = self.reward

        return action

    def calc_action_walls(self, wall):
        wall_action = 4 + wall["position"]["x"] + (8 * wall["position"]["y"])
        if wall["wallDirection"] == "Down":
            wall_action += 64
        return wall_action

    def calc_action_move_pawn(self, position_direction):
        if position_direction == "Right":
            return 3  # Move Right
        elif position_direction == "Left":
            return 2  # Move Left
        elif position_direction == "Down":
            return 1  # Move Down
        elif position_direction == "Up":
            return 0  # Move Up

    def convert_to_board(self):
        board = {
            "players": [self.player1Position, self.player2Position],
            "horizontalWalls": self.horizontalWalls,
            "verticalWalls": self.verticalWalls
        }
        return convert_board(board, self.winning_points_dim)
