import unittest
import sys
sys.path.append("./")
import models
from algorithm.mygpflowopt import MyGPflowOpt, Kernel, Acquisition


class GPflowOptTest(unittest.TestCase):
    #@unittest.skip("works fine")
    def test_simple_server(self):
        self._run_tests(models.simple_server, 20, 10)

    @unittest.skip("too long")
    def test_vcl_stochastic(self):
        self._run_tests(models.vcl_stochastic, 20, 10)

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
            result = gpflowopt.optimize(init_points, n_iter, case['kernel'], case['acq'], constrained=False, verbose=True)
            print(result)


if __name__ == '__main__':
    unittest.main()