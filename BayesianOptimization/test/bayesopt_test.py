import unittest
import sys
sys.path.append("./")
import models
from algorithm.mybayesopt import MyBasienOptimization, Acquisition


class BayesianOptimizationTest(unittest.TestCase):
    #@unittest.skip("It works fine")
    def test_simple_server(self):
        self._run_tests(models.simple_server, 10, 10)

    @unittest.skip("too long")
    def test_vcl_stochastic(self):
        self._run_tests(models.vcl_stochastic, 10, 20)

    @unittest.skip("too long")
    def test_hybrid_clour(self):
        self._run_tests(models.hybrid_cloud,10,20)

    def _run_tests(self, model, init_points, n_iter):
        testcases = [#{'acq': Acquisition.LCB, 'acq_param': 1},
                     #{'acq': Acquisition.LCB, 'acq_param': 5},
                     #{'acq': Acquisition.LCB, 'acq_param': 10},
                     #{'acq': Acquisition.EI, 'acq_param': 0},
                     {'acq': Acquisition.EI, 'acq_param': 0.1},
                     {'acq': Acquisition.EI, 'acq_param': 2}]
                     #{'acq': Acquisition.POI, 'acq_param': 0},
                     #{'acq': Acquisition.POI, 'acq_param': 0.1},
                     #{'acq': Acquisition.POI, 'acq_param': 2}]

        bayo = MyBasienOptimization(model)

        for case in testcases:
            result = bayo.optimize(init_points,n_iter,case['acq'],case['acq_param'])
            print(result)


if __name__ == '__main__':
    unittest.main()
