import sys
sys.path.append("./")
from modshogun import *
import numpy as np
import matplotlib.pyplot as plt
from spdn.spdn import SPDN

class MyShogunOpt:
    def __init__(self,model,init_points,tau):
        self.model = model
        self.init_points = init_points
        self.tau = tau
        self.kernel = GaussianKernel(10, self.tau)
        self.mean = ZeroMean()
        self.gauss = GaussianLikelihood()
        self.train_points = []
        self.train_values = []
        self.spdn = SPDN(self.model)
        self.spdn.start()

        self._init()

    def _init(self):
        # training points
        self._randomize_train_points()
        self._calculate_train_values()

    def get_mean(self,points):
        feats_train = RealFeatures(np.atleast_2d(self.train_points))
        labels_train = RegressionLabels(np.atleast_1d(self.train_values))

        inf = ExactInferenceMethod(self.kernel, feats_train, self.mean, labels_train, self.gauss)

        gp = GaussianProcessRegression(inf)
        gp.train()

        feats_test = RealFeatures(np.atleast_2d(points))

        means = gp.get_mean_vector(feats_test)
        print(means)


    def _randomize_train_points(self):
        for param in self.model.borders:
            lower = self.model.borders[param][0]
            upper = self.model.borders[param][1]
            self.train_points.append(np.random.rand(self.init_points) * (upper - lower) + lower)

    def _calculate_train_values(self):
        for i in range(self.init_points):
            row = [self.train_points[0][i],self.train_points[1][i]]
            fresult = self.spdn.f(row)
            self.train_values.append(fresult)

    def plot_posterior_mean(self,intervals):
        feats_train = RealFeatures(np.atleast_2d(self.train_points))
        labels_train = RegressionLabels(np.atleast_1d(self.train_values))

        inf = ExactInferenceMethod(self.kernel, feats_train, self.mean, labels_train, self.gauss)

        gp = GaussianProcessRegression(inf)
        gp.train()

        XY_test = self._test_matrix(intervals)

        feats_test = RealFeatures(np.atleast_2d(XY_test))

        means = gp.get_mean_vector(feats_test)

        M = self._array_1_to_2(means, intervals[0], intervals[1])

        borders = []
        for param in self.model.borders:
            borders.append([self.model.borders[param][0], self.model.borders[param][1]])

        plt.imshow(M, interpolation="nearest", aspect='auto', origin='lower', extent=(borders[0][0],
                                                                                      borders[0][1],
                                                                                      borders[1][0],
                                                                                      borders[1][1]))
        plt.colorbar()
        plt.show()

    def _test_matrix(self,intervals):
        XY_test = [[], []]
        interval_sizes = []
        for param in self.model.borders:
            interval_sizes.append(self.model.borders[param][1] - self.model.borders[param][0])

        for i in range(intervals[0]):
            for j in range(intervals[1]):
                XY_test[0].append(interval_sizes[0] / float(intervals[0]) * i)
                XY_test[1].append(interval_sizes[1] / float(intervals[1]) * j)

        return XY_test

    def _array_1_to_2(self,array,row,col):
        idx = 0
        result = []
        for i in range(row):
            row = []
            for j in range(col):
                row.append(array[idx])
                idx += 1
            result.append(row)
        result = np.atleast_2d(result)
        return result