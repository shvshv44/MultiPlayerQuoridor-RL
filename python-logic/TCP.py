import socket
import sys
import threading


class TCP:
    def __init__(self):
        self.playerName = "Python"
        # Connecting To Server
        self.client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        try:
            self.client.connect(('127.0.0.1', 55555))
            receive_thread = threading.Thread(target=self.receive)
            receive_thread.start()
        except:
            print("Could not connect to the server")


    def receive(self):
        while True:
            try:
                # Receive Message From Server
                message = self.client.recv(1024).decode('ascii')
                print(message)

            except:
                # Close Connection When Error
                print("An error occured!")
                self.client.close()
                break

    # Sending Messages To Server
    def write(self, message):
        self.client.send(message.encode('ascii'))
