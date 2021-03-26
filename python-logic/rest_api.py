import requests as http
from globals import Global

server = "http://localhost:8080"


def get_board(game_id):
    get_board_url = server + "/BoardStatus/" + game_id
    response = http.get(get_board_url)
    return response


def join_game(game_id, name):
    join_url = server + "/JoinGame/" + game_id + "/" + name
    response = http.get(join_url)
    return response
