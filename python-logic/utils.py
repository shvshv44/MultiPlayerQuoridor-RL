import json
from globals import Global


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
