import requests as http

from globals import Global

server = Global.server
competitive_agent = Global.competitive_agent


def create_game(name):
    create_game_url = server + "/CreateGame/" + name
    response = http.get(create_game_url)
    return response


def get_board(game_id):
    get_board_url = server + "/BoardStatus/" + game_id
    response = http.get(get_board_url)
    return response


def get_mcts_board(game_id):
    get_board_url = server + "/BoardStatusMCTS/" + game_id
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


def history_game(game_id):
    start_url = server + "/History/" + game_id
    response = http.get(start_url)
    return response.content.decode("utf-8")


def get_history_game_ids():
    start_url = server + "/HistoryIds"
    response = http.get(start_url)
    return response.content.decode("utf-8")


def join_competitive_agent(game_id):
    start_url = competitive_agent + "/AddCompetitiveAgentToGame/" + game_id
    response = http.get(start_url)
    return response.content.decode("utf-8")


def fetch_next_available_moves(board):
    start_url = server + "/Generate/AvailableMoves"
    response = http.post(start_url, json=board["board"])
    return response


def take_action(board, action):
    start_url = server + "/Generate/NextAction"
    response = http.post(start_url, json={"board": board["board"], "action": action})
    return response


def fetch_winner(board):
    start_url = server + "/Generate/CheckWinner"
    response = http.post(start_url, json=board["board"])
    return response
