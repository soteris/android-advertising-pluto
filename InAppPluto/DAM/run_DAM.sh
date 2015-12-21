#!/bin/bash

# Dynamic Analysis: Runs all apks in a given directory, on the connected device
#	and retrieves their runtime generated local files, databases and shared_preferences.
#       It stores that in a directory named as the package name of the app that 
#		is launched. It also decompiles each analyzed app.
#
#  Directory with apks structure: Assumes The parent directory has subdirectories. Each such subdirectory has apk files in it.
#		e.g. ROOT_DIR/MEDICAL/1.apk ROOT_DIR/HEALTH_AND_FITNESS/1.apk
#
# USAGE: run-DAM.sh <path to directory with apks> <path to apktool> <path to dir where the decompiled apks will be stored>

rootDir=$1
pathToApkTool=$2
pathToDecompiledApks=$3
i=50

for d in "$rootDir"/*/; do
	echo "$d"
	mkdir rfiles2/$d #create category directory for results

	for f in "$d"/*; do
		if [ $i -eq 50 ];then
        	        adb -s emulator-5554 emu kill
        	        emulator -avd Nexus4_Google_API -wipe-data &
        	        sleep 25
        	        i=1
	        fi


		echo "$d / $f"	
		pkg=$(aapt dump badging $f|awk -F" " '/package/ {print $2}'|awk -F"'" '/name=/ {print $2}')
		act=$(aapt dump badging $f|awk -F" " '/launchable-activity/ {print $2}'|awk -F"'" '/name=/ {print $2}')

		echo '********** DAM: Installing app. **********'
		adb install $f

		echo '********** Starting launchable activity. **********'
		adb shell am start -n $pkg/$act

		echo '********** wait for 10 seconds **********'
		sleep 10

		echo '********** issue monkey commands **********'
		adb shell monkey -p $pkg -v 500

		echo '********** Retreiving local files and DBs **********'
		adb root
		adb shell mount -o rw,remount rootfs / #only if on emulator
		adb shell chmod 777 /mnt/sdcard # only if on emulator
		adb shell mkdir /sdcard/tmp #create tmp directory on device
		adb shell cp /data/data/$pkg/shared_prefs/* /sdcard/tmp # move shared prefs to tmp
		adb shell cp /data/data/$pkg/databases/*.db /sdcard/tmp # move all loval databases to tmp
		adb shell cp /data/data/$pkg/files/* /sdcard/tmp

		#pull all the local files
		adb pull /sdcard/tmp/ rfiles2/$d/$pkg/
	
		adb shell rm -r /sdcard/tmp #remove temp directory on device
		adb -e shell rm -r /mnt/sdcard/*
	
		echo '********** Uninstalling app '  $pkg ' **********'
		adb shell pm clear $pkg
		adb shell pm uninstall $pkg
		adb shell rm -rf /data/app/$pkg-*
		adb shell rm -rf /data/app/$pkg*
		adb shell rm -rf /data/app-lib/$pkg-*
		adb shell rm -rf /data/app-lib/$pkg*
	
		echo '********** Get schema dumps for db files **********'
		for dbFile in rfiles2/$d/$pkg/*; do
			if [[ $dbFile == *.db ]]; then
				sqlite3 $dbFile .dump > $dbFile.dump
			fi

			if [[ $dbFile == *.sqlite ]]; then
                                sqlite3 $dbFile .dump > $dbFile.dump
                        fi

		done

		echo '********** Decompile package **********'
		SUBFOLD=${pathToDecompiledApks%%/}/$f
		mkdir rfiles2/$d$pkg/DECOMPILED
		(cd rfiles2/$d$pkg/DECOMPILED && $2 d $SUBFOLD)
		
		i=$((i+1))
		echo $i
	done	

done
