import rest_api
from quoridor_env import QuoridorEnv
import threading
import time


class AgentTrainer:

    def __init__(self, agent, agent_trainer):
        self.agent = agent
        self.agent_trainer = agent_trainer
        self.name = "Trainer"
        self.done_game = False

    def start_training_session(self, num_of_episodes):
        for episode_num in range(num_of_episodes):
            self.headline_print("start game of episode number {}".format(episode_num + 1))
            self.game_id = rest_api.create_game(self.name).content.decode("utf-8")
            print("Agent Trainer created game with id: {}".format(self.game_id))

            rest_api.join_game(self.game_id, "Agent").content.decode("utf-8")

            thr = threading.Thread(target=self.start_game_with_trainer, args=[self.game_id])
            thr.start()

            self.start_game_with_agent(self.game_id)

            while not self.done_game:
                pass
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

        self.done_game = True
        print("\n\nGAME FINISHED IN {} STEPS!\n\n".format(steps_num))

    def start_game_with_trainer(self, game_id):
        start_game_thr = threading.Thread(target=self.start_game, args=[self.game_id])
        start_game_thr.start()
        env_trainer = QuoridorEnv(game_id, self.name)

        cur_state_trainer = env_trainer.reset()

        done = False
        steps_num = 0

        if env_trainer.is_my_turn:
            action_trainer = self.agent.predicated_act(cur_state_trainer, env_trainer)
            new_state, reward, done, _ = env_trainer.step(action_trainer)
            cur_state_trainer = new_state

        while not done:
            steps_num += 1
            action_trainer = self.agent.predicated_act(cur_state_trainer, env_trainer)
            new_state, reward, done, _ = env_trainer.step(action_trainer)
            cur_state_trainer = new_state

    def start_game(self, game_id):
        time.sleep(3)
        rest_api.start_game(self.game_id)

    def headline_print(self, text):
        print("====================================================")
        print(text)
        print("====================================================")
