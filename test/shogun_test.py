import unittest
import sys
sys.path.append("./")
import models
from algorithm.myshogunopt import *


class ShogunOptTest(unittest.TestCase):
    @unittest.skip("works fine")
    def test_simple_server_opt(self):
        shogun = MyShogunOpt(models.simple_server, 100)
        result = shogun.optimize(10)
        print(result)

    @unittest.skip("long")
    def test_vcl_stochastic_opt(self):
        shogun = MyShogunOpt(models.vcl_stochastic, 100)
        result = shogun.optimize(2)
        print(result)

    #@unittest.skip("too long")
    def test_hybrid_cloud_opt(self):
        shogun = MyShogunOpt(models.hybrid_cloud, 20)
        result = shogun.optimize(5)
        print(result)

    @unittest.skip("just mean and variance at the known minimum point")
    def test_simple_server_mean(self):
        shogun = MyShogunOpt(models.simple_server, 1)
        mean, variance = shogun.get_mean_variance_classprobability([[1.5], [0.25]])
        print("Mean:", mean, "Variance:", variance)

    @unittest.skip("just mean and variance at the known minimum point")
    def test_vcl_stochastic_mean(self):
        shogun = MyShogunOpt(models.vcl_stochastic,200)
        shogun.get_mean_variance_classprobability([[0.015], [0.5], [0.15], [60], [5], [0.75], [0.6]])

    @unittest.skip("just mean and variance at the known minimum point")
    def test_hybrid_cloud_mean(self):
        shogun = MyShogunOpt(models.hybrid_cloud, 50)
        mean = shogun.get_mean_variance_classprobability([[5], [0.75], [0.0002], [0.2], [0.1], [0.0002], [0.1], [24], [0.8], [0.3], [0.01], [1000]])
        print(mean)

    @unittest.skip("just plotting")
    def test_simple_server_plot(self):
        shogun = MyShogunOpt(models.simple_server, 100)
        shogun.plot_posterior_mean([80, 50])


if __name__ == '__main__':
    unittest.main()
