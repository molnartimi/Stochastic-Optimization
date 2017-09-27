import subprocess
from subprocess import PIPE


class SPDN:

    def __init__(self,model):

        """ Class to communicate with SPDN.exe
        :param model: Model namedtuple from main.py
        """

        self.spdn_cmd = ('.\\resources\SPDN.exe', 'reward', '-c', 'resources\configs\SP_PAR_BICG.txt', '-m') +\
                        ('resources\models\\' + model.file,) + ('--interactive',)
        self.params = model.parameters
        self.rewards = model.rewards
        self.measures = model.measurements
        self.running = False
        self.pipe = None
        self.last_result = {}

    def start(self):

        """ Start new process """

        self.pipe = subprocess.Popen(self.spdn_cmd, stdout=PIPE, stdin=PIPE, stderr=PIPE)
        self._receive_ok()
        if not self.running: print('ERROR at starting SPDN.exe')

    def f(self, values):

        """ Count the objective function (square error of measurement values)
        :param values: of the parameters
        :return: the result as a float
        """

        self._set_params(values)
        self._set_rewards()
        self._get_results()

        f_result = 0
        for r in self.rewards:
            f_result += (self._fR(r) - self.measures[r]) ** 2
        return f_result

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

        """ Give the params with values to SPDN.exe
        :param values: of the parameters
        """

        self._write_to_spdn(self._parameters_to_string(values))  # requestRate=0.5;serviceTime=0.25
        self._receive_ok()
        if not self.running: print('ERROR at setting parameters')

    def _set_rewards(self):

        """ Give the rewards with parameters to SPDN.exe """

        self._write_to_spdn(self._rewards_to_string())
        self._receive_ok()
        if not self.running: print('ERROR at setting rewards')

    def _write_to_spdn(self,cmd):

        """ Write command into SPDN.exe
        :param cmd: command as string
        """

        self.pipe.stdin.write(bytes(cmd, 'utf-8'))
        self.pipe.stdin.flush()

    def _receive_ok(self):

        """ Check if the last input was valid in SPDN.exe
        If not, the process is closed
        """

        result = self.pipe.stdout.readline().strip().decode('utf-8')
        self.running = result == 'OK'

    def _get_results(self):

        """ Read the results of SPDN.exe from stdout
        and save it as a dictionary in self.last_result
        """

        result = self.pipe.stdout.readline().strip().decode('utf-8')
        while ('=' in result):
            equal_sign = result.index('=')
            if (';' in result):
                semicol_sign = result.index(';')
            else:
                semicol_sign = len(result) - 1
            self.last_result[result[:equal_sign]] = float(result[equal_sign + 1:semicol_sign])
            result = result[semicol_sign + 1:]

    def _fR(self,reward):

        """ Returns the function value of a reward function from the last results
        :param reward: name of reward
        :return: function value
        """

        return self.last_result[reward + '_inst']

    def _dfR(self,reward,param):

        """ Returns the partial derivative value of a reward function with a parameter from the last results
        :param reward: name of reward
        :param param: name of parameter
        :return: partial derivative value
        """

        return self.last_result[reward + '_sens_' + param]

    def _parameters_to_string(self,values):

        """ Perform the appropriate format of the parameters for SPDN.exe
        :param values: of the parameters
        :return: valid format string (name1=value1;name2=value2;...)
        """

        v_iter = iter(values)
        params = self.params[0] + '=' + str(next(v_iter))
        for p in self.params[1:]:
            params += ';' + p + '=' + str(next(v_iter))
        params += '\n'
        # print(params)
        return params

    def _rewards_to_string(self):

        """ Perform the appropriate format of the rewards for SPDN.exe
        :param for_grad: boolean, true if we would like to count also the partial derivatives
        :return: valid format string (<reward-name>|...)
        """

        rewards = ''
        for r in self.rewards:
            rewards += '<' + r + '>|'
        rewards = rewards[:rewards.__len__()-1] + '\n'
        #print(rewards)
        return rewards

    def close(self):

        """ Close the running process """

        self._write_to_spdn('END')
        self.running = False



