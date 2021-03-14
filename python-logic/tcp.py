import json
import socket
import threading
from api import MovementDirection, MovePawnAction
import random


class TCP:
    def __init__(self):
        # Connecting To Server
        self.num_of_walls = ""
        self.players = ""
        self.movements = [MovementDirection.Left, MovementDirection.Right, MovementDirection.Up, MovementDirection.Down]

        rows, cols = (18, 18)
        self.board = [[0] * cols] * rows
        self.players = ""
        self.client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        try:
            self.client.connect(('127.0.0.1', 14000))
            receive_thread = threading.Thread(target=self.receive)
            receive_thread.start()
            self.write("9726e516-bd76-49b0-8ffe-3dda4cf63585")
        except:
            print("Could not connect to the server")

    def receive(self):
        while True:
            try:
                # Receive Message From Server
                message = self.client.recv(1024 * 100).decode('ascii')
                json_message = json.loads(message)

                if json_message["type"] == 'StartGameMessage':
                    self.num_of_walls = json_message["numOfWalls"]
                    self.players = json_message["players"]

                if json_message["type"] == 'EndTurnEvent':
                    current_turn_move = json_message["currentTurnMove"]
                    player_played = json_message["playerPlayed"]

                if json_message["type"] == 'NewTurnEvent':
                    next_player_to_play = json_message["nextPlayerToPlay"]
                    avialiable_moves = json_message["avialiableMoves"]
                    available_walls = json_message["availableWalls"]

                    if len(next_player_to_play) > 15:
                        self.write(json.dumps({'direction': random.choice(self.movements).value}))

                print(message)


            except Exception as e:
                # Close Connection When Error
                print(e)
                print("An error occured!")
                self.client.close()
                break

    # Sending Messages To Server
    def write(self, message):
        self.client.send(message.encode('ascii'))
