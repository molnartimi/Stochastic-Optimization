import unittest
import sys
sys.path.append("./")
import models
from algorithm.myshogunopt import *


class ShogunOptTest(unittest.TestCase):
    #@unittest.skip("works fine")
    def test_simple_server_opt(self):
        shogun = MyShogunOpt(models.simple_server)
        result = shogun.optimize(10,10)
        result.print_result()

    @unittest.skip("long")
    def test_vcl_stochastic_opt(self):
        shogun = MyShogunOpt(models.vcl_stochastic)
        result = shogun.optimize(2,100)
        print(result)

    @unittest.skip("too long")
    def test_hybrid_cloud_opt(self):
        shogun = MyShogunOpt(models.hybrid_cloud)
        result = shogun.optimize(5,20)
        print(result)

if __name__ == '__main__':
    unittest.main()
