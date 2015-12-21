#!/bin/sh
# run.sh
# runs Pluto on DeviceAnalyzer's data
#
#
# This program takes the following commands. 
#   -D <the absolute path to the directory where the csv device-files are stored. (MANDATORY)
#   -M <The mode of operation. 0 for Apriori, 1 for FPGrowth>. (Default is: FPGrowth)
#   -A <0 for NOT running the data analysis, 1 for running the data analysis using the algorithm indicated by -M>. (Default is: 1)
#
#   Example: java -classpath .:./lib/weka.jar edu/illinois/seclab/android/tools/Pluto -D /home/me/myDir -M 1 -A 1

# REPLACE THE <dot> AT THE END WITH THE ACTUAL DIRECTORY WHERE THE DEVICE FILES ARE!
#java -Xmx12284m -classpath .:./lib/weka.jar edu/illinois/seclab/android/tools/Pluto -D /home/soteris/Projects/SCEA/scea/DATA/Test/355136055116886 -M 1 -A 1

java -Xmx51200m -classpath .:./lib/weka.jar edu/illinois/seclab/android/tools/Pluto -D ~/Projects/SCEA/ca_dataset/target -M 1 -A 1

# Uncomment to create a weka sparse nominal arff file for further analysis. Needs the directory with the device files
#java -classpath .:./lib/weka.jar -D <put the directory with the device files here> -M 0 -A 0 edu/illinois/seclab/android/tools/Pluto

# Uncomment to create a weka dense binary arff file for further analysis. Needs the directory with the device files
#java -classpath .:./lib/weka.jar -D <put the directory with the device files here> -M 1 -A 0 edu/illinois/seclab/android/tools/Pluto

# Uncomment to create an arff file and run the data anlaysis wqith Apriori. Needs the directory with the device files
#java -classpath .:./lib/weka.jar -D <put the directory with the device files here> -M 0 -A 1 edu/illinois/seclab/android/tools/Pluto

# Uncomment to create an arff file and run the data anlaysis wqith FPGrowth. Needs the directory with the device files
#java -classpath .:./lib/weka.jar -D <put the directory with the device files here> -M 1 -A 1 edu/illinois/seclab/android/tools/Pluto
