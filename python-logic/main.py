import logging
import threading

import requests
from flask import Flask
import quoridor_env
import rest_api
from agent import Agent
from globals import Global
from model import Model
from trainer import Trainer

logging.basicConfig(level=logging.INFO)

serverUrl = Global.server

def join_game_random_player(game_id):
    join_game_url = serverUrl + "/JoinGame/" + game_id + "/randomPlayer"
    response = requests.get(join_game_url)

    return response


def startTrainer(is_trainer_create_game, api):
    if is_trainer_create_game:
        trainer.start_training_session()
    else:
        trainer.wait_join_game(api)


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

    is_trainer_create_game = False

    model = Model(quoridor_env.observation_shape(), Global.num_of_actions)
    agent = Agent(model)
    trainer = Trainer(agent)

    api = rest_api.API()

    logging.INFO("Init the api")

    startTrainer(is_trainer_create_game, api)

    logging.INFO("Start the trainer")



