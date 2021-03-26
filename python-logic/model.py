import numpy as np
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Flatten
from tensorflow.keras.optimizers import Adam


class Model:

    def __init__(self, observation_input_shape, action_input_shape):
        self.states = observation_input_shape
        self.actions = action_input_shape
        self.model = self.build_model(self.states, self.actions)
        self.model.summery()

    def build_model(self, states, actions):
        model = Sequential()
        model.add(Dense(24, activation='relu', input_shape=(9,9,3)))
        model.add(Dense(24, activation='relu'))
        model.add(Dense(actions, activation='linear'))
        return model
