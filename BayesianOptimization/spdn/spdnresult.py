from logger.csvwriter import CsvWriter

class SPDNResult:
    def __init__(self, value, point, algo_id, hyper_params, model):
        self.resultValue = value
        self.resultPoint = point
        self.algorithmId = algo_id
        self.hyperParams = hyper_params
        self.model = model
        self.csv_writer = CsvWriter(self.model.id, self.algorithmId)
        self.execution_time = 0

    def print_result(self):
        print("Algorithm: " + self.algorithmId)
        print("Model: " + self.model.id)
        print("Value = " + str(self.resultValue))
        for i in range(len(self.model.parameters)):
             print(self.model.parameters[i] + " = " + str(self.resultPoint[i]))

    def write_out_to_csv(self):
        row = ['MIN VALUE']
        for param in self.model.parameters: row.append(param + '(' + str(self.model.validvalues[param]) + ')')
        for hyperparam in self.hyperParams: row.append("HP_" + hyperparam)
        row.append("EXEC TIME")
        self.csv_writer.write(row, header=True)

        row = [str(self.resultValue)]
        for i in range(len(self.model.parameters)): row.append(str(self.resultPoint[i]))
        for hyperparam in self.hyperParams: row.append(str(self.hyperParams[hyperparam]))
        row.append(str(self.execution_time))
        self.csv_writer.write(row)

    def __del__(self): self.csv_writer.close()