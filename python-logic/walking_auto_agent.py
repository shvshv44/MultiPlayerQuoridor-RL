import rest_api
from walking_quoridor_env import QuoridorEnv
import utils


class AutoAgent:
    def __init__(self, agent):
        self.agent = agent
        self.name = "AutoAgent"

    def join_game(self, game_id):
        self.game_id = game_id
        rest_api.join_game(self.game_id, self.name).content.decode("utf-8")
        self.start_game_with_agent(self.game_id)

    def start_game_with_agent(self, game_id):
        env = QuoridorEnv(game_id, self.name)

        done = False
        steps_num = 0
        cur_state = env.reset()

        while not done:
            steps_num += 1
            action = self.agent.predicated_act(cur_state, env)
            new_state, reward, done, _ = env.step(action)
            cur_state = new_state

        print("\n\nGAME FINISHED IN {} STEPS!\n\n".format(steps_num))

