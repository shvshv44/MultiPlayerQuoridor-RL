from api import MovementDirection

class Global:
    server = "http://193.106.55.110:8080"
    competitive_agent = "http://193.106.55.110:8001"
    movements = list(map(lambda c: c, MovementDirection))

    # 0-3: for UP, DOWN, LEFT RIGHT, 4 - 68: for 64 Horizontal walls, 69 - 131: for 64 Vertical walls
    num_of_actions = 4 + 2 * 8 * 8

    #   dimensions:
    #   1. main player location on board
    #   2. opponent player location on board
    #   3. horizontal walls location on board
    #   4. vertical walls location on board
    #   5. main player goal locations on board
    observation_shape = (1,) + (9, 9, 5)  # window length + board shape
