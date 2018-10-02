# CountUp - Android App

#### CountUp is an NFC-based time tracker.

----
## Requirements
1. You need six NFC tags to represent the activities (I used [these](https://www.amazon.de/selbstklebend-Durchmesser-beschreibbar-funktioniert-Communication/dp/B06XH2R5ZP/ref=sr_1_3?ie=UTF8&qid=1538492772&sr=8-3&keywords=NFC+Tag)).
2. You need to store the exact following text records (text/plain) "Act0", "Act1", "Act2", "Act3", "Act4" and "Act5" on the tags.
  It is planned to automate that in the future, but for now it has to be done manually.
(I used [this app](https://play.google.com/store/apps/details?id=com.wakdev.wdnfc) to write to the tags).

## Usage

You can stick the tags on your desk and write the names of the activities / projects you'd like to track on them if you like.

If you start the app for the first time, you are prompted to enter 6 activities you'd like to track.
If you want to enter different activities at some point, you just have to clear the apps storage in
the settings which also deletes all the tracked times at the moment (to be changed in the future).

The tracking is based on 'check-in' and 'check-out'. You can put your smartphone on the tag to check in to a certain activity, but it doesn't have to be on top of it all the time.
To check out, you can just check in another activity or click pause, which pauses the session, not just the current activity.
If you click the stop button, the session is finished and the times for this
session are added upon the total times for the activities which are presented in the summary.

If you have any ideas  improving the app, feel free to submit a PR.
