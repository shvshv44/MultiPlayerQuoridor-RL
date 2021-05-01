import threading
import time

import rest_api
from quoridor_env import QuoridorEnv


class AutoAgent:
    def __init__(self, agent):
        self.agent = agent
        self.name = "AutoAgent"

    def join_game(self, game_id):
        self.game_id = game_id
        rest_api.join_game(self.game_id, self.name).content.decode("utf-8")
        self.start_game_with_agent(self.game_id)


    def join_game_and_start(self, game_id):
        self.game_id = game_id
        rest_api.join_game(self.game_id, self.name).content.decode("utf-8")
        start_game_thr = threading.Thread(target=self.start_game, args=[self.game_id])
        start_game_thr.start()
        self.start_game_with_agent(self.game_id)

    def start_game_with_agent(self, game_id):
        env = QuoridorEnv(game_id, self.name, False)

        done = False
        steps_num = 0
        cur_state = env.reset()

        while not done:
            steps_num += 1
            action = self.agent.predicated_act(cur_state, env)
            new_state, reward, done, _ = env.step(action)
            cur_state = new_state

        print("\n\nGAME FINISHED IN {} STEPS!\n\n".format(steps_num))

    def start_game(self, game_id):
        time.sleep(2)
        rest_api.start_game(game_id)

