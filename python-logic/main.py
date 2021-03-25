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
    game_id = "3c6edf17-3b3f-4b4c-b466-52a1df76c79e"
    # join_game_random_player(game_id)

    env = QuoridorEnv(game_id, "team600")
    # model = Model(env)
    # Agent = Agent(model)
    # Agent.train_agent(env)

    done = False
    while not done:
        board, reward, done, info = env.step(3)
        print("reward:{} , done:{}".format(reward, done))
        print(board)
        print("*************************")
