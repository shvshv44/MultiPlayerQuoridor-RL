import json

import mcts

import rest_api
from MCTSState import MCTSState, Board, transform_physical_board_to_input_board
from quoridor_env import QuoridorEnv
from tcp import TCP


class MCTSTrainer:

    # noinspection SpellCheckingInspection
    def __init__(self, agent):
        self.agent = agent
        self.name = "MCTSTrainer"
        self.mcts = mcts.mcts(timeLimit=1000)

    def start_training_session(self, num_of_episodes):
        for episode_num in range(num_of_episodes):
            self.headline_print("start game of episode number {}".format(episode_num + 1))
            self.game_id = rest_api.create_game(self.name).content.decode("utf-8")
            print("Trainer created game with id: {}".format(self.game_id))
            self.tcp = TCP(self.game_id, self.name, self.on_recieved)
            self.start_game_with_agent(self.game_id)
            self.headline_print("end of episode number {}".format(episode_num + 1))

    def start_game_with_agent(self, game_id):
        env = QuoridorEnv(game_id, "Agent", False)

        cur_state = env.reset()
        done = False
        steps_num = 0

        while not done:
            steps_num += 1
            action = self.agent.act(cur_state, env)
            new_state, reward, done, _ = env.step(action)
            self.agent.remember(cur_state, action, reward, new_state, done)
            self.agent.replay(env)  # internally iterates default (prediction) model
            self.agent.target_train()  # iterates target model
            cur_state = new_state

        print("\n\nGAME FINISHED IN {} STEPS!\n\n".format(steps_num))

    def on_recieved(self, json_message):
        if json_message["type"] == "NewTurnEvent":
            if json_message["nextPlayerToPlay"] == self.name:
                board = Board(json.loads(rest_api.get_mcts_board(self.game_id).content))
                state = MCTSState(board)
                action = self.mcts.search(state).data
                self.tcp.write(action)

        elif json_message["type"] == "GameOverEvent":
            pass
        elif json_message["type"] == "RoomStateResponse":
            if len(json_message["players"]) == 2:
                rest_api.start_game(self.game_id)

    def headline_print(self, text):
        print("====================================================")
        print(text)
        print("====================================================")
