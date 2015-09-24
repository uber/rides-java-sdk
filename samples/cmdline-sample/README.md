# Uber Rides Java SDK (beta)

Official Java SDK (beta) to support the Uber Rides API.

## Before you begin

This is a beta Java SDK not intended to be run on Android. An official Android SDK will be released soon.

## Running the sample

First, add your client ID and secret retrieved from developer.uber.com to `src/main/resources/secrets.properties`.

To run the command line sample, run `$ ./gradlew clean build run`. You may need to add the redirect URL to your application (at developer.uber.com) for OAuth2 to succeed.

Running the sample will store user credentials in your home directory under `.uber_credentials` for future executions.

For full documentation, visit our [Developer Site](https://developer.uber.com/v1/endpoints/).

## Getting help

Uber developers actively monitor the [uber tag](http://stackoverflow.com/questions/tagged/uber-api) on StackOverflow. If you need help installing or using the library, you can ask a question there.  Make sure to tag your question with `uber-api` and `java`!

## Contributing

We :heart: contributions. If you've found a bug in the library or would like new features added, go ahead and open issues or pull requests against this repo.  Write a test to show your bug was fixed or the feature works as expected.