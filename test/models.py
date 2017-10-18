import collections

Model = collections.namedtuple('Model', 'id file parameters validvalues borders rewards measurements')

simple_server = Model(id='SMPL',
                      file='simple-server.pnml',
                      parameters=['requestRate', 'serviceTime'],
                      validvalues={'requestRate': 1.5, 'serviceTime': 0.25},
                      borders={'requestRate': (0.15, 15), 'serviceTime': (0.0025, 25)},
                      rewards=('Idle', 'ServedRequests'),
                      measurements={'Idle': 0.727272727272727, 'ServedRequests': 1.09090909090909})

vcl_stochastic = Model(id='VCLS',
                       file='vcl_stochastic.pnml',
                       parameters=["incomingRate", "dispatchTime", "warmDispatchTime", "jobTime",
                                   "powerTime", "powerUsage", "idlePowerFactor"],
                       validvalues={'incomingRate': 0.015, 'dispatchTime': 0.5, 'warmDispatchTime': 0.15,
                                    'jobTime': 60, 'powerTime': 5, 'powerUsage': 0.75, 'idlePowerFactor': 0.6},
                       borders={'incomingRate': (0.00015, 1.5), 'dispatchTime': (0.005, 50),
                                'warmDispatchTime': (0.0015, 15), 'jobTime': (0.6, 6000),
                                'powerTime': (0.05, 500), 'powerUsage': (0.0075, 75),
                                'idlePowerFactor': (0.006, 1)},
                       rewards=("jobsFinished", "powerUsage", "noFreeMachines", "jobsDispatched",
                                "machinesWorking", "hotMachinesWorking", "coldStarted"),
                       measurements={'jobsFinished': 0.0148748002933323, 'powerUsage': 5.20955485293811,
                                     'noFreeMachines': 0.00209451703730028, 'jobsDispatched': 0.0148748002307829,
                                     'machinesWorking': 2.3647315051718, 'hotMachinesWorking': 2.17735632582612,
                                     'coldStarted': 6.36702992684728E-7})
