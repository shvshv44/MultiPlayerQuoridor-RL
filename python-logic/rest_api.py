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
