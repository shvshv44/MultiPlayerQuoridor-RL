from api import MovementDirection

class Global:
    server = "http://localhost:8080"
    movements = list(map(lambda c: c, MovementDirection))

    # 0-3: for UP, DOWN, LEFT RIGHT, 4 - 68: for 64 Horizontal walls, 69 - 131: for 64 Vertical walls
    num_of_actions = 4 + 2 * 8 * 8
