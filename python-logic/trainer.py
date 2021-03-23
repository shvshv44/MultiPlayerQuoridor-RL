import requests as http


class Trainer:

    def __init__(self, agent):
        self.server = "http://localhost:8080"
        self.agent = agent
        self.name = "Trainer"

    def start_training_session(self):
        game_id = self.create_game()
        self.start_game_with_agent(game_id)


    def create_game(self):
        create_game_url = self.server + "/CreateGame/" + self.name
        response = http.get(create_game_url)
        return response

    def start_game_with_agent(self, game_id):
        pass # TODO: implement after TCP refactoring

