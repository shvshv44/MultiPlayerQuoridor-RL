
import rest_api
from quoridor_env import QuoridorEnv


class HumanTrainer:

    def __init__(self, agent):
        self.agent = agent

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
