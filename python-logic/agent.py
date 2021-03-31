from rl.agents import DQNAgent
from rl.policy import BoltzmannQPolicy
from rl.memory import SequentialMemory
from tensorflow.keras.optimizers import Adam
from globals import Global

class Agent:

    def __init__(self, model):
        self.dqn = self.build_agent(model.model)
        self.dqn.compile(Adam(lr=1e-3), metrics=['mae'])

    def build_agent(self, model):
        policy = BoltzmannQPolicy()
        memory = SequentialMemory(limit=50000, window_length=1)
        dqn = DQNAgent(model=model, memory=memory, policy=policy,
                       nb_actions=Global.num_of_actions, nb_steps_warmup=10, target_model_update=1e-2, batch_size=1)
        return dqn

    def train_agent(self, env):
        self.dqn.fit(env, nb_steps=5000, visualize=False, verbose=1)

    def predict(self, env):
        self.dqn.model.predict(env.get_and_convert_board())




