import threading
import time

import rest_api
from quoridor_env import QuoridorEnv


class CompetitiveAgentTrainer:

    def __init__(self, agent):
        self.agent = agent
        self.name = "Agent"
        self.done_game = False

    def start_training_session(self, num_of_episodes):
        for episode_num in range(num_of_episodes):
            self.headline_print("start game of episode number {}".format(episode_num + 1))
            self.game_id = rest_api.create_game(self.name).content.decode("utf-8")
            print("Agent Trainer created game with id: {}".format(self.game_id))

            join_competitive_agent_thr = threading.Thread(target=self.join_competitive_agent, args=[self.game_id])
            join_competitive_agent_thr.start()

            self.start_game_with_agent(self.game_id)

            while not self.done_game:
                pass
            self.headline_print("end of episode number {}".format(episode_num + 1))

    def start_game_with_agent(self, game_id):
        env = QuoridorEnv(game_id, self.name, False)

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

        self.done_game = True
        print("\n\nGAME FINISHED IN {} STEPS!\n\n".format(steps_num))

    def headline_print(self, text):
        print("====================================================")
        print(text)
        print("====================================================")

    def join_competitive_agent(self, game_id):
        rest_api.join_competitive_agent(self.game_id).content.decode("utf-8")
