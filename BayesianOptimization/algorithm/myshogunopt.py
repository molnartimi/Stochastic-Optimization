from modshogun import *
import numpy as np
from scipy.stats import norm
from scipy.optimize import minimize
import sys
import time
sys.path.append("./")
from spdn.spdn import SPDN
from spdn.spdnexception import SPDNException
from spdn.spdnresult import SPDNResult

class MyShogunOpt:
    def __init__(self,model,error_value=10000):
        self.model = model
        self.init_points = None
        self.tau = 5 # initial value, we will calculate the best value
        self.XTOL = 0.01
        self.ALGORITHM_ID = 'SHOG'
        self.ERROR_VALUE = error_value

        self.kernel = GaussianKernel(10, self.tau)
        self.mean = ZeroMean()
        self.gauss = GaussianLikelihood()
        self.lik = LogitLikelihood() # TODO or ProbitLikelihood()
        self.gradcalc = GradientCriterion()

        self.train_points = []
        self.train_values = []
        self.train_classes = []

        self.MIN_value = None
        self.MIN_point = []

        self.spdn = SPDN(self.model,self.ALGORITHM_ID)

    def optimize(self,max_iter=20,init_points=20,verbose=False):
        start_time = time.time()
        self.spdn.start(verbose)
        if self.spdn.running:
            self.init_points = init_points
            self._init()

            idx = 0
            while self.MIN_value > self.XTOL and idx < max_iter:
                reg_gp, class_gp = self._train_gps()
                # EI function
                def ei_pof(x):
                    X = self._array_1_to_2(x,len(self.model.parameters),1)
                    mean, variance, class_probability = self.get_mean_variance_classprobability(X, reg_gp)
                    phi = norm(loc=mean[0],scale=variance[0])
                    result = - (((self.MIN_value - mean[0]) * phi.cdf(self.MIN_value) + variance[0] * phi.pdf(self.MIN_value)) * class_probability)
                    return result
                # max of EI function
                res = minimize(ei_pof, np.array(self.MIN_point), method='nelder-mead',
                               options={'xtol': 0.01})

                # update with maxEI
                self._update_gps(MyShogunOpt._array_1_to_2(res.x, len(self.model.parameters), 1))
                idx += 1

            result = SPDNResult(self.MIN_value, self.MIN_point, self.ALGORITHM_ID, {"init_points": self.init_points, "max_iter": max_iter}, self.model)
            result.execution_time = time.time() - start_time
            self.spdn.close()
            result.write_out_to_csv()

            return result

    def get_mean_variance_classprobability(self, points, reg_gp=None, class_gp=None):
        if reg_gp is None or class_gp is None:
            reg_gp, class_gp = self._train_gps()

        feats_test = RealFeatures(np.atleast_2d(points))

        means = reg_gp.get_mean_vector(feats_test)
        variance = reg_gp.get_variance_vector(feats_test)
        class_probability = class_gp.get_probabilities(feats_test)

        return means, variance, class_probability


    def _train_gps(self):
        feats_train = RealFeatures(np.atleast_2d(self.train_points))
        reg_labels_train = RegressionLabels(np.atleast_1d(self.train_values))
        class_labels_train = BinaryLabels(np.atleast_1d(self.train_classes))

        reg_inf = ExactInferenceMethod(self.kernel, feats_train, self.mean, reg_labels_train, self.gauss)
        class_inf = SingleLaplaceInferenceMethod(self.kernel, feats_train, self.mean, class_labels_train, self.lik)

        reg_gp = GaussianProcessRegression(reg_inf)
        class_gp = GaussianProcessClassification(class_inf)

        reg_gp = self._helper_gp_trainer(reg_gp,reg_inf,feats_train,reg_labels_train)
        class_gp = self._helper_gp_trainer(class_gp,class_inf,feats_train,class_labels_train)

        return reg_gp, class_gp

    def _helper_gp_trainer(self,gp,inf,feats_train,labels_train):
        grad = GradientEvaluation(gp, feats_train, labels_train, self.gradcalc, False)

        grad.set_function(inf)

        grad_search = GradientModelSelection(grad)
        best_combination = grad_search.select_model()
        best_combination.apply_to_machine(gp)

        gp.train()
        return gp

    def _update_gps(self, point):
        for i in range(len(point)):
            #print("train_points[i]",self.train_points[i],"points[i]",points[i],"egybe +=",self.train_points[i] + points[i])
            self.train_points[i].append(point[i])

        row = np.atleast_2d(point)[:, 0].tolist()
        try:
            result = self.spdn.f(row)
            self.train_values.append(result)
            self.train_classes.append(1)

            if result < self.MIN_value:
                self.MIN_value = result
                self.MIN_point = row
        except SPDNException:
            self.train_values.append(self.ERROR_VALUE)
            self.train_classes.append(-1)

    def _init(self):
        # training points
        self._randomize_train_points()
        self._calculate_train_values()

    def _randomize_train_points(self):
        for param in self.model.parameters:
            lower = self.model.borders[param][0]
            upper = self.model.borders[param][1]
            self.train_points.append((np.random.rand(self.init_points) * (upper - lower) + lower).tolist())

    def _calculate_train_values(self):
        for i in range(self.init_points):
            row = []
            for j in range(len(self.model.parameters)):
                row.append(self.train_points[j][i])
            try:
                fresult = self.spdn.f(row)
                self.train_values.append(fresult)
                self.train_classes.append(1)

                if self.MIN_value is None or fresult < self.MIN_value:
                    self.MIN_value = fresult
                    self.MIN_point = row
            except SPDNException:
                self.train_values.append(self.ERROR_VALUE) # TODO is it necessary?
                self.train_classes.append(-1)

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
