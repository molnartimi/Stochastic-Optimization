import unittest
import sys
sys.path.append("./")
from bayes_opt import BayesianOptimization
from spdn.spdn import SPDN
import models
from time import gmtime, strftime
import csv

class BayesianOptimizationTest(unittest.TestCase):

    def _test(self, model, bo, init_points, n_iter, acq, eparam):
        self.spdn = SPDN(model)
        self.spdn.start(verbose=False)
        if (self.spdn.running):
            if acq == 'ucb':
                bo.maximize(init_points=init_points, n_iter=n_iter, kappa=eparam, acq=acq)
            else: # ei or poi
                bo.maximize(init_points=init_points, n_iter=n_iter, xi=eparam, acq=acq)
            self.spdn.close()
            return bo.res['max']

    def _run_tests(self, modelname, model, bo, init_points, n_iter):
        with open("bayesopt_" + modelname + "_" + strftime("%Y-%m-%d_%H-%M-%S", gmtime()) + '.csv', "w", newline="") as csvfile:
            writer = csv.writer(csvfile, delimiter=';')
            row = ['acq', 'param', 'init points', 'n iter', 'MAX VALUE']
            for param in model.parameters:
                row.append(param)
            writer.writerow(row)

            testcases = [{'acq': 'ucb', 'eparam': 1},
                         {'acq': 'ucb', 'eparam': 5},
                         {'acq': 'ucb', 'eparam': 10},
                         {'acq': 'ei', 'eparam': 0},
                         {'acq': 'ei', 'eparam': 0.1},
                         {'acq': 'ei', 'eparam': 2},
                         {'acq': 'poi', 'eparam': 0},
                         {'acq': 'poi', 'eparam': 0.1},
                         {'acq': 'poi', 'eparam': 2}]

            for case in testcases:
                result = self._test(model, bo, init_points=init_points, n_iter=n_iter,
                                    acq=case['acq'], eparam=case['eparam'])

                row = [case['acq'], case['eparam'], init_points, n_iter, result['max_val']]
                for param in result['max_params']:
                    row.append(str(result['max_params'][param]))

                writer.writerow(row)
                print(result)

    #@unittest.skip("It works fine")
    def test_simple_server(self):
        # print(spdn.f([1.5, 0.25]))
        bo = BayesianOptimization(lambda requestRate, serviceTime: - self.spdn.f([requestRate, serviceTime]),
                                  models.simple_server.borders)

        self._run_tests("simple-server", models.simple_server, bo, 2, 5)

    @unittest.skip("too long")
    def test_vcl_stochastic(self):
        # print(spdn.f([0.015, 0.5, 0.15, 60, 5, 0.75, 0.6]))
        bo = BayesianOptimization(lambda incomingRate, dispatchTime, warmDispatchTime, jobTime, powerTime,
                                         powerUsage, idlePowerFactor: - self.spdn.f([incomingRate, dispatchTime,
                                                                                warmDispatchTime, jobTime,
                                                                                powerTime, powerUsage,
                                                                                idlePowerFactor]),
                                  models.vcl_stochastic.borders)

        self._run_tests("vcl-stochastic", models.vcl_stochastic, bo, 10, 25)


if __name__ == '__main__':
    unittest.main()
