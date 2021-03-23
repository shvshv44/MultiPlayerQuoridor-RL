


class Trainer:


    def __init__(self, agent):
        self.server = "http://localhost:8080"
        self.agent = agent

    def start_training_session(self):
        game_id = self.create_game()


    def create_game(self):
        return "shaq" # TODO: use http to create the game
