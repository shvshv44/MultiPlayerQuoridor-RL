import requests
import logging
import gym
from quoridor_env import QuoridorEnv, Player
from model import Model
from agent import Agent

from tcp import TCP

logging.basicConfig(level=logging.INFO)

serverUrl = "http://localhost:8080"


def join_game_random_player(game_id):
    join_game_url = serverUrl + "/JoinGame/" + game_id + "/randomPlayer"
    response = requests.get(join_game_url)

    return response


if __name__ == '__main__':
    # game_id = "e166a63d-9615-439b-9d18-5d8b4559ef94"
    # join_game_random_player(game_id)
    # tcp = TCP()

    players = [
        Player(0, 4, "red", [72, 73, 74, 75, 76, 77, 78, 79, 80]),
        Player(1, 76, "yellow", [0, 1, 2, 3, 4, 5, 6, 7, 8])
    ]

    env = QuoridorEnv(players, 1)
    model = Model(env)
    Agent = Agent(model)

    board, reward, done, info = env.step((1, 0, 65))
    print("board =", board)
    print("reward =", reward)
    print("done =", done)
    print("info =", info)
    board, reward, done, info = env.step((1, 0, 61))
    print("board =", board)
    print("reward =", reward)
    print("done =", done)
    print("info =", info)

    env.step((0, 0, 0))
    env.step((0, 2, 0))
    env.print_board()

    board, reward, done, info = env.step((0, 0, 0))
    print("board =", board)
    print("reward =", reward)
    print("done =", done)
    print("info =", info)
    board, reward, done, info = env.step((0, 1, 0))
    print("board =", board)
    print("reward =", reward)
    print("done =", done)
    print("info =", info)
    board, reward, done, info = env.step((0, 2, 0))
    print("board =", board)
    print("reward =", reward)
    print("done =", done)
    print("info =", info)
    board, reward, done, info = env.step((0, 3, 0))
    print("board =", board)
    print("reward =", reward)
    print("done =", done)
    print("info =", info)

    for i in range(7):
        print("***********************************")
        board, reward, done, info = env.step((0, 0, 0))
        print("board =", board)
        print("reward =", reward)
        print("done =", done)
        print("info =", info)
        env.print_board()
        print("***********************************")