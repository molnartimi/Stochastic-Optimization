import sys
sys.path.append("./")
from bayes_opt import BayesianOptimization
from spdn.spdn import SPDN

class MyBasienOptimization:
    """
    :param func: lambda x1,x2,...
    """
    def optimize(self, model, func, init_points, n_iter, acq, eparam, verbose=False):
        bo = BayesianOptimization(func, model.borders)
        self.spdn = SPDN(model)
        self.spdn.start(verbose)
        if (self.spdn.running):
            if acq == 'ucb':
                bo.maximize(init_points=init_points, n_iter=n_iter, kappa=eparam, acq=acq)
            else:  # ei or poi
                bo.maximize(init_points=init_points, n_iter=n_iter, xi=eparam, acq=acq)
            self.spdn.close()
            return bo.res['max']