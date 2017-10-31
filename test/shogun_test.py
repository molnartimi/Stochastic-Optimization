import unittest
import sys
sys.path.append("./")
import models
from algorithm.myshogunopt import *


class ShogunOptTest(unittest.TestCase):
    #@unittest.skip("only one works at a time :O")
    def test_simple_server_plot(self):
        shogun = MyShogunOpt(models.simple_server,100)
        shogun.plot_posterior_mean([80,50])

    #@unittest.skip("only one works at a time :O")
    def test_simple_server_mean(self):
        shogun = MyShogunOpt(models.simple_server, 1)
        mean, variance = shogun.get_mean_and_variance([[1.5], [0.25]])
        print("Mean:", mean, "Variance:", variance)

    @unittest.skip("only one works at a time :O")
    def test_simple_server_update(self):
        shogun = MyShogunOpt(models.simple_server, 5)
        shogun._update_gp([[2, 3, 4], [0.3, 1, 1.5]])

    @unittest.skip("only one works at a time :O")
    def test_vcl_stochastic_mean(self):
        shogun = MyShogunOpt(models.vcl_stochastic,200)
        shogun.get_mean_and_variance([[0.015], [0.5], [0.15], [60], [5], [0.75], [0.6]])

    @unittest.skip("only one works at a time :O")
    def test_vcl_stochastic_plot(self):
        shogun = MyShogunOpt(models.vcl_stochastic,50)
        shogun.plot_posterior_mean([80,50])

    @unittest.skip("only one works at a time :O")
    def test_hybrid_cloud_mean(self):
        shogun = MyShogunOpt(models.hybrid_cloud, 50)
        mean = shogun.get_mean_and_variance([[5], [0.75], [0.0002], [0.2], [0.1], [0.0002], [0.1], [24], [0.8], [0.3], [0.01], [1000]])
        print(mean)


if __name__ == '__main__':
    unittest.main()
