import sys
sys.path.append("./")
from bayes_opt import BayesianOptimization
from spdn.spdn import SPDN
from spdn.spdnexception import SPDNException
from logger.csvwriter import CsvWriter

class MyBasienOptimization:
    def __init__(self, model, error_value=10000):
        self.model = model;
        self.ALGORITHM_ID = "BAYO"
        self.spdn = SPDN(self.model)
        self.csv_writer = CsvWriter(self.model.id,self.ALGORITHM_ID)
        self._write_csv_header()
        self.ACQS = {'ei': MyAcquisition.opt_with_xi, 'poi': MyAcquisition.opt_with_xi, 'ucb': MyAcquisition.opt_with_kappa}
        self.ERROR_VALUE = error_value

    def optimize(self, init_points, n_iter, acq, acq_param, verbose=False):
        self.spdn.start(verbose)
        if (self.spdn.running):
            result = self._optimize(init_points, n_iter, acq, acq_param)

            self.spdn.close()
            self._write_csv_result(init_points, n_iter, acq, acq_param,result)

            return result

    def _optimize(self, init_points, n_iter, acq, acq_param):
        def f(**kwargs):
            values = map(kwargs.get, self.model.parameters)
            try:
                return -self.spdn.f(values)
            except SPDNException:
                return -self.ERROR_VALUE

        bo = BayesianOptimization(f, self.model.borders)

        self.ACQS[acq](bo, init_points, n_iter, acq, acq_param)
        return bo.res['max']

    def _write_csv_header(self):
        row = ['init points', 'n iter', 'acquisition', 'acq parameter', 'MIN VALUE']
        for param in self.model.parameters: row.append(param + ' (' + str(self.model.validvalues[param]) + ')')
        self.csv_writer.write(row)

    def _write_csv_result(self, init_points, n_iter, acq, acq_param, result):
        row = [init_points, n_iter, acq, acq_param, -result['max_val']]
        for param in self.model.parameters: row.append(str(result['max_params'][param]))
        self.csv_writer.write(row)


    def __del__(self):
        self.csv_writer.close()

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
