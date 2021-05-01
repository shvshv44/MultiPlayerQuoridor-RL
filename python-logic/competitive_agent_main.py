import logging
from datetime import datetime

import rest_api

from auto_agent import AutoAgent
from globals import Global
import costum_agent
import keras

from bottle import route, run

logging.basicConfig(level=logging.INFO)
serverUrl = Global.server


def load_model(file):
    if file == "":
        return costum_agent.Model().model
    else:
        return keras.models.load_model("./models/{}".format(file))


model = load_model("")


@route('/AddCompetitiveAgentToGame/<game_id_to_join>', methods=['GET'])
def add_agent_to_game(game_id_to_join):
    global model
    print("adding agent to game id {}".format(game_id_to_join))
    agent = costum_agent.Agent(model)
    agent.model.load_weights('./models/quoridor.h5')
    auto_agent = AutoAgent(agent)
    auto_agent.join_game_and_start(game_id_to_join)
    return "Add agent to game id " + game_id_to_join


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
    run(host='localhost', port=8001, debug=True)
