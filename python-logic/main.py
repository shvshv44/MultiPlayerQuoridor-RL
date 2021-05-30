import logging
from datetime import datetime

# do not delete imports at any cost!
from agent import Agent
from model import Model

from auto_agent import AutoAgent
import numpy as np
import threading
from globals import Global
from trainer import WalkingTrainer, RandomTrainer
from human_trainer import HumanTrainer
import costum_agent
import better_costum_agent
import better_trainer
import better_auto_agent
import better_manager
import keras
import rest_api
import sys
import walking_costum_agent
import walking_trainer
from matplotlib import pyplot
import os

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


def load_walking_model(file):
    if file == "":
        return walking_costum_agent.Model().model
    else:
        return keras.models.load_model("./walking_models/{}".format(file))


model = load_better_model("")
walking_model = load_walking_model("")


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

@route('/AgentVsAgent1/<episodes>', methods=['GET'])
def ava1(episodes):
    global model
    agent1 = better_costum_agent.Agent(model, "Agent1")
    manager1 = better_manager.Manager(agent1)
    num_of_epochs = int(episodes)

    for i in range(0, num_of_epochs):
        print("game number {} has been started!".format(i))
        game_id = rest_api.create_game(agent1.name).content.decode("utf-8")
        threading.Thread(target=rest_api.train_vs_agent, args=(game_id,)).start()
        manager1.start_game_with_agent(game_id, False)
        print("game number {} has been finished!".format(i))

        if (i + 1) % 50 == 0:
            agent_1_save()
            rest_api.save_agent_2()

    print("ALL GAMES FINISHED! SAVING BOTH MODELS!")
    agent_1_save()
    rest_api.save_agent_2()

    return "Trained Successfully!"


@route('/AgentVsAgent2/<game_id>', methods=['GET'])
def ava2(game_id):
    print("Agent1 asked Agent2 to start a game!")
    global model
    agent2 = better_costum_agent.Agent(model, "Agent2")
    manager2 = better_manager.Manager(agent2)
    manager2.start_game_with_agent(game_id, True)

    return "Trained Successfully!"

@route('/AgentVsAgent2/Save', methods=['GET'])
def agent_2_save():
    global model
    time = datetime.now().strftime("%d_%m_%Y__%H_%M_%S")
    saved_file_name = "./models2/quoridor2_{}.h5".format(time)
    model.save(saved_file_name)
    return "Model Saved In {}".format(saved_file_name)


def agent_1_save():
    global model
    time = datetime.now().strftime("%d_%m_%Y__%H_%M_%S")
    saved_file_name = "./models1/quoridor1_{}.h5".format(time)
    model.save(saved_file_name)
    return "Model Saved In {}".format(saved_file_name)


@route('/SaveWalkingModel', methods=['GET'])
def save_walking_model():
    global walking_model
    time = datetime.now().strftime("%d_%m_%Y__%H_%M_%S")
    saved_file_name = "./walking_models/walking_{}.h5".format(time)
    walking_model.save(saved_file_name)
    return "Walking Model Saved In {}".format(saved_file_name)


@route('/TrainWalkingAgent/<episodes>/<max_steps>', methods=['GET'])
def train_walking_agent(episodes, max_steps):
    global walking_model
    agent = walking_costum_agent.Agent(walking_model)
    trainer = walking_trainer.RandomWalkingTrainer(agent, int(max_steps))
    history = trainer.start_training_session(int(episodes))

    time = datetime.now().strftime("%d_%m_%Y__%H_%M_%S")
    pyplot.plot(history)

    if not os.path.exists('./walking_metrics/'):
        os.makedirs('./walking_metrics/')

    pyplot.savefig('./walking_metrics/walking_loss_{}.png'.format(time))

    return "Trained Successfully!"


@route('/TrainWalkingAgentForEver/<episodes>/<max_steps>', methods=['GET'])
def train_walking_agent_for_ever(episodes, max_steps):
    while True:
        global walking_model
        num_of_episodes = int(episodes)
        train_walking_agent(num_of_episodes, max_steps)
        print("Finished {} iterations, saving model!".format(num_of_episodes))
        save_walking_model()
        print("Model Saved!")


@route('/LoadWalkingModel/<file_name>', methods=['GET'])
def load_walking_model_to_path(file_name):
    global walking_model
    walking_model = keras.models.load_model("./walking_models/{}".format(file_name))
    return "Model Loaded From {}".format(file_name)



if __name__ == '__main__':
    run(host='0.0.0.0', port=int(sys.argv[1]), debug=True)
