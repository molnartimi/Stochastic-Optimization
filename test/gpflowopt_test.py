import unittest
import numpy as np
from gpflowopt.domain import ContinuousParameter
import gpflow
from gpflowopt.bo import BayesianOptimizer
from gpflowopt.design import LatinHyperCube
from gpflowopt.acquisition import ExpectedImprovement
from spdn.spdn import SPDN
import models


class GPflowOptTest(unittest.TestCase):

    def _fx(self, X):
        X = np.atleast_2d(X)
        result = np.zeros(shape=(X.shape[0], 1))
        for i in range(X.shape[0]):
            row = X[i]
            fresult = self.spdn.f(row)
            result[i] = fresult
        return result

    def _getdomain(self):
        domain = None
        for key in self.model.borders:
            if domain is None:
                domain = ContinuousParameter(key, self.model.borders[key][0], self.model.borders[key][1])
            else:
                domain = domain + ContinuousParameter(key, self.model.borders[key][0], self.model.borders[key][1])
        return domain

    def _optimize(self, domain):
        # Use standard Gaussian process Regression
        lhd = LatinHyperCube(21, domain)
        X = lhd.generate()
        Y = self._fx(X)
        model = gpflow.gpr.GPR(X, Y, gpflow.kernels.Matern52(2, ARD=True))
        model.kern.lengthscales.transform = gpflow.transforms.Log1pe(1e-3)

        # Now create the Bayesian Optimizer
        alpha = ExpectedImprovement(model)
        optimizer = BayesianOptimizer(domain, alpha)

        # Run the Bayesian optimization
        with optimizer.silent():
            r = optimizer.optimize(self._fx, n_iter=5)
        return r

    def _test(self,model):
        self.model = model
        self.spdn = SPDN(self.model)
        self.spdn.start()
        if (self.spdn.running):
            domain = self._getdomain()
            result = self._optimize(domain)
            self.spdn.close()
            return result

    def test_simple_server(self):
        result = self._test(models.simple_server)
        print(result)

    def test_vcl_stochastic(self):
        result = self._test(models.vcl_stochastic)
        print(result)


if __name__ == '__main__':
    unittest.main()