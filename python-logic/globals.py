from api import MovementDirection

class Global:
    server = "http://localhost:8080"
    movements = list(map(lambda c: c, MovementDirection))

