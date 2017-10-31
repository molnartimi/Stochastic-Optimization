
import sys
sys.path.append("./")
import models
from algorithm.myshogunopt import *
import unittest

class ShogunOptTest(unittest.TestCase):
    @unittest.skip("only one works at a time :O")
    def test_simple_server_plot(self):
        shogun = MyShogunOpt(models.simple_server,100)
        shogun.plot_posterior_mean([80,50])

    #@unittest.skip("only one works at a time :O")
    def test_simple_server_mean(self):
        shogun = MyShogunOpt(models.simple_server, 100)
        mean = shogun.get_mean([[1.5], [0.25]])
        print(mean)

    @unittest.skip("only one works at a time :O")
    def test_simple_server_update(self):
        shogun = MyShogunOpt(models.simple_server, 5)
        shogun._update_gp([[2, 3, 4], [0.3, 1, 1.5]])

    @unittest.skip("only one works at a time :O")
    def test_vcl_stochastic_mean(self):
        shogun = MyShogunOpt(models.vcl_stochastic,50)
        #shogun.get_mean([[0.015],[0.5],[0.15],[60],[5],[0.75], [0.6]])
        shogun.get_mean([[0.15], [0.7], [0.95], [40], [10], [1.2], [1.6]])

    @unittest.skip("only one works at a time :O")
    def test_vcl_stochastic_plot(self):
        shogun = MyShogunOpt(models.vcl_stochastic,50)
        shogun.plot_posterior_mean([80,50])

if __name__ == '__main__':
    unittest.main()
