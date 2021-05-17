import logging
from datetime import datetime

# do not delete imports at any cost!
from agent import Agent
from model import Model

from auto_agent import AutoAgent
import numpy as np
from globals import Global
from trainer import WalkingTrainer, RandomTrainer
from human_trainer import HumanTrainer
import costum_agent
import better_costum_agent
import better_trainer
import better_auto_agent
import keras

from bottle import route, run

logging.basicConfig(level=logging.INFO)
serverUrl = Global.server


def load_model(file):
    if file == "":
        return costum_agent.Model().model
    else:
        return keras.models.load_model("./models/{}".format(file))

def load_better_model(file):
    if file == "":
        return better_costum_agent.Model().model
    else:
        return keras.models.load_model("./models/{}".format(file))


model = load_better_model("")


@route('/AddAgentToGame/<game_id_to_join>', methods=['GET'])
def add_agent_to_game(game_id_to_join):
    global model
    print("adding agent to game id {}".format(game_id_to_join))
    agent = better_costum_agent.Agent(model)
    auto_agent = better_auto_agent.AutoAgent(agent)
    # agent = costum_agent.Agent(model)
    # auto_agent = AutoAgent(agent)
    auto_agent.join_game(game_id_to_join)
    return "Add agent to game id " + game_id_to_join


@route('/TrainAgent/Walking/<episodes>', methods=['GET'])
def train_agent(episodes):
    global model
    agent = costum_agent.Agent(model)
    trainer = WalkingTrainer(agent)
    trainer.start_training_session(int(episodes))

    return "Trained Successfully!"


@route('/TrainAgent/Random/<episodes>', methods=['GET'])
def train_agent(episodes):
    global model
    agent = costum_agent.Agent(model)
    trainer = RandomTrainer(agent)
    trainer.start_training_session(int(episodes))

    return "Trained Successfully!"


@route('/TrainAgentByHuman/<game_id>', methods=['GET'])
def train_agent_human(game_id):
    global model
    agent = costum_agent.Agent(model)
    trainer = HumanTrainer(agent)
    trainer.start_game_with_agent(game_id)
    return "Trained By Human Successfully!"


@route('/TrainBetterAgent/Random/<episodes>', methods=['GET'])
def train_better_agent(episodes):
    global model
    agent = better_costum_agent.Agent(model)
    trainer = better_trainer.RandomTrainer(agent)
    trainer.start_training_session(int(episodes))

    return "Trained Successfully!"


@route('/TrainBetterAgent/Smart/<episodes>', methods=['GET'])
def train_better_agent_smart(episodes):
    global model
    agent = better_costum_agent.Agent(model)
    trainer = better_trainer.SmartTrainer(agent)
    trainer.start_training_session(int(episodes))

    return "Trained Successfully!"


@route('/SaveModel', methods=['GET'])
def save_model():
    global model
    time = datetime.now().strftime("%d_%m_%Y__%H_%M_%S")
    saved_file_name = "./models/quoridor_{}.h5".format(time)
    model.save(saved_file_name)
    return "Model Saved In {}".format(saved_file_name)


@route('/SaveModel/<file_name>', methods=['GET'])
def save_model_to_path(file_name):
    global model
    model.save(file_name)
    return "Model Saved In {}".format(file_name)


@route('/LoadModel/<file_name>', methods=['GET'])
def load_model_to_path(file_name):
    global model
    model = keras.models.load_model("./models/{}".format(file_name))
    return "Model Loaded From {}".format(file_name)


@route('/TrainAllNight', methods=['GET'])
def train_all_night():
    global model
    agent = better_costum_agent.Agent(model)
    trainer = better_trainer.RandomTrainer(agent)

    while True:
        trainer.start_training_session(200)
        save_model()

    return "Trained Successfully!"


if __name__ == '__main__':
    run(host='0.0.0.0', port=8000, debug=True)
