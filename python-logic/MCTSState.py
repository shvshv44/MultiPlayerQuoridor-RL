import json

import rest_api


def hash_dict(dictionary: dict):
    h = 0
    for key in dictionary.keys():
        if dictionary[key] is dict:
            cur = hash_dict(dictionary[key])
        else:
            cur = hash(dictionary[key])
        h = h ^ cur
    return h


class Board:
    def __init__(self, board_dictionary: dict):
        self.board_dictionary = board_dictionary

    def __hash__(self):
        return hash_dict(self.board_dictionary)

    def __getitem__(self, item):
        if item == "board":
            return self.board_dictionary

        self.board_dictionary.__getitem__(item)


# noinspection PyDictCreation
def transform_physical_board_to_input_board(physical_board, p1_turn):
    input_board = {}
    input_board["p1UUID"] = physical_board["pawns"][0]
    input_board["p2UUID"] = physical_board["pawns"][1]
    input_board["p1Pos"] = physical_board["allPawnPosition"][input_board["p1UUID"]["uuid"]]
    input_board["p2Pos"] = physical_board["allPawnPosition"][input_board["p2UUID"]["uuid"]]
    input_board["walls"] = physical_board["walls"]
    input_board["p1Walls"] = physical_board["pawnWalls"][input_board["p1UUID"]["uuid"]]
    input_board["p2Walls"] = physical_board["pawnWalls"][input_board["p2UUID"]["uuid"]]
    input_board["p1Turn"] = not p1_turn
    input_board["p1EndLine"] = physical_board["pawnEndLine"][input_board["p1UUID"]["uuid"]]
    input_board["p2EndLine"] = physical_board["pawnEndLine"][input_board["p2UUID"]["uuid"]]
    return Board(input_board)


class Action:
    def __init__(self, data):
        self.data = data

    def __hash__(self):
        if "wall" in self.data:
            return hash(self.data["wall"]["position"]["x"]) ^ hash(self.data["wall"]["position"]["y"]) ^ hash(
                self.data["wall"]["wallDirection"])
        return 0


class MCTSState:

    def __init__(self, board):
        self.board = board

    def getPossibleActions(self):
        next_moves = rest_api.fetch_next_available_moves(self.board)
        content = next_moves.content.decode("utf8")
        content = json.loads(content)
        actions = []
        for action in content:
            actions.append(Action(action))
        return actions

    def takeAction(self, action):
        response = rest_api.take_action(self.board, action.data).content.decode("utf8")
        response = json.loads(response)
        return MCTSState(transform_physical_board_to_input_board(response, not self.board["board"]["p1Turn"]))

    def isTerminal(self):
        response = rest_api.fetch_winner(self.board)
        return response.status_code != 404

    def getReward(self):
        response = rest_api.fetch_winner(self.board)
        if response.text == self.board["board"]["p1Pos"]:
            return 100
        else:
            return -100

    def __hash__(self):
        return self.board.__hash__()
