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
config.gpu_options.visible_device_list = "0"
set_session(tf.Session(config=config))

from keras import backend as K
K.set_image_dim_ordering('th')  # a lot of old examples of CNNs

from keras.models import Sequential,model_from_json
from keras.layers import Dense, Activation, Flatten, Dropout, Convolution2D, MaxPooling2D

nb_pool = 2     # size of pooling area for max pooling
nb_conv = 3     # convolution kernel size

model = Sequential()

model.add(Convolution2D(16, nb_conv, nb_conv, border_mode='valid', input_shape=(1, img_rows, img_cols), activation='relu'))
model.add(Convolution2D(16, nb_conv, nb_conv, activation='relu'))
model.add(MaxPooling2D(pool_size=(nb_pool, nb_pool)))

model.add(Convolution2D(32, nb_conv, nb_conv, activation='relu'))
model.add(Convolution2D(32, nb_conv, nb_conv, activation='relu'))
model.add(MaxPooling2D(pool_size=(nb_pool, nb_pool)))

model.add(Convolution2D(64, nb_conv, nb_conv, activation='relu'))
model.add(Convolution2D(64, nb_conv, nb_conv, activation='relu'))
model.add(MaxPooling2D(pool_size=(nb_pool, nb_pool)))

model.add(Flatten())

model.add(Dense(128, activation='relu'))
model.add(Dropout(0.5))
model.add(Dense(128, activation='relu'))
model.add(Dropout(0.5))

model.add(Dense(nb_classes, activation='softmax'))

model.compile(loss='categorical_crossentropy', optimizer='adam', metrics=['accuracy'])

from sklearn.cross_validation import train_test_split
X_train,X_val,Y_train,Y_val = train_test_split(x,y,test_size=0.2)

from keras.preprocessing.image import ImageDataGenerator
datagen = ImageDataGenerator(rotation_range = 20,
                             width_shift_range = 0.15,
                             height_shift_range = 0.15,
                             shear_range = 0.4,
                             zoom_range = 0.3,
                             channel_shift_range = 0.1)

model.fit_generator(datagen.flow(X_train, Y_train, batch_size=256),
                    samples_per_epoch=len(X_train),
                    nb_epoch=100,
                    validation_data=(X_val, Y_val),
                    verbose=1)