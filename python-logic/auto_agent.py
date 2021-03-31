import rest_api
from quoridor_env import QuoridorEnv


class AutoAgent:
    def __init__(self, agent):
        self.agent = agent
        self.name = "AutoAgent"

    def join_game(self, game_id):
        self.game_id = game_id
        rest_api.join_game(self.game_id, self.name).content.decode("utf-8")
        self.start_game_with_agent(self.game_id)

    def start_game_with_agent(self, game_id):
        env = QuoridorEnv(game_id, self.name)
        self.agent.predict(env)
