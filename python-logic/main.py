import logging

import requests
from flask import Flask

import quoridor_env
from auto_agent import AutoAgent
from agent import Agent
from globals import Global
from model import Model
from trainer import Trainer
import costum_agent


logging.basicConfig(level=logging.INFO)

serverUrl = Global.server
app = Flask(__name__)


@app.route('/addAgentToGame/<game_id_to_join>', methods=['GET'])
def add_agent_to_game(game_id_to_join):
    model = costum_agent.Model(quoridor_env.observation_shape(), Global.num_of_actions)
    agent = costum_agent.Agent(model.model)
    auto_agent = AutoAgent(agent)

    auto_agent.join_game(game_id_to_join)
    return "Add agent to game id " + game_id_to_join


def join_game_random_player(game_id):
    join_game_url = serverUrl + "/JoinGame/" + game_id + "/randomPlayer"
    response = requests.get(join_game_url)

    return response


if __name__ == '__main__':

    is_trainer_create_game = True

    if is_trainer_create_game:
        model = costum_agent.Model(quoridor_env.observation_shape(), Global.num_of_actions)
        agent = costum_agent.Agent(model.model)
        trainer = Trainer(agent)
        trainer.start_training_session(50)
        agent.save_model()

    app.run(host="127.0.0.1", port=8000)


