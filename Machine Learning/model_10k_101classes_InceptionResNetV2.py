import glob
import numpy as np
import pandas as pd
from scipy.misc import imread, imsave, imresize
from keras.utils import np_utils

csv = pd.read_csv("/home/arnavb/data_scale_1_0/data2.csv").values

img_rows = 200
img_cols = 200

import pickle
x = pickle.load(open("images_x_pickle"))

nb_classes = 101
y_values = csv[:,4]
y = np_utils.to_categorical(y_values, nb_classes)

import tensorflow as tf
from keras.backend.tensorflow_backend import set_session
config = tf.ConfigProto()
config.gpu_options.per_process_gpu_memory_fraction = 1.0
config.gpu_options.visible_device_list = "1"
set_session(tf.Session(config=config))

from keras import backend as K
K.set_image_dim_ordering('th')  # a lot of old examples of CNNs

from keras.models import Sequential, model_from_json, Model, load_model
from keras.layers import Dense, Activation, Flatten, Dropout, Convolution2D, MaxPooling2D
from keras import applications
from keras import optimizers

base_model = applications.InceptionResNetV2(include_top=False, input_shape=(1, img_rows, img_cols))

add_model = Sequential()
add_model.add(Flatten(input_shape=base_model.output_shape[1:]))
add_model.add(Dense(128, activation='relu'))
add_model.add(Dropout(0.5))
add_model.add(Dense(128, activation='relu'))
add_model.add(Dropout(0.5))
add_model.add(Dense(nb_classes, activation='softmax'))

model = Model(inputs=base_model.input, outputs=add_model(base_model.output))
model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])

from sklearn.cross_validation import train_test_split
X_train,X_val,Y_train,Y_val = train_test_split(x,y,test_size=0.2)
model.fit(X_train, Y_train, validation_data=(X_val, Y_val), batch_size=256, nb_epoch=100, verbose=1)