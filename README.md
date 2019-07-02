# eafmupdate
Evolutionary Algorithms for Feature Model Update

To run experiments, execute the JUnit test case `experiment` in class `eafmupdate/experiments/eafmupdate/process/Experiments.java` (optionally with Java VM arguments `-Xms8192M -Xmx8192M` to increase heap space)

To run experiments on selected models and/or process strategies, set the variables `useOnlyOneProcess` and `useOnlyOneModel` in the same class `Experiments.java` to true/false.

To obtain model properties, run JUnit test case `eafmupdate\experiments\eafmupdate.process.PrintProperties.printModelProperties()`.


**fmmutation** project for the mutation operators over fm

**fmupdate.models** models and case studies for the fm update

# cite as:
Paolo Arcaini, Angelo Gargantini, and Marco Radavelli
Achieving change requirements of feature models by an evolutionary approach
in Journal of Systems and SoftwareElsevier BV, vol. 150 (2019): 64--76
[download the pdf file](http://cs.unibg.it/gargantini/research/papers/vamos2018_SI.pdf)
