# android-advertising-pluto
If you end up using any of our code, please cite our paper:

Demetriou, Soteris, et al. "Free for All! Assessing User Data Exposure to Advertising Libraries on Android" Proceedings of the 23rd Annual Network and Distributed System Security Symposium (NDSS). 2016.


# Instructions to run In-app Pluto.

## Instructions to run In-app Pluto's DAM module.
### Requirements
1. An Android emulator
2. Adb (android debug bridge) tool (has to be part of the environment path)
2. apktool

The first step to run in-app Pluto is to execute the Dynamic Analysis Module (DAM) on a directory of target Android executables (apks).

### Prepare Input
Create a directory to store the apks:

```
mkdir /home/user/Projects/Pluto/APKS
```

DAM expects apks to be in Category directories. For example, if app my_app1.apk and my_app2.apk are of the HEALTH_AND_FITNESS category then create a HEALTH_AND_FITNESS category and move the apks there:

```
mkdir /home/user/Projects/Pluto/APKS/HEALTH_AND_FITNESS
mv my_app1.apk /home/user/Projects/Pluto/APKS/HEALTH_AND_FITNESS/
mv my_app2.apk /home/user/Projects/Pluto/APKS/HEALTH_AND_FITNESS/
```

### Running DAM
You can find and modify the DAM script accordingly at android-advertising-pluto/InAppPluto/DAM/run_DAM.sh

The DAM  module is implemented as a shell script which takes input arguments.

1. the full path to the directory where the apks are stored.
2. the full path to the apktool

For example if your apks are in a directory /home/user/Projects/Pluto/APKS, the apktool is stored in /home/user/Tools/apktool_2.0.0rc3/ you would launch DAM as follows:

```
./run_DAM.sh /home/user/Projects/Pluto/APKS /home/user/Tools/apktool_2.0.0rc3/apktool
```

### DAM OUTPUT
The DAM module stores the decompiled files at: rfiles2/\<CATEGORY_NAME\>/\<PACKAGE_NAME\>/DECOMPILED (relevant path) preserving the file hierarchy of the input.

Inside each apk directory you will find a list of files created by the app at runtime and a directory (DECOMPILED) with the decompiled files created by the apktool.

## Instructions to run In-app Pluto's DAM module.

## Requirements
You need to include the following natural language processing libraries:

1. ws4j-1.0.1.jar
2. jawjaw-1.0.2.jar
3. edu.mit.jwi_2.3.3.jar

You will also need to include a wordnet dictionary. You can find one from the Princeton group: http://wordnetcode.princeton.edu/wn3.1.dict.tar.gz

## Prepare the Input
The Mining Phase assumes the existence of 3 input files:

1. Opportunities.txt
2. PermissionOpportunities.txt
3. groundTruth2.txt (for evaluation - this can be removed manually (at this point) from the code)

### Opportunities file
The first input file (Opportunities.txt) is a list of keywords to look for in the following format:
```
<first keyword name>:<1st keyword's category>:<integer>:<attribute or interest>:<float>
<second keyword name>:<2nd keyword's category>:<integer>:<attribute or interest>:<float>
...
```

Example:
```
gender:person:1:attribute:0.0005
workout:activity:0:interest:0.03
```
Parameter explanation:

1. Keywords are the information the tool will look for inside the app's files.
2. Keyword category is an arbitratily define categorization of keywords. This could be person for demographic keywords such as gender.
3. The third parameter is an interger depicting the wordnet sense id (or meaning id of the word). Every word can have different meanings in different context. The wordnet graph enumerates the meanings or senses for every word.
4. The fourth attribute decides the type of the keyword. This dictates the methodology the tool will use. Different one is used for attributes and another for interests. For example, workout is an interest whereas gender is an attribute.
5. The fifth attribute is a weight [0.0-1.0] indicating how sensitive the keyword is (1 is maximum sensitivity). In the paper we used an advertising cost model to determine this but you can have your own depending on the kinds of keywords you have.

### Permission - Opportunities
The second input file the tool assumes it exists holds associations between Android permissions and keywords (or opportunities).

```
<android permission 1>:<keyword>
<android permission 2>:<keyword>
...
```

Example:
```
ACCESS_COARSE_LOCATION:address
ACCESS_FINE_LOCATION:address
com.google.android.gms.permission.ACTIVITY_RECOGNITION:workout
```

### Ground Truth
The third file the tool assumes it exists holds associations between package names and opportunities which were manually determined (hence the ground truth). Delimiter is space.

```
<package name 1> <keyword> <keyword> <keyword> 
<package name 2> <keyword> <keyword> <keyword> 
...
```

Example:
```
com.superlauncher.mobile.launcher8.pro	interest
com.textmeinc.textme	phone, email, first name, last name, age, gender, address
```

## Run it
I assume you have already compiled the java files. (I used the JAVA-SE 1.7 environment).

The Mining phase takes as input the DAM modules' output.
For example, if the DAM module stored the output at /home/user/Projects/Pluto/rfiles2 then you can run the mining phase as follows:

```
java InAppAttributeExtractor /home/user/Projects/Pluto/rfiles2
```

## OUTPUT
Lots of usefull stuff. TODO...
