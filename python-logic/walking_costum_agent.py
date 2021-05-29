from collections import deque

from tensorflow.keras.optimizers import Adam

from globals import Global

from keras import metrics
import numpy as np
import tensorflow as tf
from tensorflow.keras.layers import Dense, Flatten, Conv2D, BatchNormalization, Dropout
from tensorflow.keras.layers import LeakyReLU, Input, Embedding, Reshape, Concatenate
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.initializers import RandomNormal
import random
from tensorflow.keras.models import clone_model
from datetime import datetime

loss = "mean_squared_error"
optimizer = Adam(learning_rate=1e-3)


class Model:

    def __init__(self):
        self.actions = 4
        self.model = self.build_model(self.actions)
        self.model.summary()

    def build_model(self, actions):
        return self.walking_model(actions)

    def walking_model(self, actions):
        board_shape = (9, 9)
        n_nodes = board_shape[0] * board_shape[1]

        in_location = Input(shape=(1, ))
        loc = Dense(board_shape[0] * board_shape[1])(in_location)
        loc = Reshape((board_shape[0], board_shape[1], 1))(loc)

        in_state = Input(shape=(board_shape[0], board_shape[1], 3))

        merge_input = Concatenate()([loc, in_state])

        f3 = Conv2D(128, (3, 3), strides=(2, 2), padding='same')(merge_input)
        f3 = BatchNormalization()(f3)
        f3 = LeakyReLU(alpha=0.2)(f3)
        f3 = Conv2D(128, (3, 3), strides=(2, 2), padding='same')(f3)
        f3 = BatchNormalization()(f3)
        f3 = LeakyReLU(alpha=0.2)(f3)
        f3 = Flatten()(f3)

        f5 = Conv2D(128, (5, 5), strides=(2, 2), padding='same')(merge_input)
        f5 = BatchNormalization()(f5)
        f5 = LeakyReLU(alpha=0.2)(f5)
        f5 = Conv2D(128, (5, 5), strides=(2, 2), padding='same')(f5)
        f5 = BatchNormalization()(f5)
        f5 = LeakyReLU(alpha=0.2)(f5)
        f5 = Flatten()(f5)

        f7 = Conv2D(128, (7, 7), strides=(2, 2), padding='same')(merge_input)
        f7 = BatchNormalization()(f7)
        f7 = LeakyReLU(alpha=0.2)(f7)
        f7 = Conv2D(128, (7, 7), strides=(2, 2), padding='same')(f7)
        f7 = BatchNormalization()(f7)
        f7 = LeakyReLU(alpha=0.2)(f7)
        f7 = Flatten()(f7)

        merge = Concatenate()([f3, f5, f7])
        d = Dense(256)(merge)
        d = LeakyReLU(alpha=0.2)(d)
        d = Dropout(0.6)(d)
        d = Dense(64)(d)
        d = LeakyReLU(alpha=0.2)(d)
        d = Dense(16)(d)
        d = LeakyReLU(alpha=0.2)(d)
        out_layer = Dense(actions, activation='softmax')(d)

        model = tf.keras.models.Model([in_location, in_state], out_layer)
        model.compile(optimizer=optimizer, loss=loss, metrics=['mae'])
        return model


class Agent:

    def __init__(self, model, name="Agent"):
        self.memory = deque(maxlen=2000)

        self.gamma = 0.85
        self.epsilon_max = 0.9
        self.epsilon_min = 0.15
        self.epsilon = self.epsilon_max
        self.epsilon_decay = 0.98
        self.tau = .125

        self.target_model = model
        self.model = self.create_model_clone(model)
        self.name = name

    def act(self, state, env):
        self.epsilon *= self.epsilon_decay
        self.epsilon = max(self.epsilon_min, self.epsilon)
        if np.random.random() < self.epsilon:
            print("random action")
            action = self.random_act(env)
        else:
            print("predicted action")
            action = self.predicated_act(state, env)

        return action

    def predicated_act(self, state, env):
        all_predictions = self.model.predict(self.prepare_state_to_predication(state, env))[0]
        legal_predictions = self.minimize_to_legal_predictions(all_predictions, env)
        action_index = self.greedy_predication(legal_predictions)
        return env.get_action_options()[action_index]

    def random_act(self, env):
        choices = env.get_action_options()
        choices_len = len(choices)
        random_i = np.random.randint(0, choices_len)
        return choices[random_i]

    def remember(self, state, action, reward, new_state, done):
        self.memory.append([state, action, reward, new_state, done])

    def replay(self, env, history):
        batch_size = 32

        if len(self.memory) < batch_size:
            batch_size = 16

        if len(self.memory) < batch_size:
            return

        samples = random.sample(self.memory, batch_size)
        for sample in samples:
            state, action, reward, new_state, done = sample
            target = self.target_model.predict(self.prepare_state_to_predication(state, env))
            if done:
                target[0][action] = reward
            else:
                Q_future = max(
                    self.target_model.predict(self.prepare_state_to_predication(new_state, env))[0])
                target[0][action] = reward + Q_future * self.gamma
            curr_history = self.model.fit(self.prepare_state_to_predication(state, env), target, epochs=1, verbose=0)
            history_to_add = curr_history.history["loss"]
            history.append(history_to_add)

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
        loc_dim = (state[0],)
        state_dim = state[1].reshape(1, 9, 9, 3)
        return [loc_dim, state_dim]

    def minimize_to_legal_predictions(self, all_predictions, env):
        return all_predictions[env.get_action_options()]

    def greedy_predication(self, legal_predictions):
        return np.argmax(legal_predictions)

    def probability_predication(self, legal_predictions):
        max_indices = legal_predictions.argsort()[-3:][::-1]
        max_sum = 0
        for i in max_indices:
            max_sum += legal_predictions[i]

        action_probs = [0]
        temporary_sum = 0
        for i in max_indices:
            temporary_sum += (legal_predictions[i] / max_sum)
            action_probs.append(temporary_sum)

        action_index = 0
        actual_prob = np.random.random()
        for prob_index in range(0, len(max_indices)):
            if action_probs[prob_index] <= actual_prob <= action_probs[prob_index + 1]:
                action_index = prob_index

        return max_indices[action_index]
