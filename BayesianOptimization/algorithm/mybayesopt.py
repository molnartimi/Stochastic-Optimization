import sys
import time
sys.path.append("./")
from bayes_opt import BayesianOptimization
from spdn.spdn import SPDN
from spdn.spdnexception import SPDNException
from spdn.spdnresult import SPDNResult

class MyBasienOptimization:
    def __init__(self, model, error_value=1000000000):
        self.model = model
        self.ALGORITHM_ID = "BAYO"
        self.spdn = SPDN(self.model,self.ALGORITHM_ID)
        self.ACQS = {'ei': MyAcquisition.opt_with_xi, 'poi': MyAcquisition.opt_with_xi, 'ucb': MyAcquisition.opt_with_kappa}
        self.ERROR_VALUE = error_value

    def optimize(self, init_points, n_iter, acq, acq_param, verbose=False):
        start_time = time.time()
        self.spdn.start(verbose)
        if (self.spdn.running):
            result = self._optimize(init_points, n_iter, acq, acq_param)
            result.execution_time = time.time() - start_time
            self.spdn.close()
            result.write_out_to_csv()
            return result

    def _optimize(self, init_points, n_iter, acq, acq_param):
        def f(**kwargs):
            values = map(kwargs.get, self.model.parameters)
            try:
                return -self.spdn.f(values)
            except SPDNException:
                return -self.ERROR_VALUE

        bo = BayesianOptimization(f, self.model.borders, verbose=False)

        self.ACQS[acq](bo, init_points, n_iter, acq, acq_param)
        return SPDNResult(-bo.res['max']['max_val'],
                          self._convertResultPoint(bo.res['max']['max_params']),
                          self.ALGORITHM_ID,
                          {"init_points": init_points, "n_iter": n_iter, "acq": acq, "acq_param": acq_param},
                          self.model)

    def _convertResultPoint(self, point_dict):
        params = []
        for i in range(len(point_dict)):
            params.append(point_dict[self.model.parameters[i]])
        return params


class MyAcquisition:
    @staticmethod
    def opt_with_kappa(bo, init_points, n_iter, acq, acq_param):
        return bo.maximize(init_points, n_iter, kappa=acq_param, acq=acq)

    @staticmethod
    def opt_with_xi(bo, init_points, n_iter, acq, acq_param):
        return bo.maximize(init_points, n_iter, xi=acq_param, acq=acq)


class Acquisition:
    EI = 'ei'
    POI = 'poi'
    LCB = 'ucb'
