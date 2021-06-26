import random
from collections import deque

import numpy as np
import tensorflow as tf
import tensorflow as ts
from tensorflow.keras.layers import Dense, Flatten, Conv2D, Dropout
from tensorflow.keras.layers import LeakyReLU, Input, Embedding, Reshape, Concatenate
from tensorflow.keras.models import clone_model
from tensorflow.keras.optimizers import Adam

from globals import Global

loss = "mean_squared_error"
optimizer = Adam(learning_rate=1e-3)


class Model:

    def __init__(self):
        self.states = Global.observation_shape
        self.actions = Global.num_of_actions
        self.model = self.build_model(self.states, self.actions)
        self.model.summary()

    def build_model(self, states, actions):
        # return self.new_model(states, actions)
        return self.best_model(states, actions)

    def best_model(self, states, actions):
        board_shape = (states[1], states[2])
        n_nodes = board_shape[0] * board_shape[1]

        in_start_loc = Input(shape=(1,))
        # Turn positive numbers to numbers between 0 and 1 -> need to change if 4 players playing
        li = Embedding(2, 9)(in_start_loc)
        li = LeakyReLU(alpha=0.2)(li)
        li = Dense(n_nodes)(li)
        li = Reshape((board_shape[0], board_shape[1], 1))(li)

        in_state = Input(shape=(board_shape[0], board_shape[1], 6))
        merge = Concatenate()([in_state, li])

        f3 = Conv2D(128, (3, 3), strides=(2, 2), padding='same')(merge)
        f3 = LeakyReLU(alpha=0.2)(f3)
        f3 = Conv2D(128, (3, 3), strides=(2, 2), padding='same')(f3)
        f3 = LeakyReLU(alpha=0.2)(f3)
        f3 = Flatten()(f3)

        f5 = Conv2D(128, (5, 5), strides=(2, 2), padding='same')(merge)
        f5 = LeakyReLU(alpha=0.2)(f5)
        f5 = Conv2D(128, (5, 5), strides=(2, 2), padding='same')(f5)
        f5 = LeakyReLU(alpha=0.2)(f5)
        f5 = Flatten()(f5)

        f7 = Conv2D(128, (7, 7), strides=(2, 2), padding='same')(merge)
        f7 = LeakyReLU(alpha=0.2)(f7)
        f7 = Conv2D(128, (7, 7), strides=(2, 2), padding='same')(f7)
        f7 = LeakyReLU(alpha=0.2)(f7)
        f7 = Flatten()(f7)

        merge2 = Concatenate()([f3, f5, f7])
        d = Dropout(0.4)(merge2)
        out = tf.nn.relu(self.noisy_dense(256, d))
        out = tf.nn.relu(self.noisy_dense(256, out))
        out = self.noisy_dense(132, out)
        out_layer = Dense(actions, activation='softmax')(d)

        model = ts.keras.models.Model([in_start_loc, in_state], out_layer)
        model.compile(optimizer=optimizer, loss=loss, metrics=['mae'])
        return model

    def noisy_dense(self, units, input):
        w_shape = [units, input.shape[1]]
        mu_w = tf.Variable(initial_value=tf.random.truncated_normal(shape=w_shape))
        sigma_w = tf.Variable(initial_value=tf.constant(0.017, shape=w_shape))
        epsilon_w = tf.random.uniform(shape=w_shape)

        b_shape = [units]
        mu_b = tf.Variable(initial_value=tf.random.truncated_normal(shape=b_shape))
        sigma_b = tf.Variable(initial_value=tf.constant(0.017, shape=b_shape))
        epsilon_b = tf.random.uniform(shape=b_shape)

        w = tf.add(mu_w, tf.multiply(sigma_w, epsilon_w))
        b = tf.add(mu_b, tf.multiply(sigma_b, epsilon_b))

        return tf.matmul(input, tf.transpose(w)) + b


