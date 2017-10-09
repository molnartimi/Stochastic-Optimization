1. Starting SPDN in reward mode. Loading the model (-m, --model) and analysis configuration (-c, --configuration) in interactive mode

>> SPDN.exe reward -c configs\SP_PAR_BICG.txt -m models\hybrid-cloud.pnml --interactive

2. Successful startup is indicated with OK. Otherwise begins with ERROR

<< OK

2. SPDN is ready to read parameter values from the standard input (terminated by a newline). The format is name1=value1;name2=value2;...

>> incRate=5;p=0.75;lbTime=1.0002;execTime1=0.2;execTime2=0.1;failRate=0.0002;idleFactor=0.1;repairTime=24;publicRent=0.8;runPower=0.3;idlePower=0.01;repairCost=1000

3. The answer is OK at successful read, otherwise begins with ERROR

<< OK

4. SPDN is ready to read reward options from the standard input (terminated by a newline). The format is <reward-name>[inst,acc](var1,var2,...)|...

>> <JobsProcessing>[inst](incRate)

5. The answer is OK at successful read, otherwise begins with ERROR

<< OK

6. The analysis is running...
7. The results are written to the standard output. The format is reward-name_inst=val;reward-name_sens_var=val;...

<< JobsProcessing_inst=19.7500689579312;JobsProcessing_sens_incRate=0.0624792277699441

8. Repeat from 2. The program can be terminated at steps 2 and 4 by typing END (terminated by a newline)


Possible ERROR signals:
ERROR PARAMETER PARSE: <message>
ERROR PARAMETER UNKNOWN: <message>
ERROR REWARD PARSE: <message>
ERROR ANALYSIS: <message>

ERROR FATAL: <message>     This terminates SPDN.