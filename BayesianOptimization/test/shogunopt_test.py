import unittest
import sys
sys.path.append("./")
import models
from algorithm.myshogunopt import *


class ShogunOptTest(unittest.TestCase):
    #@unittest.skip("works fine")
    def test_simple_server_opt(self):
        self._test(models.simple_server)

    #@unittest.skip("long")
    def test_vcl_stochastic_opt(self):
        shogun = MyShogunOpt(models.vcl_stochastic)
        result = shogun.optimize(20,20)
        print(result)

    #@unittest.skip("too long")
    def test_hybrid_cloud_opt(self):
        shogun = MyShogunOpt(models.hybrid_cloud)
        result = shogun.optimize(20,20)
        print(result)

    def _test(self, model):
        init_points = 10
        n_iter = 10
        shogun = MyShogunOpt(model)
        result = shogun.optimize(init_points, n_iter)
        result.print_result()

if __name__ == '__main__':
    unittest.main()
