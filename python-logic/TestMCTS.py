from mcts import mcts

import MCTSState

board = {
    "board": {
        "p1UUID": "d7f056cb-a915-4d40-9f42-64e7e593739a",
        "p2UUID": "97a5cd06-0451-4716-9c27-34731001f932",
        "p1Pos": {
            "y": 2,
            "x": 3
        },
        "p2Pos": {
            "y": 5,
            "x": 2
        },
        "walls": [],
        "p1Walls": 4,
        "p2Walls": 2,
        "p1Turn": True,
        "p1EndLine": [{
            "y": 0,
            "x": 4
        }],
        "p2EndLine": [{
            "y": 3,
            "x": 5
        }]
    },
    "action": {
        "name": "MOVE_PAWN",
        "direction": "Down"
    }
}


if __name__ == '__main__':
    initiate_state = MCTSState.MCTSState(board)
    mcts = mcts(timeLimit=1000)
    bestAction = mcts.search(initialState=initiate_state)
    print(bestAction)

