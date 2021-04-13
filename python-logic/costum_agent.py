from collections import deque

from tensorflow.keras.optimizers import Adam
from globals import Global
import numpy as np
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Flatten, Conv2D
from tensorflow.keras.optimizers import Adam
import random
from tensorflow.keras.models import clone_model
from datetime import datetime

loss = "mean_squared_error"
optimizer = Adam(learning_rate=1e-3)


class Model:

    def __init__(self, observation_input_shape, action_input_shape):
        self.states = observation_input_shape
        self.actions = action_input_shape
        self.model = self.build_model(self.states, self.actions)
        self.model.summary()

    def build_model(self, states, actions):
        model = Sequential()
        model.add(Conv2D(8, (3, 3), padding='same', input_shape=states))
        model.add(Conv2D(16, (3, 3), padding='same', input_shape=states))
        model.add(Flatten())
        model.add(Dense(324, activation='relu'))
        model.add(Dense(actions, activation='linear'))
        model.compile(optimizer=optimizer, loss=loss, metrics=['mae'])
        return model


class Agent:

    def __init__(self, model):
        self.memory = deque(maxlen=2000)

        self.gamma = 0.85
        self.epsilon = 1.0
        self.epsilon_min = 0.01
        self.epsilon_decay = 0.995
        self.tau = .125

        self.target_model = model
        self.model = self.create_model_clone(model)

    def act(self, state, env):
        self.epsilon *= self.epsilon_decay
        self.epsilon = max(self.epsilon_min, self.epsilon)
        #if np.random.random() < self.epsilon:
        if True:
            print("random action")
            action = self.random_act(env)
        else:
            print("predicted action")
            action = self.predicated_act(state, env)

        print(action)
        return action

    def predicated_act(self, state, env):
        all_predictions = self.model.predict(self.prepare_state_to_predication(state, env))[0]
        legal_predictions = self.minimize_to_legal_predictions(all_predictions, env)
        action_index = np.argmax(legal_predictions)
        return env.get_action_options()[action_index]

    def random_act(self, env):
        choices = env.get_action_options()
        choices_len = len(choices)
        random_i = np.random.randint(0, choices_len)
        print(choices)
        return choices[random_i]

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
                Q_future = max(self.target_model.predict(self.prepare_state_to_predication(new_state, env))[0])
                target[0][action] = reward + Q_future * self.gamma
            self.model.fit(self.prepare_state_to_predication(state, env), target, epochs=1, verbose=0)

    def target_train(self):
        weights = self.model.get_weights()
        target_weights = self.target_model.get_weights()
        for i in range(len(target_weights)):
            target_weights[i] = weights[i] * self.tau + target_weights[i] * (1 - self.tau)
        self.target_model.set_weights(target_weights)

    def save_model(self):
        time = datetime.now().strftime("%d_%m_%Y__%H_%M_%S")
        saved_file_name = "./models/shaq_{}.h5".format(time)
        self.save_model_to_path(saved_file_name)

    def save_model_to_path(self, fn):
        self.model.save(fn)

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

