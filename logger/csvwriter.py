import csv
from time import gmtime, strftime
import os

class CsvWriter:

    def __init__(self,modelId,algorithmId,spdn=False):
        dirname = os.path.dirname(os.path.abspath(__file__))
        if spdn:
            self.file = open(dirname + "/results/" + modelId + "_" + algorithmId + ".csv", "a")
        else:
            self.file = open(dirname + "/results/" + modelId + "_" + algorithmId + "_" +
                             strftime("%m-%d_%H-%M", gmtime()) + ".csv", "w")
        self.writer = csv.writer(self.file, delimiter=';')

    def write(self,row):
        self.writer.writerow(row)

    def close(self):
        self.file.close()