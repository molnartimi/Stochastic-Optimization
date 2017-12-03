import unittest
import sys
sys.path.append("./")
import models
from algorithm.mygpflowopt import MyGPflowOpt, Kernel, Acquisition, Prior


class GPflowOptTest(unittest.TestCase):
    @unittest.skip("good")
    def test_smpl_exp_ei(self):
        self._test(models.simple_server, Kernel.EXP, Acquisition.EI)
    
    @unittest.skip("good")
    def test_smpl_rbf_poi(self):
        self._test(models.simple_server, Kernel.RBF, Acquisition.POI)
    
    @unittest.skip("good")
    def test_smpl_m52_ei(self):
        self._test(models.simple_server, Kernel.M52, Acquisition.EI)
    
    @unittest.skip("good")
    def test_vcl_exp_poi(self):
        self._test(models.vcl_stochastic, Kernel.EXP, Acquisition.POI)
    
    @unittest.skip("good")
    def test_vcl_rbf_ei(self):
        self._test(models.vcl_stochastic, Kernel.RBF, Acquisition.EI)
    
    @unittest.skip("good")
    def test_vcl_m32_poi(self):
        self._test(models.vcl_stochastic, Kernel.M32, Acquisition.POI)

    def test_hybc_exp_ei(self):
        self._test(models.hybrid_cloud, Kernel.EXP, Acquisition.EI)

    def test_hybc_rbf_poi(self):
        self._test(models.hybrid_cloud, Kernel.RBF, Acquisition.POI)
	
    def test_hybc_m52_poi(self):
        self._test(models.hybrid_cloud, Kernel.M52, Acquisition.POI)

    def _test(self, model, kernel, acq):
        init_points = 10
        n_iter = 10
        gpflowopt = MyGPflowOpt(model)
        result = gpflowopt.optimize(init_points, n_iter, kernel, acq)
        result.print_result()

if __name__ == '__main__':
    unittest.main()