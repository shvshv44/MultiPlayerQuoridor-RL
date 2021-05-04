import rest_api


class MCTSState:
    def __init__(self, board):
        self.board = board

    def getPossibleActions(self):
        next_moves = rest_api.fetch_next_available_moves(self.board)
        return next_moves

    def takeAction(self,action):
        response = rest_api.take_action(self.board, action)
        return MCTSState(response)

    def isTerminal(self):
        response = rest_api.fetch_winner(self.board)
        return response.status_code != 404

    def getReward(self):
        response = rest_api.fetch_winner(self.board)
        if response.text == self.board.p1Pos:
            return 100
        else:
            return -100
