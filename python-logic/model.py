import numpy as np
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Flatten, Conv2D
from tensorflow.keras.optimizers import Adam


class Model:

    def __init__(self, observation_input_shape, action_input_shape):
        self.states = observation_input_shape
        self.actions = action_input_shape
        self.model = self.build_model(self.states, self.actions)
        self.model.summary()

    def build_model(self, states, actions):
        model = Sequential()
        model.add(Conv2D(8, (3,3), padding='same', input_shape=states))
        model.add(Conv2D(16, (3, 3), padding='same', input_shape=states))
        model.add(Flatten())
        model.add(Dense(324, activation='relu'))
        model.add(Dense(actions, activation='linear'))
        return model
