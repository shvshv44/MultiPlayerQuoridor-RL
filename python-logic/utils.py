import json
from globals import Global
import numpy as np


def convert_action_to_server(action):
    output = json.dumps({})
    if 0 <= action <= 3:
        output = json.dumps({'direction': Global.movements[action].value})
    elif 4 <= action <= 131:
        dir = "Right"
        value = action - 4

        if value > 64 - 1:
            dir = "Down"
            value -= 64

        x, y = value % 8, value // 8

        output = json.dumps({
            'wall': {
                "position": {
                    "x": x,
                    "y": y
                },
                "wallDirection": dir
            }
        }
        )

    return output

def convert_board(board, winning_points_dim):
    dim1 = np.zeros((9, 9), dtype=int)
    dim2 = np.zeros((9, 9), dtype=int)
    dim1[board["players"][0]["y"]][board["players"][0]["x"]] = 1
    dim2[board["players"][1]["y"]][board["players"][1]["x"]] = 1

    all_dims = np.dstack((dim1, dim2, board["horizontalWalls"], board["verticalWalls"], winning_points_dim))
    return all_dims
