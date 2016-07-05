![Count Them Calories logo](web_ctc_logo.png)
# Count Them Calories
Android app for keeping your calories in check.

## Going for release
All the planned basic functionality that I've planned for 1.0 is complete. There is definitely sill room for improvement, but after some thought and consideration I decided that the time has come to release this to the public audience.
All feedback/critique is welcome and appreciated.

Application is available on [Google Play](https://play.google.com/store/apps/details?id=com.github.st1hy.countthemcalories)

## Current state: 1.0.2
* Fixed app name translation
* Fixed image size on tablets

### 1.0
* Added contribute page
* Bug fixes

### 0.7.1 alpha
* Fixed support for pre-Lollipop devices

### 0.7 alpha
* Added searching by product category.
* More material design

### 0.6 alpha
* Added undo function for deletion of items. Its a mix of functional programming (rx) and command pattern 
* Tags have the same swipe to delete/edit as rest of main screens
* Various bug fixes.
* More tests

## General project goal:
* TDD from the beginning via espresso / mockito / robolectic
* dagger2 + rxJava + MVP based

## Planned functionality for 1.0
* Right from the start of the app user can compose meals from defined ingredients
* User can define its own ingredients define how much calories they have, make a picture, etc.
* Application keeps track of every meal a day and calculates total calories consumed per day.
* No ads, free for all

## Future functionality (aka. wish list)
* Foremost it would be nice to have online database of products.
* Diet planner (how much calories per each day is left)
* Sharing preferences, ingredients and meals between users; cloud based (?)
* Graphs, moving averages, weight loss tracker, all based on the honesty of the user.

