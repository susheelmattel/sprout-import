# app-android

## Pull Requests
Be sure to create a feature branch and then a pull request before merging to master. Please follow the [contributing guidelines](CHANGELOG.md) to documents changes related to each pull request.


## Setup
Install [Android Studio] (https://developer.android.com/studio/index.html).

Check out project from Version Control.


## Execute
On the toolbar, select "app" and run.

Select deployment target to run on.


## Emulator
Please check out https://developer.android.com/studio/run/emulator.html

With Android Studio:

Install [Android Studio] (https://developer.android.com/studio/index.html).

Once Android Studio is installed, find AVD Manager to create a device.

After the device is created, run 

`adb install -r [path-to-apk]`

Without Android Studio:

Please check out https://developer.android.com/studio/run/emulator-commandline.html


## UI Test
An easy way of testing UI with pseudo-random generated events using monkey or monkeyrunner tools.

Example:

`adb shell monkey -p com.sproutling -v 1000`

* -p "package name"
* -v "number of events"

Note: if the number of events seems endless, please run the following command to stop the process.

`adb shell ps | awk '/com\.android\.commands\.monkey/ { system("adb shell kill " $2) }'`


## Jenkins 
You can check the status for **Sproutling** and **Cartwheel** builds at 
http://utility.platform.mattel/jenkins/job/Sproutling%20Android/

**Checks**
1. `develop`branch should always be healthy. In case, if it goes red, than underlying issues needs to be addressed first.
2. Before creating the pull request against `develop`, make sure your branch build is successful. This will help ensure that the `develop` is always healthy. 

