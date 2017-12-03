import subprocess
from subprocess import PIPE
import os
import random
from .spdnexception import SPDNException
from logger.csvwriter import CsvWriter


class SPDN:

    def __init__(self,model,algorithm_id):

        """ Class to communicate with SPDN.exe
        :param model: Model namedtuple from test.models
        """
        self.spdn_cmd = ('mono', '../SPDN/SPDN.exe', 'reward', '-c',
                         '../SPDN/configs/SP_PAR_BICG.txt', '-m',
                         '../SPDN/models/' + model.file, '--interactive', '-l', 'File')
        self.model = model
        self.model_id = model.id
        self.params = model.parameters
        self.rewards = model.rewards
        self.measures = model.measurements
        self.running = False
        self.pipe = None
        self.last_result = {}
        self.verbose = False
        self.csvwriter = None
        self.csv_data_id = algorithm_id + str(int(random.random() * 10000)) # id to csv rows those belong to one process

    def start(self,verbose=False):

        """ Start new process
        :param verbose: Log terminal events if True
        """

        self.verbose = verbose
        self.pipe = subprocess.Popen(self.spdn_cmd, stdout=PIPE, stdin=PIPE, stderr=PIPE)
        response = self._check_spdn_response()
        if response != 'OK':
            print('ERROR at starting SPDN.exe')
        else:
            self.running = True
            self.csvwriter = CsvWriter(self.model_id,'DATAS',spdn=True)
            header = ["Random_ID","Feasibility","Obj_func_value"]
            for i in range(len(self.model.parameters)): header.append(self.model.parameters[i])
            self.csvwriter.write(header, header=True)

    def f(self, values):

        """ Count the objective function (square error of measurement values)
        :param values: of the parameters
        :param error_check_mode: True if we call it as constraint function
        :return: the result as a float
        """

        self._set_params(values)
        self._set_rewards()
        try:
            self._get_results()

            f_result = 0

            for r in self.rewards:
                f_result += (self._fR(r) - self.measures[r]) ** 2
            self._write_csv_result(values, f_result, 1)
            return f_result
        except SPDNException as error:
            self._write_csv_result(values, "ERROR", -1)
            raise(error)


    def df(self, values):

        """ Count the gradient of the objective function (square error of measurement values)
        :param values: of the parameters
        :return: the result as a list (~vector)
        """

        self._set_params(values)
        self._set_rewards()
        self._get_results()

        df_result = []
        for p in self.params:
            temp = 0
            for r in self.rewards:
                temp += 2 * (self._fR(r) - self.measures[r]) * self._dfR(r, p)
            df_result.append(temp)
        return df_result

    def _set_params(self,values):
        self._write_to_spdn(self._parameters_to_string(values))  # requestRate=0.5;serviceTime=0.25
        response = self._check_spdn_response()

    def _set_rewards(self):
        self._write_to_spdn(self._rewards_to_string())
        response = self._check_spdn_response()

    def _write_to_spdn(self,cmd):
        """ Write command into SPDN.exe"""
        if self.verbose: print(">>>>>>>>>>" + cmd.strip())
        self.pipe.stdin.write(bytes(cmd))
        self.pipe.stdin.flush()

    def _check_spdn_response(self):
        response = self.pipe.stdout.readline().strip()
        if self.verbose: print("<<<<<<<<<<" + response.strip())
        return response

    def _get_results(self):
        """ Read the results of SPDN.exe from stdout and save it as a dictionary in self.last_result"""

        result = self._check_spdn_response()
        if 'ERROR' in result:
            raise SPDNException(result)
        else:
            while ('=' in result):
                equal_sign = result.index('=')
                if (';' in result):
                    semicol_sign = result.index(';')
                else:
                    semicol_sign = len(result) - 1
                self.last_result[result[:equal_sign]] = float(result[equal_sign + 1:semicol_sign])
                result = result[semicol_sign + 1:]

    def _fR(self,reward):
        """ Returns the function value of a reward function from the last results """
        return self.last_result[reward + '_inst']

    def _dfR(self,reward,param):
        """ Returns the partial derivative value of a reward function with a parameter from the last results """
        return self.last_result[reward + '_sens_' + param]

    def _parameters_to_string(self,values):
        """ Perform the appropriate format of the parameters for SPDN.exe (name1=value1;name2=value2;...)"""
        v_iter = iter(values)
        params = self.params[0] + '=' + str(next(v_iter))
        for p in self.params[1:]:
            params += ';' + p + '=' + str(next(v_iter))
        params += '\n'
        # print(params)
        return params

    def _rewards_to_string(self):
        """ Perform the appropriate format of the rewards for SPDN.exe (<reward-name>|...)"""

        rewards = ''
        for r in self.rewards:
            rewards += '<' + r + '>|'
        rewards = rewards[:rewards.__len__()-1] + '\n'
        #print(rewards)
        return rewards

    def _write_csv_result(self, points, value, feasibility):
        row = [self.csv_data_id, feasibility, value]
        for point in points: row.append(str(point))
        self.csvwriter.write(row)

    def close(self):
        """ Close the running processes """
        if self.csvwriter != None: self.csvwriter.close()
        if self.running:
            self._write_to_spdn('END\n')
            self.running = False
            self.pipe.stdin.close()
            self.pipe.stdout.close()
            self.pipe.stderr.close()
        os.kill(self.pipe.pid,15)
        self.pipe.terminate()

    def __del__(self):
        if self.running:
            self.close()



