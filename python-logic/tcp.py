import json
import socket
import threading
import random
import traceback
from globals import Global

class TCP:
    def __init__(self, game_id, name, receive_func):
        # Connecting To Server
        self.num_of_walls = ""
        self.players = ""
        self.json_dec = json.JSONDecoder()
        self.name = name

        rows, cols = (18, 18)
        self.board = [[0] * cols] * rows
        self.players = ""
        self.client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        try:
            self.client.connect(('127.0.0.1', 14000))
            receive_thread = threading.Thread(target=self.receive, args=[receive_func])
            receive_thread.start()
            self.write({"gameId": game_id, "name": self.name})
        except:
            print("Could not connect to the server")

    def receive(self, receiveFunc):
        while True:
            try:
                # Receive Message From Server
                message = self.client.recv(1024 * 100).decode('ascii')

                pos = 0
                while not pos == len(str(message)):
                    j, json_len = self.json_dec.raw_decode(str(message)[pos:])
                    pos += json_len
                    # For printing the messages from server
                    #print("{} : {}".format(self.name, j))
                    receiveFunc(j)

            except Exception as e:
                # Close Connection When Error
                print("An error occured!")
                print(e)
                traceback.print_exc()
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
            self.write(json.dumps({'direction': random.choice(Global.movements).value}))

    # Sending Messages To Server
    def write(self, message):
        self.client.send(str(message).replace('\'', '\"').encode('ascii'))
