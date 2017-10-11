import sys
sys.path.append("./")
import numpy as np
from gpflowopt.domain import ContinuousParameter
import gpflow
from gpflowopt.bo import BayesianOptimizer
from gpflowopt.design import LatinHyperCube
from gpflowopt.acquisition import ExpectedImprovement
from spdn.spdn import SPDN
from spdn.spdnexception import SPDNException
from logger.csvwriter import CsvWriter


class MyGPflowOpt:
    def optimize(self, model, init_points, n_iter, verbose=False):
        self.ALGORITHM_ID = "GPFL"
        self.model = model
        self.spdn = SPDN(self.model)
        self.spdn.start(verbose)
        if (self.spdn.running):
            csvwriter = CsvWriter(model.id,self.ALGORITHM_ID)
            row = ['init points', 'n iter', 'MAX VALUE']
            for param in model.parameters: row.append(param)
            csvwriter.write(row)

            domain = self._getdomain()
            try:
                result = self._optimize(domain, init_points, n_iter)
                self.spdn.close()

                row = [init_points, n_iter, result['fun'][0]]
                for param in result['x'][0]: row.append(str(param))
                csvwriter.write(row)
                csvwriter.close()

                return result
            except SPDNException as error:
                print(repr(error))
                csvwriter.close()

    def _getdomain(self):
        domain = None
        for key in self.model.borders:
            if domain is None:
                domain = ContinuousParameter(key, self.model.borders[key][0], self.model.borders[key][1])
            else:
                domain = domain + ContinuousParameter(key, self.model.borders[key][0], self.model.borders[key][1])
        return domain

    def _optimize(self, domain, init_points, n_iter):
        # Use standard Gaussian process Regression
        lhd = LatinHyperCube(init_points, domain)
        X = lhd.generate()
        Y = self._fx(X)
        model = gpflow.gpr.GPR(X, Y, gpflow.kernels.Matern52(2, ARD=True))
        model.kern.lengthscales.transform = gpflow.transforms.Log1pe(1e-3)

        # Now create the Bayesian Optimizer
        alpha = ExpectedImprovement(model)
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
