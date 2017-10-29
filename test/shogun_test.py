#import unittest
import sys
sys.path.append("./")
import models
from algorithm.myshogunopt import *
from numpy import *

"""class ShogunOptTest(unittest.TestCase):
    def test_simple(self):
        print("elkezdem")
        shogun = MyShogunOpt(models.simple_server)
        print("letrehoztam")
        shogun.plot_posterior_mean(10)
        print("befejeztem")"""


if __name__ == '__main__':
    shogun = MyShogunOpt(models.simple_server,10,5)
    shogun.get_mean([[1.5],[0.25]])
    #shogun.plot_posterior_mean([30,20])
