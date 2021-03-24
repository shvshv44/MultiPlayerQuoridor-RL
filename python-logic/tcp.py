import json
import socket
import threading
from api import MovementDirection, MovePawnAction
import random

class TCP:
    def __init__(self, game_id, receive_func):
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
            receive_thread = threading.Thread(target=self.receive(receive_func))
            receive_thread.start()
            self.write("e533ebd4-746a-4016-a124-182c08210327")
        except:
            print("Could not connect to the server")

    def receive(self, receiveFunc):
        while True:
            try:
                # Receive Message From Server
                message = self.client.recv(1024 * 100).decode('ascii')
                json_message = json.loads(message)

                receiveFunc(json_message)
                # if json_message["type"] == 'StartGameMessage':
                #     self.handle_start_game_message(json_message)
                #
                # if json_message["type"] == 'EndTurnEvent':
                #     self.handle_end_turn_event(json_message)
                #
                # if json_message["type"] == 'NewTurnEvent':
                #     self.handle_new_turn_event(json_message)

                print(message)


            except Exception as e:
                # Close Connection When Error
                print(e)
                print("An error occured!")
                self.client.close()
                break

    def handle_start_game_message(self, json_message):
        self.num_of_walls = json_message["numOfWalls"]
        self.players = json_message["players"]

    def handle_end_turn_event(self, json_message):
        current_turn_move = json_message["currentTurnMove"]
        player_played = json_message["playerPlayed"]

    def handle_new_turn_event(self, json_message):
        next_player_to_play = json_message["nextPlayerToPlay"]
        avialiable_moves = json_message["avialiableMoves"]
        available_walls = json_message["availableWalls"]
        if len(next_player_to_play) > 15:
            self.write(json.dumps({'direction': random.choice(self.movements).value}))

    # Sending Messages To Server
    def write(self, message):
        self.client.send(message.encode('ascii'))
