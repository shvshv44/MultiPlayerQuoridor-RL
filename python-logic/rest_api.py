import requests as http

from globals import Global

server = Global.server


def create_game(name):
    create_game_url = server + "/CreateGame/" + name
    response = http.get(create_game_url)
    return response


def get_board(game_id):
    get_board_url = server + "/BoardStatus/" + game_id
    response = http.get(get_board_url)
    return response


def join_game(game_id, name):
    join_url = server + "/JoinGame/" + game_id + "/" + name
    response = http.get(join_url)
    return response


def start_game(game_id):
    start_url = server + "/StartGame/" + game_id
    response = http.get(start_url)
    return response


def start_generated_game(game_id, max_steps):
    start_url = server + "/StartGeneratedGame/{}/{}".format(game_id, max_steps)
    response = http.get(start_url)
    return response


def train_vs_agent(game_id):
    vs_url = Global.vs_server + "/AgentVsAgent2/" + game_id
    response = http.get(vs_url)
    return response


def save_agent_2():
    save_url = Global.vs_server + "/AgentVsAgent2/Save"
    response = http.get(save_url)
    return response
