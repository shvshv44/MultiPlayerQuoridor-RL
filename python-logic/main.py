import logging

import requests
from flask import Flask

import quoridor_env
from auto_agent import AutoAgent
from agent import Agent
from globals import Global
from model import Model
from trainer import Trainer

logging.basicConfig(level=logging.INFO)

serverUrl = Global.server
app = Flask(__name__)


@app.route('/addAgentToGame/<game_id_to_join>', methods=['GET'])
def add_agent_to_game(game_id_to_join):
    model = Model(quoridor_env.observation_shape(), Global.num_of_actions)
    agent = Agent(model)
    auto_agent = AutoAgent(agent)

    auto_agent.join_game(game_id_to_join)
    return "Add agent to game id " + game_id_to_join

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

    is_trainer_create_game = False

    model = Model(quoridor_env.observation_shape(), Global.num_of_actions)
    agent = Agent(model)
    trainer = Trainer(agent)

    if is_trainer_create_game:
        trainer.start_training_session()

    app.run(host="127.0.0.1", port=8000)


