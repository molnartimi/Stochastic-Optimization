if __name__ == "__main__":

    from spdn.spdn import SPDN
    import collections
    import sys
    sys.path.append("./")
    from bayes_opt import BayesianOptimization

    Model = collections.namedtuple('Model', 'file parameters rewards measurements')

    simple_server = Model(file='simple-server.pnml',
                          parameters=('requestRate', 'serviceTime'),
                          rewards=('Idle', 'ServedRequests'),
                          measurements={'Idle': 0.727272727272727, 'ServedRequests': 1.09090909090909})

    vcl_stochactic = Model(file='vcl_stochastic.pnml',
                           parameters=("incomingRate", "dispatchTime","warmDispatchTime","jobTime",
                                       "powerTime","powerUsage","idlePowerFactor"),
                           rewards=("jobsFinished", "powerUsage","noFreeMachines","jobsDispatched",
                                    "machinesWorking","hotMachinesWorking","coldStarted"),
                           measurements={'jobsFinished': 0.0148748002933323, 'powerUsage': 5.20955485293811,
                                         'noFreeMachines': 0.00209451703730028, 'jobsDispatched': 0.0148748002307829,
                                         'machinesWorking': 2.3647315051718, 'hotMachinesWorking': 2.17735632582612,
                                         'coldStarted': 6.36702992684728E-7})
    
    spdn = SPDN(simple_server)
    spdn.start()
    if (spdn.running):
        # print(spdn.f([1.5, 0.25]))
        bo = BayesianOptimization(lambda requestRate, serviceTime: - spdn.f([requestRate,serviceTime]),
                                  {'requestRate': (0.0001, 3), 'serviceTime': (0.0001, 1)})
        #bo.explore({'requestRate': [0.4, 2.0], 'serviceTime': [0.1, 0.6]})
        bo.maximize(init_points=10, n_iter=15, kappa=2, acq='ei')
        print(bo.res['max'])
        spdn.close()

    spdn = SPDN(vcl_stochactic)
    spdn.start()
    if(spdn.running):
        # print(spdn.f([0.015, 0.5, 0.15, 60, 5, 0.75, 0.6]))
        bo = BayesianOptimization(lambda incomingRate, dispatchTime, warmDispatchTime, jobTime, powerTime, powerUsage,
                                         idlePowerFactor: - spdn.f([incomingRate, dispatchTime, warmDispatchTime,
                                                                    jobTime, powerTime, powerUsage, idlePowerFactor]),
                                  {'incomingRate': (0.0001, 1), 'dispatchTime': (0.0001, 3),
                                   'warmDispatchTime': (0.0001, 2), 'jobTime': (0.0001, 200),
                                   'powerTime': (0.0001, 20), 'powerUsage': (0.0001, 5),
                                   'idlePowerFactor': (0.0001, 5)})
        bo.maximize(init_points=10, n_iter=15, kappa=2, acq='ei')
        print(bo.res['max'])
        spdn.close()

