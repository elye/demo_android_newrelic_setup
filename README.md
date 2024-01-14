Simple design that follow https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/install-configure/install-android-agent-gradle/ to setup.
It should work with the need to replace the NEWRELIC-APPKEY in the newrelic.properties and MyApplication.kt file.

The App will 
- Crash to send Unhandled Exception up (first button) - note to see the crash on newrelic, it need to relaunch to send previous crash
- Send Handled Exception up (second button)
- Should upload the mapping.txt automatically. (currently there's an issue as reported in https://forum.newrelic.com/s/hubtopic/aAXPh0000000Y4vOAE/appnewrelicmapuploadrelease-fail-to-upload-mappingtxt - this issue is now fixed)
