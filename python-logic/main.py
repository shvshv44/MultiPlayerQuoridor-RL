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
model = costum_agent.Model(quoridor_env.observation_shape(), Global.num_of_actions)


@app.route('/addAgentToGame/<game_id_to_join>', methods=['GET'])
def add_agent_to_game(game_id_to_join):
    agent = costum_agent.Agent(model.model)
    auto_agent = AutoAgent(agent)

    auto_agent.join_game(game_id_to_join)
    return "Add agent to game id " + game_id_to_join


@app.route('/trainAgent/<num_of_games>', methods=['GET'])
def add_agent_to_game(num_of_games):
    agent = costum_agent.Agent(model.model)
    trainer = Trainer(agent)
    trainer.start_training_session(num_of_games)
    agent.save_model()
    return "Training Finished!"


@app.route('/saveModel', methods=['GET'])
def save_model():
    agent = costum_agent.Agent(model.model)
    agent.save_model()
    return "Model Saved!"


@app.route('/saveModel/<file_name>', methods=['GET'])
def save_model_to_file(file_name):
    agent = costum_agent.Agent(model.model)
    agent.save_model_to_path(file_name)
    return "Model Saved to {}".format(file_name)


if __name__ == '__main__':
    app.run(host="127.0.0.1", port=8000)
