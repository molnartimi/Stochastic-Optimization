import sys
import time
sys.path.append("./")
from gpflowopt.domain import ContinuousParameter
import gpflow
from gpflowopt.bo import BayesianOptimizer
from gpflowopt.design import LatinHyperCube # TODO van meg mas is
from gpflowopt.optim import *
from gpflowopt.acquisition import *
from gpflow.priors import *
from spdn.spdn import SPDN
from spdn.spdnexception import SPDNException
from spdn.spdnresult import SPDNResult


class MyGPflowOpt:
    def __init__(self, model, error_value=10000):
        self.model = model
        self.ALGORITHM_ID = "GPFL"
        self.spdn = SPDN(self.model,self.ALGORITHM_ID)
        self.KERNELS = {'exp': MyKernel.exp, 'rbf': MyKernel.rbf, 'm12': MyKernel.m12, 'm32': MyKernel.m12, 'm52': MyKernel.m52}
        self.ACQS = {'ei': MyAcquisition.ei, 'poi': MyAcquisition.poi, 'lcb': MyAcquisition.lcb}
        self.PRIORS = {'gaussian': MyPrior.gaussian, 'lognormal': MyPrior.lognormal, 'gamma': MyPrior.gamma}
        self.ERROR_VALUE = error_value

    def optimize(self, init_points, n_iter, kernel, acquisition, constrain_prior=None, prior_param1=None, prior_param2=None, verbose=False):
        start_time = time.time()
        self.spdn.start(verbose)
        if (self.spdn.running):

            domain = self._getdomain()
            try:
                if constrain_prior is None:
                    result = self._optimize(domain, init_points, n_iter, kernel, acquisition)
                else:
                    result = self._optimize_constrained(domain, init_points, n_iter, kernel, acquisition, constrain_prior, prior_param1, prior_param2)

                result.execution_time = time.time() - start_time
                self.spdn.close()
                result.write_out_to_csv()

                return result
            except Exception as error:
                print(repr(error))
                result = SPDNResult(repr(error), [""]*len(self.model.parameters), self.ALGORITHM_ID,
                                  {"init_points": init_points, "n_iter": n_iter, "kernel": kernel, "acq": acquisition},
                                  self.model)
                result.write_out_to_csv()

    def _getdomain(self):
        domain = None
        for param in self.model.parameters:
            if domain is None:
                domain = ContinuousParameter(param, self.model.borders[param][0], self.model.borders[param][1])
            else:
                domain = domain + ContinuousParameter(param, self.model.borders[param][0], self.model.borders[param][1])
        return domain

    def _optimize(self, domain, init_points, n_iter, kernel, acquisition):
        # Use standard Gaussian process Regression
        lhd = LatinHyperCube(init_points, domain)
        X = lhd.generate()
        Y = self._fx(X)
        # optmodel = gpflow.gpr.GPR(X, Y, gpflow.kernels.Matern52(2, ARD=True))
        optmodel = gpflow.gpr.GPR(X, Y, self.KERNELS[kernel](len(self.model.parameters)))
        optmodel.kern.lengthscales.transform = gpflow.transforms.Log1pe(1e-3) # TODO ???

        # Now create the Bayesian Optimizer
        alpha = self.ACQS[acquisition](optmodel)
        optimizer = BayesianOptimizer(domain, alpha)

        # Run the Bayesian optimization
        with optimizer.silent():
            r = optimizer.optimize(self._fx, n_iter=n_iter)
            return SPDNResult(r['fun'][0], r['x'][0], self.ALGORITHM_ID, {"init_points": init_points, "n_iter": n_iter, "kernel": kernel, "acq": acquisition}, self.model)

    def _optimize_constrained(self, domain, init_points, n_iter, kernel, acquisition, constrain_prior, prior_param1, prior_param2):
        # Initial evaluations
        lhd = LatinHyperCube(init_points, domain)
        X = lhd.generate()
        Yo = self._fx(X)
        Yc = self._constraint(X)

        # Models
        objective_model = gpflow.gpr.GPR(X, Yo, self.KERNELS[kernel](len(self.model.parameters)))
        objective_model.kern.lengthscales.transform = gpflow.transforms.Log1pe(1e-3)
        constraint_model = gpflow.gpr.GPR(np.copy(X), Yc, self.KERNELS[kernel](len(self.model.parameters)))
        constraint_model.kern.lengthscales.transform = gpflow.transforms.Log1pe(1e-3)
        #constraint_model.likelihood.variance.prior = self.PRIORS[constrain_prior](prior_param1,prior_param2) # TODO is it necessary?

        # Setup
        alpha = self.ACQS[acquisition](objective_model)
        pof = ProbabilityOfFeasibility(constraint_model)
        joint = alpha * pof

        # Then run the BayesianOptimizer for n_iter iterations
        optimizer = BayesianOptimizer(domain, joint)
        with optimizer.silent():
            result = optimizer.optimize([self._fx,self._constraint], n_iter)
            return result

    def _fx(self, X):
        X = np.atleast_2d(X)
        result = np.zeros(shape=(X.shape[0], 1))
        for i in range(X.shape[0]):
            row = X[i]
            try:
                fresult = self.spdn.f(row)
                result[i] = fresult
            except SPDNException:
                result[i] = self.ERROR_VALUE
        return result

    def _constraint(self, X):
        X = np.atleast_2d(X)
        result = np.zeros(shape=(X.shape[0], 1))
        for i in range(X.shape[0]):
            row = X[i]
            try:
                const_result = self.spdn.f(row)
                result[i] = const_result # or 1 or something
            except SPDNException:
                result[i] = -1
        return result


class Kernel:
    EXP = 'exp'
    RBF = 'rbf'
    M12 = 'm12'
    M32 = 'm32'
    M52 = 'm52'


class MyKernel:
    @staticmethod
    def exp(input_dim): return gpflow.kernels.Exponential(input_dim, ARD=True)
    @staticmethod
    def rbf(input_dim): return gpflow.kernels.RBF(input_dim, ARD=True)
    @staticmethod
    def m12(input_dim): return gpflow.kernels.Matern12(input_dim, ARD=True)
    @staticmethod
    def m32(input_dim): return gpflow.kernels.Matern32(input_dim, ARD=True)
    @staticmethod
    def m52(input_dim): return gpflow.kernels.Matern52(input_dim, ARD=True)


class Acquisition:
    EI = 'ei'
    POI = 'poi'
    LCB = 'lcb'


class MyAcquisition:
    @staticmethod
    def ei(optmodel): return ExpectedImprovement(optmodel)
    @staticmethod
    def poi(optmodel): return ProbabilityOfImprovement(optmodel)
    @staticmethod
    def lcb(optmodel): return LowerConfidenceBound(optmodel)

class Prior:
    GAUSSIAN = 'gaussian'
    LOGNORMAL = 'lognormal'
    GAMMA = 'gamma'
    UNIFORM = 'uniform'

class MyPrior:
    @staticmethod
    def gaussian(mu,variance): return Gaussian(mu,variance)
    @staticmethod
    def lognormal(mu,variance): return LogNormal(mu,variance)
    @staticmethod
    def gamma(shape,scale): return Gamma(shape,scale)
    {staticmethod}
    def uniform(lower,upper): return Uniform(lower,upper)