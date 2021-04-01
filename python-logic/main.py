import requests
import logging
import gym
import quoridor_env
from quoridor_env import QuoridorEnv
from model import Model
from agent import Agent
from tcp import TCP
from globals import Global
from trainer import Trainer
import costum_agent
from datetime import datetime

logging.basicConfig(level=logging.INFO)

serverUrl = Global.server


def join_game_random_player(game_id):
    join_game_url = serverUrl + "/JoinGame/" + game_id + "/randomPlayer"
    response = requests.get(join_game_url)

    return response


if __name__ == '__main__':
    # game_id = "edf2c4b2-95b4-4abf-829f-8920c357c13e"
    # env = QuoridorEnv(game_id, "team600")

    # done = False
    # stam = True
    # while not done:
    #     if stam:
    #         board, reward, done, info = env.step(0)
    #         stam = not stam
    #     else:
    #         board, reward, done, info = env.step(1)
    #         stam = not stam
    #     print("reward:{} , done:{}".format(reward, done))
    #     print(board)
    #     print("*************************")

    model = costum_agent.Model(quoridor_env.observation_shape(), Global.num_of_actions)
    agent = costum_agent.Agent(model.model)
    trainer = Trainer(agent)
    trainer.start_training_session(50)

    time = datetime.now().strftime("%d-%m-%Y-%H:%M:%S")
    saved_file_name = "quoridor-{time}.h5".format(time=time)
    agent.save_model(saved_file_name)