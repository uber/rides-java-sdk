# Uber Rides Java SDK (Beta)

The SDK helps your Java app interact with the Uber rides API using libraries that wrap the raw HTTP calls made to the Uber API.

## Setup

### Installing

#### Before you begin
To use the API, you need to register your app in the [Uber developer dashboard](https://developer.uber.com/dashboard). When you register, the app gets a client ID, secret, and server token used for authentication. 

Note: This beta Java SDK is not suitable for Android development. To that end, we will release an official Android SDK soon.

#### Gradle
If using Gradle, add this to your project’s `build.gradle` file:
```gradle
dependencies {
    compile 'com.uber.sdk:rides:0.1.0'
}
```

#### Maven
If using Maven, add this to your project’s `pom.xml` file:
```xml
<dependency>
  <groupId>com.uber.sdk</groupId>
  <artifactId>rides</artifactId>
  <version>0.1.0</version>
</dependency>
```

### Authenticating your app

The oAuth 2.0 authorization flow lets your app request rides on the user’s behalf and access their profile and history without compromising their credentials. It has two parts: First, your app asks the user to authorize and second, it exchanges the authorization code for an access token from Uber. Here is a sample of the authorization flow:

Note: Make sure the callback redirect URI matches what’s in the developer dashboard for the app. 

```java
// Include the Java SDK session, rides, and sync services.
import com.uber.sdk.rides.client.Session;
import com.uber.sdk.rides.client.UberRidesSyncService;
import com.uber.sdk.rides.client.UberRidesServices;
// Include the auth and exception services.
import com.google.api.client.auth.oauth2.Credential;
import com.google.common.collect.ImmutableList;
import com.uber.sdk.rides.auth.OAuth2Credentials;
import com.google.api.client.auth.oauth2.Credential;
import com.uber.sdk.rides.client.error.NetworkException;
import com.google.common.collect.ImmutableList;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.util.List;

public class Test {
        public static void main(String[] args) throws Exception {
                String clientId = "SPECIFY_CLIENT_ID";
                String clientSecret = "SPECIFY_CLIENT_SECRET";
                List<OAuth2Credentials.Scope> yourScopes = ImmutableList.of(
                        OAuth2Credentials.Scope.HISTORY,
                        OAuth2Credentials.Scope.REQUEST,
                        OAuth2Credentials.Scope.PROFILE);
                // Create an OAuth2Credentials object with your app clientd, clientSecret, scopes, and a redirectUri to capture the user’s authorization code.
                String redirectUri = "SPECIFY_APP_REDIRECT_URL";
                OAuth2Credentials credentials = new OAuth2Credentials.Builder()
                        .setClientSecrets(clientId, clientSecret)
                        .setScopes(yourScopes)
                        .setRedirectUri(redirectUri)
                        .build();

                // Request the user to open the URL within authorizationUrl.
                String authorizationUrl = credentials.getAuthorizationUrl();
                System.out.println(authorizationUrl);
                // Paste the authorization code from the redirectUri callback.
                System.out.println("Paste your authorization code here:");

                // Use the credential object to create a session, which the client can use to make requests.
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String authorizationCode = br.readLine();
                String userId = "mrinan";
                Credential credential = credentials.authenticate(authorizationCode, userId);
                Session session = new Session.Builder().setCredential(credential).build();
                UberRidesSyncService service = UberRidesServices.createSync(session);

        }
}
```
Note: Keep each user's access token in a secure data store. Reuse the same token to make API calls on behalf of your user without repeating the authorization flow each time they visit your app. You don’t have to because the SDK handles the token refresh automatically when it makes API requests with an `UberRidesService`.

## Sync vs. Async Calls

Both synchronous and asynchronous calls work with the Uber rides Java SDK. Instantiate your service appropriately with
`UberRidesServices.createSync(session)` or `UberRidesServices.createAsync(session)`. You can access asynchronous calls from a platform-specific thread through callbacks. 

## Read-Only Calls

If you just need read-only access to Uber API resources, create a session with the server token you received after [registering your app](https://developer.uber.com/dashboard).
```java
Session session = new Session.Builder().setServerToken(“yourServerToken”).build();
```
Use this session to create an UberRidesService and fetch API resources:
```java
UberRidesSyncService service = UberRidesServices.createSync(session);
ProductsResponse products = service.getProducts(37.775f, -122.417f).getBody();
```

## Making Common Calls

In the `samples` folder, you can use the sample Java classes to test standard requests. Alternatively, you can download a sample from the [releases page](https://github.com/uber/rides-java-sdk/releases/tag/v0.1.0) to try them out.

### Before you begin

* Before you run a sample, edit the relevant `secrets.properties` file and add your app credentials.
* To run the command line sample, navigate to `samples/cmdline-sample` and run `$ ../../gradlew clean build run`. Or if you are using one of the downloaded samples, run `$ ./gradlew clean build run` from the root directory. This stores user credentials in your home directory under `.uber_credentials`.
* Be sure to import these SDK services into your Java class to be able to make most calls:
```java
import com.uber.sdk.rides.client.model.Location;
import com.uber.sdk.rides.client.model.Driver;
import com.uber.sdk.rides.client.model.PriceEstimate;
import com.uber.sdk.rides.client.model.PriceEstimatesResponse;
import com.uber.sdk.rides.client.model.Promotion;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.model.RideEstimate;
import com.uber.sdk.rides.client.model.RideMap;
import com.uber.sdk.rides.client.model.RideRequestParameters;
import com.uber.sdk.rides.client.model.SandboxProductRequestParameters;
import com.uber.sdk.rides.client.model.SandboxRideRequestParameters;
import com.uber.sdk.rides.client.model.TimeEstimate;
import com.uber.sdk.rides.client.model.TimeEstimatesResponse;
import com.uber.sdk.rides.client.model.UserActivity;
import com.uber.sdk.rides.client.model.UserActivityPage;
import com.uber.sdk.rides.client.model.UserProfile;
import com.uber.sdk.rides.client.model.Vehicle;
import com.uber.sdk.rides.client.error.NetworkException;
import com.uber.sdk.rides.auth.OAuth2Credentials;
import com.uber.sdk.rides.client.Session;
import com.uber.sdk.rides.client.UberRidesSyncService;
import com.uber.sdk.rides.client.UberRidesServices;
import com.uber.sdk.rides.client.model.Product;
import com.uber.sdk.rides.client.model.ProductsResponse;
```
For full documentation, visit our [Developer Site](https://developer.uber.com/v1/endpoints/).

### Get available products

```java
// Get a list of products.
ProductsResponse products = service.getProducts(37.775f, -122.417f).getBody();
for (Product product : products.getProducts()) {
System.out.println(product.getProductId() + ": " + product.getDisplayName());
}
```

### Request a ride

```java
//Request a real-world Uber ride.
Location startLocation = new Location(37.77f, -122.41f);
Location endLocation = new Location(37.49f, -122.41f);
RideRequestParameters rideRequestParameters = new RideRequestParameters.Builder().setStartLocation(startLocation)
       .setProductId("SPECIFY_PRODUCT_ID")
       .setEndLocation(endLocation)
       .build();
Ride ride = service.requestRide(rideRequestParameters).getBody();
String rideId = ride.getRideId();
```
This makes a real-world request and sends an Uber driver to the start location specified.

To develop and test against request endpoints in a sandbox environment, make sure to instantiate your `UberRidesService` with a `Session` whose `Environment` is set to `SANDBOX`.
```java
Session session = new Session.Builder().setCredential(credential).setEnvironment(Environment.SANDBOX).build();
UberRidesService service = UberRidesServices.createSync(session);
```
The default `Environment` of a `Session` is set to `PRODUCTION`. See our [documentation](https://developer.uber.com/v1/sandbox/) to learn more about the sandbox environment.

### Update a ride in the sandbox

If you request a ride in the sandbox, you step through the different states of the ride.
```java
SandboxRideRequestParameters rideParameters = new SandboxRideRequestParameters.Builder().setStatus(“accepted”).build();
Response<Void> response = client.updateSandboxRide(rideId, rideParameters);
```
A successful update returns a 204 for `response.getStatus()`.

Note: The `updateSandboxRide` method is not valid in the `PRODUCTION` `Environment`, where the ride status changes automatically. In a `PRODUCTION` `Environment`, the call throws an `IllegalStateException`.

## Getting Help

Uber developers actively monitor the [uber tag](http://stackoverflow.com/questions/tagged/uber-api) on StackOverflow. If you need help installing or using the library, ask a question there. Make sure to tag your question with `uber-api` and `java`!

## Contributing

We :heart: contributions. If you find a bug in the library or would like new features added, go ahead and open issues or pull requests against this repo.  Write a test for your bug fix or to show that your feature works as expected.