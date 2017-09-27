import unittest
import collections
import sys
sys.path.append("./")
from bayes_opt import BayesianOptimization
from spdn.spdn import SPDN

Model = collections.namedtuple('Model', 'file parameters rewards measurements')

simple_server = Model(file='simple-server.pnml',
                      parameters=('requestRate', 'serviceTime'),
                      rewards=('Idle', 'ServedRequests'),
                      measurements={'Idle': 0.727272727272727, 'ServedRequests': 1.09090909090909})

class BayesianOptimizationTest(unittest.TestCase):

    def test_simple_server(self):

        spdn = SPDN(simple_server)
        spdn.start()
        if (spdn.running):
            # print(spdn.f([1.5, 0.25]))
            bo = BayesianOptimization(lambda requestRate, serviceTime: - spdn.f([requestRate, serviceTime]),
                                      {'requestRate': (0.0001, 3), 'serviceTime': (0.0001, 1)})
            # bo.explore({'requestRate': [0.4, 2.0], 'serviceTime': [0.1, 0.6]})
            bo.maximize(init_points=10, n_iter=15, kappa=2, acq='ei')
            print(bo.res['max'])
            spdn.close()


if __name__ == '__main__':
    unittest.main()
