import unittest
import sys
sys.path.append("./")
import models
from algorithm.mygpflowopt import MyGPflowOpt, Kernel, Acquisition, Prior


class GPflowOptTest(unittest.TestCase):
    @unittest.skip("works fine")
    def test_simple_server(self):
        self._run_tests(models.simple_server, 20, 10)

    @unittest.skip("too long")
    def test_vcl_stochastic(self):
        self._run_tests(models.vcl_stochastic, 20, 10)

    #@unittest.skip("too long")
    def test_hybrid_cloud(self):
        self._run_tests(models.hybrid_cloud, 10, 10)

    @unittest.skip("not working")
    def test_constrained_simple_server(self):
        self._run_tests_constrained(models.simple_server, 20, 10)

    def _run_tests(self, model, init_points, n_iter):
        testcases = [{'kernel': Kernel.EXP, 'acq': Acquisition.EI},
                     {'kernel': Kernel.EXP, 'acq': Acquisition.POI},
                     {'kernel': Kernel.EXP, 'acq': Acquisition.LCB},
                     {'kernel': Kernel.M12, 'acq': Acquisition.EI},
                     {'kernel': Kernel.M12, 'acq': Acquisition.POI},
                     {'kernel': Kernel.M12, 'acq': Acquisition.LCB},
                     {'kernel': Kernel.M32, 'acq': Acquisition.EI},
                     {'kernel': Kernel.M32, 'acq': Acquisition.POI},
                     {'kernel': Kernel.M32, 'acq': Acquisition.LCB},
                     {'kernel': Kernel.M52, 'acq': Acquisition.EI},
                     {'kernel': Kernel.M52, 'acq': Acquisition.POI},
                     {'kernel': Kernel.M52, 'acq': Acquisition.LCB}]

        gpflowopt = MyGPflowOpt(model)

        for case in testcases:
            print(case['kernel'] + '-' + case['acq'])
            result = gpflowopt.optimize(init_points, n_iter, case['kernel'], case['acq'], verbose=True)
            print(result)

    def _run_tests_constrained(self, model, init_points, n_iter):
        testcases = [{'kernel': Kernel.M12, 'acq': Acquisition.EI, 'prior': Prior.GAMMA, 'param1': 1, 'param2': 1},
                     {'kernel': Kernel.M12, 'acq': Acquisition.EI, 'prior': Prior.GAUSSIAN, 'param1': 1, 'param2': 1},
                     {'kernel': Kernel.M12, 'acq': Acquisition.EI, 'prior': Prior.LOGNORMAL, 'param1': 1, 'param2': 1}]

        gpflowopt = MyGPflowOpt(model)

        for case in testcases:
            #print(case['kernel'] + ',' + case['acq'] + ',' + case['prior'])
            try:
                result = gpflowopt.optimize(init_points, n_iter, case['kernel'], case['acq'], case['prior'], case['param1'], case['param2'])
            except Exception as error:
                result = repr(error)
            print(result)

if __name__ == '__main__':
    unittest.main()