from rl.agents import DQNAgent
from rl.policy import BoltzmannQPolicy
from rl.memory import SequentialMemory

class Agent:

    def __init__(self, model):
        dqn = self.build_agent(model.model, model.actions)
        dqn.compile(Adam(lr=1e-3), metrics=['mae'])
        dqn.fit(env, nb_steps=50000, visualize=False, verbose=1)

    def build_agent(self, model, actions):
        policy = BoltzmannQPolicy()
        memory = SequentialMemory(limit=50000, window_length=1)
        dqn = DQNAgent(model=model, memory=memory, policy=policy,
                       nb_actions=actions, nb_steps_warmup=10, target_model_update=1e-2)
        return dqn



