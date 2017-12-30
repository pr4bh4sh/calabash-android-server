## Get the code
Clone [calabash-android](https://github.com/calabash/calabash-android) and [calabash-android-server](https://github.com/calabash/calabash-android-server) within the same parent directory, for example:

```
mkdir ~/calabash
cd ~/calabash
git clone https://github.com/calabash/calabash-android-server
git clone https://github.com/calabash/calabash-android
```

## Configure Android Studio

1. Launch _Android Studio_ and choose _Import project (Eclipse ADT, Gradle, etc.)_.
1. Select the calabash-android-server, for example `~/calabash/calabash-android-server/server`.
1. Click _Next_ a whole bunch of times until the project is opened.
1. From the _Run_ menu, select _Edit Configurations_.
1. Click the _+_ button and select _Android Application_.
1. Set _Name_ to _TestServer_. 
1. Select _instrumentation-backend_ as the _Module_.
1. Set _Deploy_ to _Nothing_.
1. Set _Launch_ to _Nothing_.
1. Remove _Gradle-aware Make_ from _Before launch_:
1. Click _OK_.

## Build the TestServer

1. Navigate to the `ruby-gem` folder in the _calabash-android_ repo, for example:

    ```
    cd ~/calabash/calabash-android/ruby-gem
    ```

1. Install the dependencies:

    ```
    bundle install
    ```

1. Build the _TestServer_:

    ```
    bundle exec rake build
    ```

`TestServer.apk` will be generated in `calabash-android/ruby-gem/lib/calabash-android/lib`.  

If you later make any modifications to the source code then you just need to run `bundle exec rake build` to generate a new `TestServer.apk`.

## Deploy the apps

1. Create a new directory to contain your apps/tests and copy the application to be tested into it, for example:

    ```
    mkdir ~/calabash/myapp
    cp myapp.apk ~/calabash/myapp
    cd ~/calabash/myapp
    ```

1. Add a `Gemfile` which points at the _calabash-android_ ruby gem source, for example:

    ```
    gem ‘calabash-android’, path:’~/calabash/calabash-android/ruby-gem’
    source ‘https://rubygems.org’
    ```

1. Launch the _calabash-android console_ for your test app, for example:

    ```
    bundle exec calabash-android console myapp.apk
    ```

1. Install the app to test and the _TestServer_

    ```
    reinstall_apps
    ```

## Configure the Android device / simulator

1. Enable developer mode.
1. Open _Settings -> Developer options_.
1. Enable _USB debugging_.
1. Tap _Select debug app_ and select your test app.
1. If you need to debug loading processes: Enable _Wait for debugger_.

## Debugging

1. Force an update of the _TestServer_ if you’ve modified the source code and rebuilt it:

    ```
    bundle exec calabash-android build myapp.apk
    ```

1. Launch the _calabash-android console_ for your test app, for example:

    ```
    bundle exec calabash-android console myapp.apk
    ```

1. Shut down any running instances if required:

    ```
    shutdown_test_server
    ```

1. Reinstall `TestServer.apk`

    ```
    reinstall_test_server
    ```

1. In the _calabash-android console_, launch the apps:

    ```
    start_test_server_in_background
    ```
1. In _Android Studio_, select _Run -> Attach debugger to Android process_.
1. Tick _Show all processes_, select the app and click _OK_.
1. Set a breakpoint in _Android Studio_. For example, on the first line of the `executeQuery` method in `sh.calaba.instrumentationbackend.query.Query` which can be found in the file `~/calabash/calabash-android-server/server/instrumentation-backend/src/sh.calaba/instrumentationbackend/query/Query.java`.

1. In the _calabash-android console_, execute calabash commands to cause the breakpoint to be hit, for example: 

    ```
    query('button')
    ```