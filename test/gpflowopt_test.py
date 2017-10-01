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

    def test_simple_server(self):

        self.spdn = SPDN(models.simple_server)
        self.spdn.start()
        if (self.spdn.running):
            domain = ContinuousParameter('requestRate', 0.0001, 3) + ContinuousParameter('serviceTime', 0.0001, 1)
            result = self._optimize(domain)
            print(result)
            self.spdn.close()

    def test_vcl_stochastic(self):

        self.spdn = SPDN(models.vcl_stochastic)
        self.spdn.start()
        if (self.spdn.running):
            domain = ContinuousParameter('incomingRate', 0.0001, 1) + ContinuousParameter('dispatchTime', 0.0001, 3) +\
                ContinuousParameter('warmDispatchTime', 0.0001, 2) + ContinuousParameter('jobTime', 0.0001, 200) +\
                ContinuousParameter('powerTime', 0.0001, 20) + ContinuousParameter('powerUsage', 0.0001, 5) +\
                ContinuousParameter('idlePowerFactor', 0.0001, 5)

            result = self._optimize(domain)
            print(result)
            self.spdn.close()


if __name__ == '__main__':
    unittest.main()