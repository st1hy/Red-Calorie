![Count Them Calories logo](web_ctc_logo.png)
# Count Them Calories
Android app for keeping your calories in check.

Application is available on [Google Play](https://play.google.com/store/apps/details?id=com.github.st1hy.countthemcalories)

## Current state 1.3 beta
* General code redesign aiming at "simplification" and "maintainability"
* No interesting function added but bugs
* Removed Robolectric tests and most of mockito tests: 
1st constantly broke when upgrading support library, 
2nd after redesign become impossible to maintain (reverse implementation will do that). 
* Espresso tests survived pretty much without major change. Espresso for the win.

## 1.2.3
* Fixed incorrectly starting multiple activities in some edge cases
* Fixed missing view refresh when ingredient was removed in a meal.
* Improved search flow: when user cannot find the searched result and adds new item default name is the same as the query.

### 1.2.2
* Improved behaviour when permission to access external storage is revoked.

### 1.2.1
* Fixed taking picture from the application.
* Fixed meal date changing after editing.
* Fixed adding new meal with ingredients behavior on add meal screen in some scenarios.

### 1.2
* Added quick search button in when adding new ingredients.
* Clicking on ingredient on the list opens dialog with options: add to new meal, edit, remove.
* Clicking on energy density unit opens dialog with ability to change the unit.
* Improved time format: it should respect 24h system wide setting.

### 1.1.2
* Bug fix: fixed being unable to save ingredient after edit text error was displayed.

### 1.1.1
* Added remove picture
* Improved opening ingredient and meal detail transitions.
* Bug fixes.

### 1.1.0
* Extracted view logic from activities to fragments.
* Improved icon and background when no content is present.
* Bug fixes.

### 1.0.2
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
* TDD from the beginning via espresso ~~/ mockito / robolectic~~
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

