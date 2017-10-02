import collections

Model = collections.namedtuple('Model', 'file parameters borders rewards measurements')

simple_server = Model(file='simple-server.pnml',
                      parameters=('requestRate', 'serviceTime'),
                      borders={'requestRate': (0.0001, 3), 'serviceTime': (0.0001, 1)},
                      rewards=('Idle', 'ServedRequests'),
                      measurements={'Idle': 0.727272727272727, 'ServedRequests': 1.09090909090909})

vcl_stochastic = Model(file='vcl_stochastic.pnml',
                       parameters=("incomingRate", "dispatchTime", "warmDispatchTime", "jobTime",
                                   "powerTime", "powerUsage", "idlePowerFactor"),
                       borders={'incomingRate': (0.0001, 1), 'dispatchTime': (0.0001, 3),
                                'warmDispatchTime': (0.0001, 2), 'jobTime': (0.0001, 200),
                                'powerTime': (0.0001, 20), 'powerUsage': (0.0001, 5),
                                'idlePowerFactor': (0.0001, 5)},
                       rewards=("jobsFinished", "powerUsage", "noFreeMachines", "jobsDispatched",
                                "machinesWorking", "hotMachinesWorking", "coldStarted"),
                       measurements={'jobsFinished': 0.0148748002933323, 'powerUsage': 5.20955485293811,
                                     'noFreeMachines': 0.00209451703730028, 'jobsDispatched': 0.0148748002307829,
                                     'machinesWorking': 2.3647315051718, 'hotMachinesWorking': 2.17735632582612,
                                     'coldStarted': 6.36702992684728E-7})
