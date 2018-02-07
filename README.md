![Red Calorie](web_ctc_logo.png)
# Red Calorie [![Build Status](https://travis-ci.org/st1hy/Red-Calorie.svg?branch=master)](https://travis-ci.org/st1hy/Red-Calorie)
Android app for keeping your calories in check.
Previous known as: Count Them Calories

Free and open source.

No advertisement.

Limited functionality, very, very slowly growing due to real life responsibilities.

Application is available on [Google Play](https://play.google.com/store/apps/details?id=com.github.st1hy.countthemcalories)

Changelog available [here](https://github.com/st1hy/Count-Them-Calories/releases).

## General project goal:
* TDD from the beginning via espresso
* dagger2 + rxJava + Glide + GreenDao + lots other libraries

All critique is welcomed and appreciated.

## Build flavors
* devtools - manual testing flavor, contains LeakCanary and other debug settings
* production - clean flavor for publishing, automatic testing etc.

## Testing
Mayor functionality is tested with android tests.
* Runs on Android 26 Virtual Device.
* For all tests to pass: Window animation scale, Transition animation scale and Animator duration scale must be turn off in developer options.


