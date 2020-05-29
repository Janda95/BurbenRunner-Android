# IPSubnet-Android

## Master : [![Build Status](https://travis-ci.org/Janda95/BurbenRunner.svg?branch=master)](https://travis-ci.org/Janda95/BurbenRunner)

## Development : [![Build Status](https://travis-ci.org/Janda95/BurbenRunner.svg?branch=dev)](https://travis-ci.org/Janda95/BurbenRunner) 

## Summary

This is an Android application made with Google Maps Android API for creating and saving routes for running or general distance calculations.

## Disclaimer

The current version is not on yet on the Google Play app store, and is currently a work in progress, for personal use. If you would like to download and modify this code for personal use, then feel free to fork! PRs are welcome.

## Development environment

- OS: Ubuntu 18.04.1 LTS (Bionic Beaver)
- AndroidSDK Version: 3.5
- Minimum Application API: 21 (5.0 Lollipop)
- Target Application API: 28 (9.0 Pie)
- Google Java Style Guide
- Travis CI Testing Integration

## Devices Tested

- Google Pixel 2 (Simulator)
- S6 Edge+ (Physical)
- 10.1 WXGA (Simulator)
- 7 WSVGA (Simulator)
- 5.1 WVGA (Simulator)

## How to download the application

1. Link to Google Playstore
1. Clone from Github
1. Open with Android Studio
1. Sync Gradle (if option is requested).
1. Add Google Api key xml file (see below)
1. Run application and select available device

### How to add an API key to project

1. Add xml resource file
1. Generate API Key using Google Maps Api Console
1. Add string resouce with Api String from generated key: "< string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">API_KEY_STRING</ string>"
1. In Manifest file add a meta data tag containing api string resource: < meta-data android:name="com.google.android.geo.API_KEY" android:value="@string/google_maps_key" />

## How to run the application using simulator

- Select Available Device image with device api above 21

### Otherwise

1. Open AVD Manager
1. Create Virtual Device
1. Run Application with Virtual Device

## How to run the application using your device

### Store

- PENDING

### Personal Device

1. Put Android device into developer mode
1. Plug into computer with appropriate cable while running Android Studio
1. Run application with device to download non-production application onto device

## Contributers / Attributions

Google Maps API

Copyright [2019] [Jarek Rettinghouse]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

- [Jarek Rettinghouse (Janda95)](https://github.com/Janda95) - Developer / Project Owner
- [Nick Rettinghouse (call-me-nick)](https://github.com/call-me-nick) - User feedback
- Theresa Rettinghouse - User feedback
- Kris Barile - User feedback
