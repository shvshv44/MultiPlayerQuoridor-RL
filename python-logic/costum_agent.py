from collections import deque

from tensorflow.keras.optimizers import Adam
from globals import Global
import numpy as np
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Flatten, Conv2D, LeakyReLU, BatchNormalization
from tensorflow.keras.optimizers import Adam
import random
from tensorflow.keras.models import clone_model
from datetime import datetime

loss = "mean_squared_error"
optimizer = Adam(learning_rate=1e-3)


class Model:

    def __init__(self):
        self.states = Global.observation_shape
        self.actions = Global.num_of_actions
        self.model = self.build_model(self.states, self.actions)
        self.model.summary()

    def build_model(self, states, actions):
        model = Sequential()
        model.add(Conv2D(8, (3, 3), padding='same', input_shape=states))
        model.add(BatchNormalization())
        model.add(LeakyReLU())
        model.add(Conv2D(16, (3, 3), padding='same'))
        model.add(BatchNormalization())
        model.add(Flatten())
        model.add(Dense(512))
        model.add(LeakyReLU())
        model.add(Dense(256))
        model.add(LeakyReLU())
        model.add(Dense(actions, activation='linear'))
        model.compile(optimizer=optimizer, loss=loss, metrics=['mae'])
        return model


class Agent:

    def __init__(self, model):
        self.memory = deque(maxlen=2000)

        self.gamma = 0.85
        self.epsilon = 1.0
        self.epsilon_min = 0.01
        self.epsilon_decay = 0.998
        self.tau = .125

        self.target_model = model
        self.model = self.create_model_clone(model)

    def act(self, state, env):
        self.epsilon *= self.epsilon_decay
        self.epsilon = max(self.epsilon_min, self.epsilon)
        if np.random.random() < self.epsilon:
            #print("random action")
            action = self.random_act(env)
        else:
            #print("predicted action")
            action = self.predicated_act(state, env)

        return action

    def predicated_act(self, state, env):
        all_predictions = self.model.predict(self.prepare_state_to_predication(state, env))[0]
        legal_predictions = self.minimize_to_legal_predictions(all_predictions, env)
        if len(legal_predictions) > 1 and np.random.random() < 0.3:
            print("Second choice act")
            action_index = np.argmax(legal_predictions)
            legal_predictions = np.delete(legal_predictions, action_index)
        if len(legal_predictions) > 1 and np.random.random() < 0.1:
            print("Second choice act")
            action_index = np.argmax(legal_predictions)
            legal_predictions = np.delete(legal_predictions, action_index)
        action_index = np.argmax(legal_predictions)
        return env.get_action_options()[action_index]

    def random_act(self, env):
        choices = env.get_action_options()
        if np.random.random() > 0.1:
            return_value = Agent.smart_move(self, choices)
        else:
            choices_len = len(choices)
            random_i = np.random.randint(0, choices_len)
            return_value = choices[random_i]
        return return_value

    def smart_move(self, actions):
        random_choice = np.random.random()
        if random_choice < 0.9 and actions.count(1) > 0:
            return 1
        elif np.random.random() < 0.5 and actions.count(2) > 0:
            return 2
        elif actions[0] == 3 or actions.count(3) > 0:
            return 3
        elif actions.count(0) > 0:
            return 0
        else:
            choices_len = len(actions)
            random_i = np.random.randint(0, choices_len)
            return_value = actions[random_i]
            return return_value

    def remember(self, state, action, reward, new_state, done):
        self.memory.append([state, action, reward, new_state, done])

    def replay(self, env):
        batch_size = 32
        if len(self.memory) < batch_size:
            return

        samples = random.sample(self.memory, batch_size)
        for sample in samples:
            state, action, reward, new_state, done = sample
            target = self.target_model.predict(self.prepare_state_to_predication(state, env))
            if done:
                target[0][action] = reward
            else:
                g = self.target_model.predict(self.prepare_state_to_predication(new_state, env))
                Q_future = max(self.target_model.predict(self.prepare_state_to_predication(new_state, env))[0])
                target[0][action] = reward + Q_future * self.gamma
            self.model.fit(self.prepare_state_to_predication(state, env), target, epochs=1, verbose=0)

    def target_train(self):
        weights = self.model.get_weights()
        target_weights = self.target_model.get_weights()
        for i in range(len(target_weights)):
            target_weights[i] = weights[i] * self.tau + target_weights[i] * (1 - self.tau)
        self.target_model.set_weights(target_weights)

    def create_model_clone(self, model):
        model_copy = clone_model(model)
        model_copy.build(model.layers[0].input_shape)  # replace 10 with number of variables in input layer
        model_copy.compile(optimizer=optimizer, loss=loss, metrics=['mae'])
        model_copy.set_weights(model.get_weights())
        return model_copy

    def prepare_state_to_predication(self, state, env):
        return state.reshape((1,) + env.observation_shape())

    def minimize_to_legal_predictions(self, all_predictions, env):
        return all_predictions[env.get_action_options()]
