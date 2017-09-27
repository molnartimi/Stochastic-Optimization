import collections

Model = collections.namedtuple('Model', 'file parameters rewards measurements')

simple_server = Model(file='simple-server.pnml',
                      parameters=('requestRate', 'serviceTime'),
                      rewards=('Idle', 'ServedRequests'),
                      measurements={'Idle': 0.727272727272727, 'ServedRequests': 1.09090909090909})

vcl_stochastic = Model(file='vcl_stochastic.pnml',
                       parameters=("incomingRate", "dispatchTime", "warmDispatchTime", "jobTime",
                                   "powerTime", "powerUsage", "idlePowerFactor"),
                       rewards=("jobsFinished", "powerUsage", "noFreeMachines", "jobsDispatched",
                                "machinesWorking", "hotMachinesWorking", "coldStarted"),
                       measurements={'jobsFinished': 0.0148748002933323, 'powerUsage': 5.20955485293811,
                                     'noFreeMachines': 0.00209451703730028, 'jobsDispatched': 0.0148748002307829,
                                     'machinesWorking': 2.3647315051718, 'hotMachinesWorking': 2.17735632582612,
                                     'coldStarted': 6.36702992684728E-7})
