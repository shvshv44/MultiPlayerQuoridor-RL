import logging
from datetime import datetime

# do not delete imports at any cost!
from agent import Agent
from model import Model

from auto_agent import AutoAgent
from globals import Global
from trainer import Trainer
from human_trainer import HumanTrainer
from history_trainer import HistoryTrainer
import costum_agent
import keras
import rest_api

from bottle import route, run

logging.basicConfig(level=logging.INFO)
serverUrl = Global.server


def load_model(file):
    if file == "":
        return costum_agent.Model().model
    else:
        return keras.models.load_model("./models/{}".format(file))


model = load_model("")


@route('/AddAgentToGame/<game_id_to_join>', methods=['GET'])
def add_agent_to_game(game_id_to_join):
    global model
    print("adding agent to game id {}".format(game_id_to_join))
    agent = costum_agent.Agent(model)
    auto_agent = AutoAgent(agent)
    auto_agent.join_game(game_id_to_join)
    return "Add agent to game id " + game_id_to_join


@route('/TrainAgent/<episodes>', methods=['GET'])
def train_agent(episodes):
    global model
    agent = costum_agent.Agent(model)
    trainer = Trainer(agent)
    trainer.start_training_session(int(episodes))

    return "Trained Successfully!"


@route('/TrainAgentByHuman/<game_id>', methods=['GET'])
def train_agent_human(game_id):
    global model
    agent = costum_agent.Agent(model)
    trainer = HumanTrainer(agent)
    trainer.start_game_with_agent(game_id)
    return "Trained By Human Successfully!"


@route('/TrainAgentByHistory', methods=['GET'])
def train_agent_history():
    global model
    agent = costum_agent.Agent(model)
    trainer = HistoryTrainer(agent)
    trainer.start()
    return "Trained By Human Successfully!"


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


if __name__ == '__main__':
    run(host='0.0.0.0', port=8000, debug=True)
