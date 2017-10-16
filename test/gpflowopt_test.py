import unittest
import sys
sys.path.append("./")
import models
from algorithm.mygpflowopt import MyGPflowOpt


class GPflowOptTest(unittest.TestCase):
    #@unittest.skip("works fine")
    def test_simple_server(self):
        """gpflowopt = MyGPflowOpt(models.simple_server)
        result = gpflowopt.optimize(20, 10, 'exp', 'ei', verbose=True)
        print(result)"""
        self._run_tests(models.simple_server, 20, 10)

    @unittest.skip("too long")
    def test_vcl_stochastic(self):
        self._run_tests(models.vcl_stochastic, 20, 10)

    def _run_tests(self, model, init_points, n_iter):
        testcases = [{'kernel': 'exp', 'acq': 'ei'},
                     {'kernel': 'exp', 'acq': 'poi'},
                     {'kernel': 'exp', 'acq': 'lcb'},
                     {'kernel': 'm12', 'acq': 'ei'},
                     {'kernel': 'm12', 'acq': 'poi'},
                     {'kernel': 'm12', 'acq': 'lcb'},
                     {'kernel': 'm32', 'acq': 'ei'},
                     {'kernel': 'm32', 'acq': 'poi'},
                     {'kernel': 'm32', 'acq': 'lcb'},
                     {'kernel': 'm52', 'acq': 'ei'},
                     {'kernel': 'm52', 'acq': 'poi'},
                     {'kernel': 'm52', 'acq': 'lcb'}]
        gpflowopt = MyGPflowOpt(model)
        for case in testcases:
            print(case['kernel'] + '-' + case['acq'])
            result = gpflowopt.optimize(init_points, n_iter, case['kernel'], case['acq'], verbose=False)
            print(result)


if __name__ == '__main__':
    unittest.main()