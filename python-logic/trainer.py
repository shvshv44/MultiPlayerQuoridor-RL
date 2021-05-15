import numpy as np

import rest_api
import utils
from quoridor_env import QuoridorEnv
from tcp import TCP


class Trainer:
    def __init__(self, agent):
        self.agent = agent
        self.name = "AATrainer"
        self.want_to_win = 0.3

    def start_training_session(self, num_of_episodes):
        for episode_num in range(num_of_episodes):
            self.headline_print("start game of episode number {}".format(episode_num + 1))
            self.game_id = rest_api.create_game(self.name).content.decode("utf-8")
            print("Trainer created game with id: {}".format(self.game_id))
            self.tcp = TCP(self.game_id, self.name, self.on_recieved)
            self.start_game_with_agent(self.game_id)
            self.headline_print("end of episode number {}".format(episode_num + 1))

    def start_game_with_agent(self, game_id):
        env = QuoridorEnv(game_id, "Agent")

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

    def headline_print(self, text):
        print("====================================================")
        print(text)
        print("====================================================")

    def on_recieved(self, json_message):
        raise NotImplementedError("This is abstract class you must implement the method!")


class WalkingTrainer(Trainer):

    def on_recieved(self, json_message):
        if json_message["type"] == "NewTurnEvent":
            if json_message["nextPlayerToPlay"] == self.name:
                myLoc = json_message["currentPosition"]
                actions = []
                for move in json_message["avialiableMoves"]:
                    if int(move["x"]) > int(myLoc["x"]):
                        actions.append(3)  # Move Right
                    elif int(move["x"]) < int(myLoc["x"]):
                        actions.append(2)  # Move Left
                    elif int(move["y"]) > int(myLoc["y"]):
                        actions.append(1)  # Move Down
                    elif int(move["y"]) < int(myLoc["y"]):
                        actions.append(0)  # Move Up

                actions_len = len(actions)
                random_i = np.random.randint(0, actions_len)
                act_json = utils.convert_action_to_server(actions[random_i])
                self.tcp.write(act_json)

        elif json_message["type"] == "GameOverEvent":
            pass
        elif json_message["type"] == "RoomStateResponse":
            if len(json_message["players"]) == 2:
                rest_api.start_game(self.game_id)


class RandomTrainer(Trainer):

    def on_recieved(self, json_message):
        if json_message["type"] == "NewTurnEvent":
            if json_message["nextPlayerToPlay"] == self.name:
                actions = utils.convert_moves_to_action_options(json_message)
                choices_len = len(actions)
                random_i = np.random.randint(0, choices_len)
                act_json = utils.convert_action_to_server(actions[random_i])
                self.tcp.write(act_json)

        elif json_message["type"] == "RoomStateResponse":
            if len(json_message["players"]) == 2:
                rest_api.start_game(self.game_id)


class SmartTrainer(Trainer):

    def on_recieved(self, json_message):
        if json_message["type"] == "NewTurnEvent":
            if json_message["nextPlayerToPlay"] == self.name:
                actions = utils.convert_moves_to_action_options(json_message)
                print("Agent position", json_message["secondPlayerPosition"])
                action = SmartTrainer.trainer_move(self, actions, json_message["secondPlayerPosition"]["y"], json_message["secondPlayerPosition"]["x"])
                act_json = utils.convert_action_to_server(action)
                self.tcp.write(act_json)

        elif json_message["type"] == "RoomStateResponse":
            if len(json_message["players"]) == 2:
                rest_api.start_game(self.game_id)

    def trainer_move(self, actions, second_player_position_y, second_player_position_x):
        if self.want_to_win < 0.7:
            self.want_to_win = self.want_to_win + 0.001
        random_choice = np.random.random()
        putWall = True if second_player_position_y > 3 else False
        if putWall and np.random.random() < 0.2 and \
                actions.count(4 + second_player_position_x + (8 * second_player_position_y)) > 0:
            return 4 + second_player_position_x + (8 * second_player_position_y)
        elif putWall and np.random.random() < 0.2 and \
                actions.count(4 - 1 + second_player_position_x + (8 * second_player_position_y)) > 0:
            return 4 - 1 + second_player_position_x + (8 * second_player_position_y)
        elif putWall and np.random.random() < 0.2 and \
                actions.count(4 + 64 + second_player_position_x + (8 * second_player_position_y)) > 0:
            return 4 + 64 + second_player_position_x + (8 * second_player_position_y)
        elif putWall and np.random.random() < 0.2 and \
                actions.count(4 + 64 - 1 + second_player_position_x + (8 * second_player_position_y)) > 0:
            return 4 + 64 - 1 + second_player_position_x + (8 * second_player_position_y)
        elif np.random.random() < 0.3:
            choices_len = len(actions)
            random_i = np.random.randint(0, choices_len)
            return actions[random_i]
        elif len(actions) > 4:
            if random_choice < self.want_to_win and (actions[0] == 0 or actions[1] == 0 or actions[2] == 0 or actions[3] == 0):
                return 0
            elif np.random.random() < 0.5 and (actions[0] == 2 or actions[1] == 2 or actions[2] == 2 or actions[3] == 2):
                return 2
            elif actions[0] == 3 or actions[1] == 3 or actions[2] == 3 or actions[3] == 3:
                return 3
            else:
                return 1
        return actions[0]
