import logging
from datetime import datetime

import requests
from flask import Flask
from flask_injector import FlaskInjector

# do not delete imports at any cost!
from agent import Agent
from model import Model

import quoridor_env
from auto_agent import AutoAgent
from dependencies import configure
from globals import Global
from trainer import Trainer
import costum_agent

logging.basicConfig(level=logging.INFO)

serverUrl = Global.server
app = Flask(__name__)


@app.route('/AddAgentToGame/<game_id_to_join>', methods=['GET'])
def add_agent_to_game(model: costum_agent.Model, game_id_to_join):
    agent = costum_agent.Agent(model.model)
    auto_agent = AutoAgent(agent)

    auto_agent.join_game(game_id_to_join)
    return "Add agent to game id " + game_id_to_join


@app.route('/TrainAgent/<episodes>', methods=['GET'])
def train_agent(model: costum_agent.Model, episodes):
    agent = costum_agent.Agent(model.model)
    trainer = Trainer(agent)
    trainer.start_training_session(int(episodes))
    return "Trained Successfully!"


@app.route('/SaveModel', methods=['GET'])
def save_model(model: costum_agent.Model):
    time = datetime.now().strftime("%d_%m_%Y__%H_%M_%S")
    saved_file_name = "./models/quoridor_{}.h5".format(time)
    model.model.save(saved_file_name)
    return "Model Saved In {}".format(saved_file_name)


@app.route('/SaveModel/<file_name>', methods=['GET'])
def save_model_to_path(model: costum_agent.Model, file_name):
    model.model.save(file_name)
    return "Model Saved In {}".format(file_name)


# Setup Flask Injector, this has to happen AFTER routes are added
FlaskInjector(app=app, modules=[configure])

if __name__ == '__main__':
    app.run(host="127.0.0.1", port=8000)