class Agent:

    def __init__(self, model):
        self.memory = deque(maxlen=2000)

        self.gamma = 0.85
        self.epsilon_max = 0.85
        self.epsilon_min = 0.15
        self.epsilon = self.epsilon_max
        self.epsilon_decay = 0.98
        self.tau = .125

        self.target_model = model
        self.model = self.create_model_clone(model)
        self.alternate_model = self.model
        self.advance_chance_value = 0.2

        self.random_act_epsilon_max = 0.9
        self.random_act_epsilon_min = 0.1
        self.random_act_epsilon = self.epsilon_max
        self.random_act_epsilon_decay = 0.99995

        self.history = []

    def get_history(self):
        return self.history

    def act(self, state, env, location_label):
        self.epsilon *= self.epsilon_decay
        self.epsilon = max(self.epsilon_min, self.epsilon)
        if np.random.random() < 0.5:  # self.epsilon:
            print("random action")
            action = self.random_act(env)
        else:
            # print("predicted action")
            action = self.predicated_act(state, env, location_label)

        return action

    def predicated_act(self, state, env, location_label):
        all_predictions = self.model.predict(self.prepare_state_to_predication(state, env, location_label))[0]
        self.add_advance_more_value(location_label, all_predictions)
        legal_predictions = self.minimize_to_legal_predictions(all_predictions, env)
        action_index = self.probability_predication(legal_predictions)
        # action_index = self.greedy_predication(legal_predictions)
        return env.get_action_options()[action_index]

    def random_act(self, env):
        choices = env.get_action_options()
        choices_len = len(choices)
        self.random_act_epsilon *= self.random_act_epsilon_decay
        self.random_act_epsilon = max(self.random_act_epsilon_min, self.random_act_epsilon)
        if np.random.random() < self.random_act_epsilon:
            return_value = Agent.smart_move(self, choices, env.get_opponent_location())
        else:
            return_value = choices[np.random.randint(0, choices_len)]
        return return_value

    def smart_move(self, actions, opponent_position):
        print("smart random action")
        random_choice = np.random.random()
        if random_choice < 0.7 and actions.count(1) > 0:
            return 1
        elif np.random.random() < 0.3 and actions.count(
                4 + opponent_position[1] + (8 * (opponent_position[0] - 1))) > 0:
            print("Put wall on trainer face 1")
            return 4 + opponent_position[1] + (8 * (opponent_position[0] - 1))
        elif np.random.random() < 0.3 and actions.count(
                4 - 1 + opponent_position[1] + (8 * (opponent_position[0] - 1))) > 0:
            print("Put wall on trainer face 2")
            return 4 - 1 + opponent_position[1] + (8 * (opponent_position[0] - 1))
        elif np.random.random() < 0.5 and actions.count(2) > 0:
            return 2
        elif np.random.random() < 0.5 and actions.count(3) > 0:
            return 3
        elif np.random.random() < 0.1 and actions.count(0) > 0:
            return 0
        else:
            choices_len = len(actions)
            random_i = np.random.randint(0, choices_len)
            return_value = actions[random_i]
            return return_value

    def remember(self, state, action, reward, new_state, done):
        self.memory.append([state, action, reward, new_state, done])

    def replay(self, env, location_label):
        batch_size = 32

        if len(self.memory) < batch_size:
            batch_size = 16

        if len(self.memory) < batch_size:
            return

        samples = random.sample(self.memory, batch_size)
        for sample in samples:
            state, action, reward, new_state, done = sample
            target = self.model.predict(self.prepare_state_to_predication(state, env, location_label))
            if done:
                target[0][action] = reward
            else:
                # Q_future = max(self.target_model.predict(self.prepare_state_to_predication(new_state, env, location_label))[0])
                # target = reward + self.discount_factor * np.max(self.model.predict(next_state)[0])
                Q_future = \
                self.alternate_model.predict(self.prepare_state_to_predication(new_state, env, location_label))[0][
                    np.argmax(self.model.predict(self.prepare_state_to_predication(new_state, env, location_label))[0])]
                target[0][action] = reward + Q_future * self.gamma
            current_history = self.model.fit(self.prepare_state_to_predication(state, env, location_label), target, epochs=1, verbose=0)
            self.history.append(current_history.history["loss"][0])

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

    def prepare_state_to_predication(self, state, env, location_label):
        location_dim = (location_label,)
        state_dim = state.reshape(env.observation_shape())
        # location_dim = (1,) + (location_label,)
        # state_dim = state.reshape((1,) + env.observation_shape())
        return [location_dim, state_dim]

    def minimize_to_legal_predictions(self, all_predictions, env):
        return all_predictions[env.get_action_options()]

    def reset_epsilon(self):
        self.epsilon = self.epsilon_max

    def add_advance_more_value(self, location_label, all_predictions):
        advance_index = 0

        # Start up so  prefer move down
        if location_label == 0:
            advance_index = 1

        # Start down so prefer to move up
        if location_label == 1:
            advance_index = 0

        all_predictions[advance_index] += self.advance_chance_value
        if all_predictions[advance_index] > 1:
            all_predictions[advance_index] = 1

    def greedy_predication(self, legal_predictions):
        return np.argmax(legal_predictions)

    def probability_predication(self, legal_predictions):
        max_indices = legal_predictions.argsort()[-3:][::-1]
        max_sum = 0
        for i in max_indices:
            max_sum += legal_predictions[i]

        if max_sum == 0:
            return np.argmax(legal_predictions)

        action_probs = [0]
        temporary_sum = 0
        for i in max_indices:
            change = (legal_predictions[i] / max_sum)
            if change > 0.5:
                change = change - 0.15
            if change < 0.075:
                change = 0.075
            temporary_sum += change
            action_probs.append(temporary_sum)
        action_probs.append(1.0)

        action_index = 0
        actual_prob = np.random.random()
        for prob_index in range(0, len(max_indices)):
            if action_probs[prob_index] <= actual_prob <= action_probs[prob_index + 1]:
                action_index = prob_index

        return max_indices[action_index]
