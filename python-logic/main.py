import requests
import logging

from TCP import TCP

logging.basicConfig(level=logging.INFO)

serverUrl = "http://localhost:8080"


def join_game_random_player(game_id):
    join_game_url = serverUrl + "/JoinGame/" + game_id + "/randomPlayer"
    response = requests.get(join_game_url)

    return response


if __name__ == '__main__':
    # game_id = "e166a63d-9615-439b-9d18-5d8b4559ef94"
    # join_game_random_player(game_id)
    tcp = TCP()
