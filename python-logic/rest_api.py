import requests as http
from flask import Flask

from globals import Global
import time

server = Global.server

app = Flask(__name__)


class API():
    game_id = ""

    def __init__(self):
        app.run(host="127.0.0.1", port=8000)

    def wait_to_adding_game(self):
        while self.game_id == "":
            time.sleep(0.1)
        return self.game_id

    @app.route('/addAgentToGame/<game_id_to_join>', methods=['GET'])
    def add_agent_to_game(game_id_to_join):
        game_id = game_id_to_join
        return "Add agent to game id " + game_id


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
