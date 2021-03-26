import requests
import logging
import gym
from quoridor_env import QuoridorEnv
from model import Model
from agent import Agent

from tcp import TCP

logging.basicConfig(level=logging.INFO)

serverUrl = "http://localhost:8080"


def join_game_random_player(game_id):
    join_game_url = serverUrl + "/JoinGame/" + game_id + "/randomPlayer"
    response = requests.get(join_game_url)

    return response


def get_game_id():
    join_game_url = serverUrl + "/JoinGame/" + game_id + "/randomPlayer"
    response = requests.get(join_game_url)

    return response


if __name__ == '__main__':
    game_id = "edf2c4b2-95b4-4abf-829f-8920c357c13e"
    # join_game_random_player(game_id)

    env = QuoridorEnv(game_id, "team600")
    # model = Model(env)
    # Agent = Agent(model)
    # Agent.train_agent(env)

    done = False
    stam = True
    while not done:
        if stam:
            board, reward, done, info = env.step(0)
            stam = not stam
        else:
            board, reward, done, info = env.step(1)
            stam = not stam
        print("reward:{} , done:{}".format(reward, done))
        print(board)
        print("*************************")
