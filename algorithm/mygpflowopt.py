import sys
sys.path.append("./")
from gpflowopt.domain import ContinuousParameter
import gpflow
from gpflowopt.bo import BayesianOptimizer
from gpflowopt.design import LatinHyperCube
from gpflowopt.acquisition import *
from spdn.spdn import SPDN
from spdn.spdnexception import SPDNException
from logger.csvwriter import CsvWriter
from gpflowopt.optim import *


class MyGPflowOpt:
    def __init__(self, model):
        self.model = model;
        self.ALGORITHM_ID = "GPFL"
        self.spdn = SPDN(self.model)
        self.csv_writer = CsvWriter(self.model.id,self.ALGORITHM_ID)
        self._write_csv_header()
        self.KERNELS = {'exp': MyKernel.exp, 'm12': MyKernel.m12, 'm32': MyKernel.m12, 'm52': MyKernel.m52}
        self.ACQS = {'ei': MyAcquisition.ei, 'poi': MyAcquisition.poi, 'lcb': MyAcquisition.lcb}

    def optimize(self, init_points, n_iter, kernel, acquisition, constrained=False, verbose=False):
        self.spdn.start(verbose)
        if (self.spdn.running):

            domain = self._getdomain()
            try:
                if not constrained:
                    result = self._optimize(domain, init_points, n_iter, kernel, acquisition)
                else:
                    result = self._optimize_constrained(domain, init_points, n_iter, kernel, acquisition)

                self.spdn.close()
                self._write_csv_result(init_points,n_iter,kernel,acquisition,result)

                return result
            except Exception as error:
                print(repr(error))
                self._write_csv_result(init_points,n_iter,kernel,acquisition,'Error: ' + repr(error))

    def _getdomain(self):
        domain = None
        for key in self.model.borders:
            if domain is None:
                domain = ContinuousParameter(key, self.model.borders[key][0], self.model.borders[key][1])
            else:
                domain = domain + ContinuousParameter(key, self.model.borders[key][0], self.model.borders[key][1])
        return domain

    def _optimize(self, domain, init_points, n_iter, kernel, acquisition):
        # Use standard Gaussian process Regression
        lhd = LatinHyperCube(init_points, domain)
        X = lhd.generate()
        Y = self._fx(X)
        #optmodel = gpflow.gpr.GPR(X, Y, gpflow.kernels.Matern52(2, ARD=True))
        optmodel = gpflow.gpr.GPR(X, Y, self.KERNELS[kernel](len(self.model.parameters)))
        optmodel.kern.lengthscales.transform = gpflow.transforms.Log1pe(1e-3) # TODO ???

        # Now create the Bayesian Optimizer
        alpha = self.ACQS[acquisition](optmodel)
        optimizer = BayesianOptimizer(domain, alpha)

        # Run the Bayesian optimization
        with optimizer.silent():
            r = optimizer.optimize(self._fx, n_iter=n_iter)
            return r

    def _optimize_constrained(self, domain, init_points, n_iter, kernel, acquisition):
        # Initial evaluations
        lhd = LatinHyperCube(init_points, domain)
        X = lhd.generate()
        Yo = self._fx(X)
        Yc = np.copy(Yo)

        # Models
        objective_model = gpflow.gpr.GPR(X, Yo, self.KERNELS[kernel]())
        objective_model.likelihood.variance = 0.01
        constraint_model = gpflow.gpr.GPR(np.copy(X), Yc, self.KERNELS[kernel]())
        constraint_model.kern.lengthscales.transform = gpflow.transforms.Log1pe(1e-3)
        constraint_model.likelihood.variance = 0.01
        constraint_model.likelihood.variance.prior = gpflow.priors.Gamma(1. / 4., 1.0)

        # Setup
        ACQ = MyAcquisition(objective_model)
        acqs = {'ei': ACQ.ei, 'poi': ACQ.poi, 'lcb': ACQ.lcb}
        alpha = acqs[acquisition]()
        pof = ProbabilityOfFeasibility(constraint_model)
        joint = alpha * pof

        # First setup the optimization strategy for the acquisition function
        # Combining MC step followed by L-BFGS-B
        acquisition_opt = StagedOptimizer([MCOptimizer(domain, 200), SciPyOptimizer(domain)])

        # Then run the BayesianOptimizer for 50 iterations
        optimizer = BayesianOptimizer(domain, joint, optimizer=acquisition_opt)
        with optimizer.silent():
            result = optimizer.optimize([self._fx,self._fx], n_iter)
            return result

    def _fx(self, X):
        X = np.atleast_2d(X)
        result = np.zeros(shape=(X.shape[0], 1))
        for i in range(X.shape[0]):
            row = X[i]
            fresult = self.spdn.f(row)
            result[i] = fresult
        return result

    def _write_csv_header(self):
        row = ['init points', 'n iter', 'kernel', 'acquisition', 'MAX VALUE']
        for param in self.model.parameters: row.append(param + ' (' + str(self.model.validvalues[param]) + ')')
        self.csv_writer.write(row)

    def _write_csv_result(self, init_points, n_iter, kernel, acquisition, result):
        row = [init_points, n_iter, kernel, acquisition]
        if 'Error' in result: row.append(result)
        else:
            row.append(result['fun'][0])
            for param in result['x'][0]: row.append(str(param))
        self.csv_writer.write(row)

    def __del__(self):
        self.csv_writer.close()


class Kernel:
    EXP = 'exp'
    M12 = 'm12'
    M32 = 'm32'
    M52 = 'm52'


class MyKernel:
    def exp(input_dim): return gpflow.kernels.Exponential(input_dim)
    def m12(input_dim): return gpflow.kernels.Matern12(input_dim)
    def m32(input_dim): return gpflow.kernels.Matern32(input_dim)
    def m52(input_dim): return gpflow.kernels.Matern52(input_dim)


class Acquisition:
    EI = 'ei'
    POI = 'poi'
    LCB = 'lcb'


class MyAcquisition():
    def ei(optmodel): return ExpectedImprovement(optmodel)
    def poi(optmodel): return ProbabilityOfImprovement(optmodel)
    def lcb(optmodel): return LowerConfidenceBound(optmodel)