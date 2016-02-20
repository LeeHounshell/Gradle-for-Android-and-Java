Gradle for Android and Java Final Project
=========================================

I'm participating in the Udacity+Google Android Nanodegree program.
And this is the Build-it-Bigger assignment for the Gradle course.


## Description

This project creates a Joke viewing app with multiple 'flavors.' The app makes use of libraries and is integrated with Google Cloud Messaging, AdMob and Google Analytics.

Functionally, this app presents a screen with a button to see a joke (the 'free' version also includes advertising links and screens).  When the button is pressed, a
message is sent to a GCE backend joke-server for request of a joke. The GCE server calls into a Java joke-provider library, which in turn makes an Internet REST request
to the 'Chuck Norris' joke database for a joke.  The received joke is sent back to the app (via GCM) and then sent via Intent extra to the AAR joke library, for Android display.

Of particular note here is the build solution:  This integrates app 'flavors' by introducing a Gradle 'preprocessor' so it becomes possible to use #IFDEF 'flavor' in Java code!
I have a blog entry explaining how the 'preprocessor.gradle' handles both 'flavor' and 'buildType' here: http://harlie.com/?p=38


## Features

 * A Gradle build 'preprocessor' which permits #IFDEF 'flavor' and #IFDEF 'buildType' in Android Java code
 * Google Cloud Messaging
 * Google AdMob Advertising for both banner and interstitial ads
 * Google Analytics tracking
 * Google Play Store upgrade integration
 * Deep-Link support for joke display
 * Android Archive library (AAR) integration
 * Java library (JAR) integration
 * Test Suite using Gradle and Expresso2 for unit and full-functional task based tests
 * AppCompat
 * Material Design
 * Custom Styling
 * Toolbar
 * CircleView for the loading indicator
 * Jsoup for text processing
 * Lint-Free Code
 * Included functional GCM backend integrated with the Internet Chuck Norris joke database

This app uses a API and data from [ICNDb.com](http://www.icndb.com/api/) to retrieve jokes.
To configure your GCM server, edit 'strings.xml' under app/src/main/res/values/.


---
SCREENSHOTS
---


![screen](../master/screens/Build-it-Bigger.gif)

![screen](../master/screens/Phone-paid-screenshot1.png)

![screen](../master/screens/Phone-screenshot3.png)

![screen](../master/screens/Phone-free-screenshot1.png)

![screen](../master/screens/Phone-free-screenshot2.png)


## License

This work is Copyright 2015 Lee Hounshell, and 
is licensed under a Creative Commons Attribution-NonCommercial 3.0 
Unported License. See http://creativecommons.org/licenses/by-nc/3.0 for
the license details.

