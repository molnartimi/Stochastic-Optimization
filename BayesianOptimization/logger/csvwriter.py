import csv
import os

class CsvWriter:

    def __init__(self,modelId,algorithmId,spdn=False):
        dirname = os.path.dirname(os.path.abspath(__file__))
        if spdn:
            filename = dirname + "/results/" + modelId + "-" + algorithmId + "_points.csv"
            if os.path.isfile(filename): self.exist = True
            else: self.exist = False
            self.file = open(filename, "a")
        else:
            filename = dirname + "/results/" + modelId + "-" + algorithmId + ".csv"
            if os.path.isfile(filename): self.exist = True
            else: self.exist = False
            self.file = open(filename, "a")
        self.writer = csv.writer(self.file, delimiter=';')

    def write(self,row, header=False):
        if not header or (header and not self.exist):
            self.writer.writerow(row)

    def close(self):
        if not self.file.closed:
            self.file.close()