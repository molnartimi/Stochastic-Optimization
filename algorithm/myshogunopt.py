from modshogun import *
import numpy as np
from scipy.stats import norm
from scipy.optimize import minimize
import matplotlib.pyplot as plt
import sys
sys.path.append("./")
from spdn.spdn import SPDN

class MyShogunOpt:
    def __init__(self,model,init_points):
        self.model = model
        self.init_points = init_points
        self.tau = 5 # initial value, we will calculate the best value
        self.XTOL = 0.01

        self.kernel = GaussianKernel(10, self.tau)
        self.mean = ZeroMean()
        self.gauss = GaussianLikelihood()
        self.gradcalc = GradientCriterion()

        self.train_points = []
        self.train_values = []

        self.MIN_value = None
        self.MIN_point = []

        self.spdn = SPDN(self.model)
        self.spdn.start()

        self._init()

    def _init(self):
        # training points
        self._randomize_train_points()
        self._calculate_train_values()

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

            if self.MIN_value is None or fresult<self.MIN_value:
                self.MIN_value = fresult
                self.MIN_point = row

    def optimize(self,max_iter):
        print("It is also pseudo code")
        # for MAX_ITER iteration
        idx = 0
        while self.MIN_value > self.XTOL or idx < max_iter:
            # EI function
            def ei(x):
                mean, variance = self.get_mean(x) # TODO not valid return values yet
                phi = norm(loc=mean,scale=variance)
                return - (self.MIN_value - mean) * phi.cdf(x) + variance * phi.pdf(x)
            # max of EI function
            res = minimize(ei, np.array(self.MIN_point), method='nelder-mead',
                           options={'xtol': 0.01, 'disp': True})

            # update with maxEI
            self._update_gp(self._array_1_to_2(res.x))
        return (self.MIN_value, self.MIN_point)

    def get_mean(self,points):
        feats_train = RealFeatures(np.atleast_2d(self.train_points))
        labels_train = RegressionLabels(np.atleast_1d(self.train_values))

        inf = ExactInferenceMethod(self.kernel, feats_train, self.mean, labels_train, self.gauss)

        gp = GaussianProcessRegression(inf)

        grad = GradientEvaluation(gp, feats_train, labels_train, self.gradcalc, False)
        grad.set_function(inf)

        grad_search = GradientModelSelection(grad)
        best_combination = grad_search.select_model()
        best_combination.apply_to_machine(gp)

        gp.train()

        feats_test = RealFeatures(np.atleast_2d(points))

        means = gp.get_mean_vector(feats_test)
        variance = gp.get_variance_vector(feats_test)
        print(means)
        print(variance)
        return means

    def _update_gp(self,points):
        for i in range(len(points)):
            self.train_points[i].append(points[i])

        for i in range(len(points[0])):
            row = np.atleast_2d(points)[:, i].tolist()
            np.append(self.train_values, self.spdn.f(row))

    def plot_posterior_mean(self,intervals):
        feats_train = RealFeatures(np.atleast_2d(self.train_points))
        labels_train = RegressionLabels(np.atleast_1d(self.train_values))

        inf = ExactInferenceMethod(self.kernel, feats_train, self.mean, labels_train, self.gauss)
        gp = GaussianProcessRegression(inf)

        grad = GradientEvaluation(gp, feats_train, labels_train, self.gradcalc, False)
        grad.set_function(inf)

        grad_search = GradientModelSelection(grad)
        best_combination = grad_search.select_model()
        best_combination.apply_to_machine(gp)

        # we have to "cast" objects to the specific kernel interface we used (soon to be easier)
        best_width = GaussianKernel.obtain_from_generic(inf.get_kernel()).get_width()
        best_scale = inf.get_scale()
        best_sigma = GaussianLikelihood.obtain_from_generic(inf.get_model()).get_sigma()

        # print("Selected tau (kernel bandwidth):", best_width)
        # print("Selected gamma (kernel scaling):", best_scale)
        # print("Selected sigma (observation noise):", best_sigma)


        gp.train()

        XY_test = self._test_matrix(intervals)
        feats_test = RealFeatures(np.atleast_2d(XY_test))

        means = gp.get_mean_vector(feats_test)
        M = self._array_1_to_2(means, intervals[0], intervals[1])

        borders = []
        for param in self.model.borders:
            borders.append([self.model.borders[param][0], self.model.borders[param][1]])

        plt.imshow(M, interpolation="nearest", aspect='auto', origin='lower',
                   extent=(borders[0][0], borders[0][1], borders[1][0], borders[1][1]))
        plt.plot(self.train_points[0], self.train_points[1], '*', color='black')
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