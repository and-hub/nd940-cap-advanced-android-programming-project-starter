## Political Preparedness

PolitcalPreparedness is an example application built to demonstrate core Android Development skills as presented in the Udacity Android Developers Kotlin curriculum. 

This app demonstrates the following views and techniques:

* [Retrofit](https://square.github.io/retrofit/) to make api calls to an HTTP web service.
* [Moshi](https://github.com/square/moshi) which handles the deserialization of the returned JSON to Kotlin data objects. 
* [Glide](https://bumptech.github.io/glide/) to load and cache images by URL.
* [Room](https://developer.android.com/training/data-storage/room) for local database storage.
  
It leverages the following components from the Jetpack library:

* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
* [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
* [Data Binding](https://developer.android.com/topic/libraries/data-binding/) with binding adapters
* [Navigation](https://developer.android.com/topic/libraries/architecture/navigation/) with the SafeArgs plugin for parameter passing between fragments


## Instalation
Clone this repository and import into Android Studio
```
git clone https://github.com/and-hub/political-preparedness.git
```
The entire app is located in `/starter` directory.

## API key
Place your Google Civic Information API key in [CivicsHttpClient.kt](https://github.com/and-hub/political-preparedness/blob/master/starter/app/src/main/java/com/example/android/politicalpreparedness/network/CivicsHttpClient.kt) file.
