import unittest
import sys
sys.path.append("./")
import models
from algorithm.mygpflowopt import MyGPflowOpt


class GPflowOptTest(unittest.TestCase):
    #@unittest.skip("works fine")
    def test_simple_server(self):
        gpflowopt = MyGPflowOpt()
        result = gpflowopt.optimize(models.simple_server, init_points=5, n_iter=10, verbose=True)
        print(result)

    def test_vcl_stochastic(self):
        gpflowopt = MyGPflowOpt()
        result = gpflowopt.optimize(models.vcl_stochastic, init_points=10, n_iter=10, verbose=True)
        print(result)

if __name__ == '__main__':
    unittest.main()