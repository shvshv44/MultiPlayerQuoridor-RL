import requests as http
from globals import Global


class RestApi:
    def get_board(self, gameId):
        get_board_url = Global.server + "/GetBoard/" + gameId
        response = http.get(get_board_url)
        return response