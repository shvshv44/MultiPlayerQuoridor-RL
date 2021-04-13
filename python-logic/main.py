import logging

import requests
from flask import Flask

import quoridor_env
from auto_agent import AutoAgent
from globals import Global
from trainer import Trainer
from costum_agent import Model
from costum_agent import Agent
from flask_cors import CORS

logging.basicConfig(level=logging.INFO)

serverUrl = Global.server
app = Flask(__name__)
CORS(app)
model = Model(quoridor_env.observation_shape(), Global.num_of_actions)


@app.route('/AddAgentToGame/<game_id_to_join>', methods=['GET'])
def add_agent_to_game(game_id_to_join):
    agent = Agent(model.model)
    auto_agent = AutoAgent(agent)

    auto_agent.start_game_with_agent(game_id_to_join)
    return "Add agent to game id " + game_id_to_join


@app.route('/TrainAgent/<num_of_games>', methods=['GET'])
def train_agent(num_of_games):
    agent = Agent(model.model)
    trainer = Trainer(agent)
    trainer.start_training_session(int(num_of_games))
    return "Training Finished!"


@app.route('/SaveModel', methods=['GET'])
def save_model():
    agent = Agent(model.model)
    agent.save_model()
    return "Model Saved!"


@app.route('/SaveModel/<file_name>', methods=['GET'])
def save_model_to_file(file_name):
    agent = Agent(model.model)
    agent.save_model_to_path(file_name)
    return "Model Saved to {}".format(file_name)


if __name__ == '__main__':
    #app.run(host="127.0.0.1", port=8000)
    agent = Agent(model.model)
    trainer = Trainer(agent)
    trainer.start_training_session(int(5))
