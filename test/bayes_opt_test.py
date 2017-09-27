import unittest
import sys
sys.path.append("./")
from bayes_opt import BayesianOptimization
from spdn.spdn import SPDN
import models

class BayesianOptimizationTest(unittest.TestCase):

    def test_simple_server(self):

        spdn = SPDN(models.simple_server)
        spdn.start()
        if (spdn.running):
            # print(spdn.f([1.5, 0.25]))
            bo = BayesianOptimization(lambda requestRate, serviceTime: - spdn.f([requestRate, serviceTime]),
                                      {'requestRate': (0.0001, 3), 'serviceTime': (0.0001, 1)})
            # bo.explore({'requestRate': [0.4, 2.0], 'serviceTime': [0.1, 0.6]})
            bo.maximize(init_points=10, n_iter=15, kappa=2, acq='ei')
            print(bo.res['max'])
            spdn.close()

    def test_vcl_stochastic(self):
        spdn = SPDN(models.vcl_stochastic)
        spdn.start()
        if (spdn.running):
            # print(spdn.f([0.015, 0.5, 0.15, 60, 5, 0.75, 0.6]))
            bo = BayesianOptimization(lambda incomingRate, dispatchTime, warmDispatchTime, jobTime, powerTime,
                                             powerUsage, idlePowerFactor: - spdn.f([incomingRate, dispatchTime,
                                                                                    warmDispatchTime, jobTime,
                                                                                    powerTime, powerUsage,
                                                                                    idlePowerFactor]),
                                      {'incomingRate': (0.0001, 1), 'dispatchTime': (0.0001, 3),
                                       'warmDispatchTime': (0.0001, 2), 'jobTime': (0.0001, 200),
                                       'powerTime': (0.0001, 20), 'powerUsage': (0.0001, 5),
                                       'idlePowerFactor': (0.0001, 5)})
            bo.maximize(init_points=10, n_iter=15, kappa=2, acq='ei')
            print(bo.res['max'])
            spdn.close()

if __name__ == '__main__':
    unittest.main()
