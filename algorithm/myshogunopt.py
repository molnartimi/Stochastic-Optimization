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

    def optimize(self,max_iter):
        # for MAX_ITER iteration
        idx = 0
        while self.MIN_value > self.XTOL or idx < max_iter:
            # EI function
            def ei(x):
                mean, variance = self.get_mean_and_variance(x) # TODO not valid return values yet
                phi = norm(loc=mean,scale=variance)
                return - (self.MIN_value - mean) * phi.cdf(x) + variance * phi.pdf(x)
            # max of EI function
            res = minimize(ei, np.array(self.MIN_point), method='nelder-mead',
                           options={'xtol': 0.01, 'disp': True})

            # update with maxEI
            self._update_gp(MyShogunOpt._array_1_to_2(res.x))
        return (self.MIN_value, self.MIN_point)

    def get_mean_and_variance(self, points):
        gp = self._train_gp()

        feats_test = RealFeatures(np.atleast_2d(points))

        means = gp.get_mean_vector(feats_test)
        variance = gp.get_variance_vector(feats_test)

        return means, variance

    def plot_posterior_mean(self,intervals):
        XY_test = self._test_matrix(intervals)
        means, _ = self.get_mean_and_variance(XY_test)

        M = MyShogunOpt._array_1_to_2(means, intervals[0], intervals[1])

        borders = []
        for param in self.model.parameters:
            borders.append([self.model.borders[param][0], self.model.borders[param][1]])

        plt.imshow(M, interpolation="nearest", aspect='auto', origin='lower',
                   extent=(borders[0][0], borders[0][1], borders[1][0], borders[1][1]))
        plt.plot(self.train_points[0], self.train_points[1], '*', color='black')
        plt.colorbar()
        plt.show()

    def _train_gp(self):
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
        return gp

    def _update_gp(self,points):
        for i in range(len(points)):
            self.train_points[i].append(points[i])

        for i in range(len(points[0])):
            row = np.atleast_2d(points)[:, i].tolist()
            np.append(self.train_values, self.spdn.f(row))

    def _randomize_train_points(self):
        for param in self.model.parameters:
            lower = self.model.borders[param][0]
            upper = self.model.borders[param][1]
            self.train_points.append(np.random.rand(self.init_points) * (upper - lower) + lower)

    def _calculate_train_values(self):
        for i in range(self.init_points):
            row = []
            for j in range(len(self.model.parameters)):
                row.append(self.train_points[j][i])
            fresult = self.spdn.f(row)
            self.train_values.append(fresult)

            if self.MIN_value is None or fresult<self.MIN_value:
                self.MIN_value = fresult
                self.MIN_point = row

    def _test_matrix(self,intervals):
        XY_test = [[], []]
        interval_sizes = []
        for param in self.model.parameters:
            interval_sizes.append(self.model.borders[param][1] - self.model.borders[param][0])

        for i in range(intervals[0]):
            for j in range(intervals[1]):
                XY_test[0].append(interval_sizes[0] / float(intervals[0]) * i)
                XY_test[1].append(interval_sizes[1] / float(intervals[1]) * j)

        return XY_test

    @staticmethod
    def _array_1_to_2(array,row,col):
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

    def _get_hyperparams(self):
        feats_train = RealFeatures(np.atleast_2d(self.train_points))
        labels_train = RegressionLabels(np.atleast_1d(self.train_values))

        inf = ExactInferenceMethod(self.kernel, feats_train, self.mean, labels_train, self.gauss)

        best_width = GaussianKernel.obtain_from_generic(inf.get_kernel()).get_width() # selected tau (kernel bandwith)
        best_scale = inf.get_scale() # selected gamma (kernel scaling)
        best_sigma = GaussianLikelihood.obtain_from_generic(inf.get_model()).get_sigma() # selected sigma (observation noise)

        # print("Selected tau (kernel bandwidth):", best_width)
        # print("Selected gamma (kernel scaling):", best_scale)
        # print("Selected sigma (observation noise):", best_sigma)

        return best_width, best_scale, best_sigma