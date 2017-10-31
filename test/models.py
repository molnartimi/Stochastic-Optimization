import collections

Model = collections.namedtuple('Model', 'id file parameters validvalues borders rewards measurements')

simple_server = Model(id='SMPL',
                      file='simple-server.pnml',
                      parameters=['requestRate', 'serviceTime'],
                      validvalues={'requestRate': 1.5, 'serviceTime': 0.25},
                      borders={'requestRate': (0.15, 15), 'serviceTime': (0.025, 2.5)},
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

hybrid_cloud = Model(id='HYBC',
                     file='hybrid-cloud.pnml',
                     parameters=["incRate","p","lbTime","execTime1","execTime2","failRate","idleFactor","repairTime",
                                 "publicRent","runPower","idlePower","repairCost"],
                     validvalues={'incRate': 5, 'p': 0.75, 'lbTime': 0.0002, 'execTime1': 0.2, 'execTime2': 0.1,
                                  'failRate': 0.0002, 'idleFactor': 0.1, 'repairTime': 24, 'publicRent': 0.8,
                                  'runPower': 0.3, 'idlePower': 0.01, 'repairCost': 1000},
                     borders={'incRate': (1,10), 'p': (0.01,0.99), 'lbTime': (0.00009,0.002), 'execTime1': (0.02,0.99),
                              'execTime2': (0.01,0.99), 'failRate': (0.00009, 0.002), 'idleFactor': (0.001,0.1),
                              'repairTime': (20,30), 'publicRent': (0.08,2), 'runPower': (0.03,0.99), 'idlePower': (0.001,0.1),
                              'repairCost': (100,3000)},
                     rewards=("Expense","JobComplete","JobsProcessing","NoFailedServer"),
                     measurements={'Expense': 0.586765349081149, 'JobComplete': 4.99999999371597,
                                   'JobsProcessing': 0.925618638346947, 'NoFailedServer': 0.997730355094315})
