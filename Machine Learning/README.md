# Machine Learning

Deep learning models that achieve state-of-the-art on predicting air pollution levels were developed in Keras using Python and trained on a Titan X Pascal GPU.  Over 20 variations of deep convolutional neural networks were developed and optimized by varying one of many hyperparameters, including the number of convolutional layers, number of maxpool layers, size of convolutional layers, use of dropout, and use of data augmentation.  In the end, one model achieved an accuracy rate of 85.15% in binary classification of pollution, and another model achieved an accuracy rate of 72.70% in 10-class classification of pollution.  These models achieve state-of-the-art in this task while using a novel method that is **reliable, scalable, standardized, not limited, inexpensive, and simple**.  This novel research demonstrates that satellite images, which are inexpensive and ubiquitous, are accurate in predicting air pollution.

![Results](https://github.com/arnavbansal1/SatellitePollutionCNN/blob/master/Images/Results.png)

A basic, high-level excerpt of the deep convolutional neural networks code using Keras in Python:

```python
model = Sequential()
model.add(Convolution2D(nb_filters, nb_conv, nb_conv, activation ='relu'))
model.add(MaxPooling2D(pool_size=(nb_pool, nb_pool)))
... 
model.add(Dropout(0.5))
model.add(Flatten())
model.add(Dense(128, activation ='relu'))
model.add(Dropout(0.5))
...
model.add(Dense(2, activation ='softmax’))
```

A variety of models were trained to predict the BAQI using the satellite images.  The following hyperparameters were varied and optimized:

Number of Convolutional Layers (1, 2, 3, 4, 5, 6)
Allows for prediction of more complex features
Usually increases accuracy but also overfitting

Number of MaxPooling Layers (1, 2, 3, 4, 5, 6)
Allows for finding features on different scales
Allows for improved efficiency, via reduction of image resolution 
Usually increases accuracy

Size of Convolutional Layers (16, 32, 64, 128, 256, 512)
Increases the number of features that can be detected
Usually increases accuracy but also overfitting

Dropout (0 – i.e. not used, 0.25, 0.5)
Reduces overfitting by randomly excluding some nodes during training, as this prevents feature detection from relying on any specific nodes
Usually decreases accuracy but increases validation accuracy

Data Augmentation (Not used, Used)
Allows for finding features that are invariant to transformations
Easier and more memory efficient than manual generation of more data
Usually increases accuracy for object detection problems

Output Loss Estimators
2-class (binary), 5-class, 10-class, and 101-class cross-entropy
Number of categories (intervals of the 0 to 100 scale) can indicate the model’s accuracy
The model may also consider close or almost-correct values as wrong, especially as the number of categories approaches the data variance

The 15 models were trained on a Titan X Pascal GPU, with training time varying between 0.25 and 1.25 hours.
