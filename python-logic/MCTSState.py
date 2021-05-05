import json

import rest_api


# noinspection PyDictCreation
def transform_physical_board_to_input_board(physical_board, p1_turn):
    input_board = {}
    input_board["p1UUID"] = physical_board["pawns"][0]
    input_board["p2UUID"] = physical_board["pawns"][1]
    input_board["p1Pos"] = physical_board["allPawnPosition"][input_board["p1UUID"]]
    input_board["p2Pos"] = physical_board["allPawnPosition"][input_board["p2UUID"]]
    input_board["walls"] = physical_board["walls"]
    input_board["p1Walls"] = physical_board["pawnWalls"][input_board["p1UUID"]]
    input_board["p2Walls"] = physical_board["pawnWalls"][input_board["p2UUID"]]
    input_board["p1Turn"] = not p1_turn
    input_board["p1EndLine"] = physical_board["pawnEndLine"][input_board["p1UUID"]]
    input_board["p2EndLine"] = physical_board["pawnEndLine"][input_board["p2UUID"]]
    return input_board


class MCTSState:

    def __init__(self, board):
        self.board = board

    def getPossibleActions(self):
        next_moves = rest_api.fetch_next_available_moves(self.board)
        next_moves["content"] = next_moves.content.decode("utf8")
        next_moves["content"] = json.loads(next_moves["content"])
        return next_moves

    def takeAction(self, action):
        response = rest_api.take_action(self.board, action).content.decode("utf8")
        response = json.loads(response)
        return MCTSState(transform_physical_board_to_input_board(response, self.board["p1Turn"]))

    def isTerminal(self):
        response = rest_api.fetch_winner(self.board)
        return response.status_code != 404

    def getReward(self):
        response = rest_api.fetch_winner(self.board)
        if response.text == self.board["board"]["p1Pos"]:
            return 100
        else:
            return -100
