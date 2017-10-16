import sys
sys.path.append("./")
import numpy as np
from gpflowopt.domain import ContinuousParameter
import gpflow
from gpflowopt.bo import BayesianOptimizer
from gpflowopt.design import LatinHyperCube
from gpflowopt.acquisition import *
from spdn.spdn import SPDN
from spdn.spdnexception import SPDNException
from logger.csvwriter import CsvWriter
from enum import Enum


class MyGPflowOpt:
    def __init__(self, model):
        self.model = model;
        self.ALGORITHM_ID = "GPFL"
        self.spdn = SPDN(self.model)
        self.csv_writer = CsvWriter(self.model.id,self.ALGORITHM_ID)
        self._write_csv_header()
        KERNELS = MyKernels(len(self.model.parameters))
        self.kernels = {'exp': KERNELS.exp, 'm12': KERNELS.m12, 'm32': KERNELS.m12, 'm52': KERNELS.m52}

    def optimize(self, init_points, n_iter, kernel, acquisition, verbose=False):
        self.spdn.start(verbose)
        if (self.spdn.running):

            domain = self._getdomain()
            try:
                result = self._optimize(domain, init_points, n_iter, kernel, acquisition)
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
        optmodel = gpflow.gpr.GPR(X,Y, self.kernels[kernel]())
        optmodel.kern.lengthscales.transform = gpflow.transforms.Log1pe(1e-3) # TODO ???

        # Now create the Bayesian Optimizer
        ACQ = MyAcquisition(optmodel)
        acqs = {'ei': ACQ.ei, 'poi': ACQ.poi, 'lcb': ACQ.lcb}
        alpha = acqs[acquisition]()
        optimizer = BayesianOptimizer(domain, alpha)

        # Run the Bayesian optimization
        with optimizer.silent():
            r = optimizer.optimize(self._fx, n_iter=n_iter)
            return r

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

class Kernels(Enum):
    EXP = 'exp'
    M12 = 'm12'
    M32 = 'm32'
    M52 = 'm52'


class MyKernels:
    def __init__(self, input_dim):
        self.input_dim = input_dim

    def exp(self): return gpflow.kernels.Exponential(self.input_dim)
    def m12(self): return gpflow.kernels.Matern12(self.input_dim)
    def m32(self): return gpflow.kernels.Matern32(self.input_dim)
    def m52(self): return gpflow.kernels.Matern52(self.input_dim)


class Acquisition(Enum):
    EI = 'ei'
    POI = 'poi'
    LCB = 'lcb'
    # TODO
    POF = 'pof'


class MyAcquisition():
    def __init__(self, optmodel):
        self.optmodel = optmodel

    def ei(self): return ExpectedImprovement(self.optmodel)
    def poi(self): return ProbabilityOfImprovement(self.optmodel)
    def lcb(self): return LowerConfidenceBound(self.optmodel)
    def pof(self): return ProbabilityOfFeasibility(self.optmodel)