# Uber Rides Java SDK (Beta)
This SDK helps your Java App make HTTP requests to the Uber Rides API.

## Setup

### Installing

#### Before you begin
Register your app in the [Uber developer dashboard](https://developer.uber.com/dashboard). Notice that the app gets a client ID, secret, and server token required for authenticating with the API. 

Note: This Java SDK is not suitable for Android development. We will release an official Android SDK soon.

#### Gradle
If using Gradle, add this to your project’s `build.gradle` file:
```gradle
dependencies {
    compile 'com.uber.sdk:rides:0.3.0'
}
```

#### Maven
If using Maven, add this to your project's `pom.xml` file:
```xml
<dependency>
  <groupId>com.uber.sdk</groupId>
  <artifactId>rides</artifactId>
  <version>0.3.0</version>
</dependency>
```

### Authenticating and creating a session
To make calls, you need to create an authenticated session with the API. While operations on behalf of users require a user-authorized token using OAuth 2, general requests can use server-token authentication.    

#### Create a session using a server token
```java
// Get the server token for your app from the developer dashboard.
Session session = new Session.Builder()
    .setServerToken("YOUR_SERVER_TOKEN")
    .setEnvironment(Environment.PRODUCTION)
    .build();
```
#### Create a session using the OAuth 2 flow
In an OAuth session, the app first asks the user to authorize and then exchanges the authorization code for an access token from Uber.
Note: Make sure the redirect URI matches the callback URI in the developer dashboard for the app. 

**Step 1**. Create an OAuth2Credentials object with your client ID, client secret, scopes, and a redirect callback URI to capture the user’s authorization code.
```java
OAuth2Credentials credentials = new OAuth2Credentials.Builder()
    .setClientSecrets(clientId, clientSecret)
    .setScopes(yourScopes)
    .setRedirectUri(redirectUri)
    .build();
```
**Step 2**. Navigate the user to the authorization URL from the OAuth2Credentials object. 
```java
String authorizationUrl = credentials.getAuthorizationUrl();
```  
**Step 3**. Once the user approves the request, you get an authorization code. Create a credential object to store the authorization code and the user ID.
```java
Credential credential = credentials.authenticate(authorizationCode, userId);
```
**Step 4**. Create a session object using the credential object.
```java
Session session = new Session.Builder()
    .setCredential(credential)
    .setEnvironment(Environment.PRODUCTION)
    .build();
```
**Step 5**. Instantiate a service using a session to start making calls.
```java
UberRidesSyncService service = UberRidesServices.createSync(session);
```
Note: Keep each user's access token in a secure data store. Reuse the same token to make API calls on behalf of your user without repeating the authorization flow each time they visit your app. The SDK handles the token refresh automatically when it makes API requests with an `UberRidesService`.

## Sync vs. Async Calls
Both synchronous and asynchronous calls work with the Uber rides Java SDK. Instantiate your service appropriately with `UberRidesServices.createSync(session)` or `UberRidesServices.createAsync(session)`. Asynchronous calls are returned on a platform appropriate thread accessible through callbacks. 

## Samples for Common Calls
Use the Java classes in the [samples](https://github.com/uber/rides-java-sdk/tree/master/samples/cmdline-sample) folder to test standard requests. Alternatively, you can download a sample from the [releases page](https://github.com/uber/rides-java-sdk/releases/tag/v0.1.0) to try them out.

For full documentation, visit our [Developer Site](https://developer.uber.com/v1/endpoints/).

### Get available products
```java
// Get a list of products for a specific location in GPS coordinates, example: 37.79f, -122.39f.
ProductsResponse productsResponse = service.getProducts(37.79f, -122.39f).getBody();
List <Product> products = productsResponse.getProducts();
String productId = products.get(0).getProductId();
```

### Request a ride
```java
// Request an Uber ride by giving the GPS coordinates for pickup and drop-off.
RideRequestParameters rideRequestParameters = new RideRequestParameters.Builder().setPickupCoordinates(37.77f, -122.41f)
       .setProductId(productId)
       .setDropoffCoordinates(37.49f, -122.41f)
       .build();
Ride ride = service.requestRide(rideRequestParameters).getBody();
String rideId = ride.getRideId();
```
**Warning**: This real-world request can send an Uber driver to the specified start location. To develop
and test against endpoints in a sandbox environment, instantiate your `UberRidesService` with a `Session` whose `Environment` is set to `SANDBOX`.
```java
Session session = new Session.Builder().setCredential(credential).setEnvironment(Environment.SANDBOX).build();
UberRidesSyncService service = UberRidesServices.createSync(session);
```
See our [documentation](https://developer.uber.com/v1/sandbox/) to learn more about the sandbox environment.

### Update a ride in the sandbox
If you request a ride in the sandbox, you can step through the different states of the ride.
```java
SandboxRideRequestParameters rideParameters = new SandboxRideRequestParameters.Builder().setStatus(“accepted”).build();
Response<Void> response = client.updateSandboxRide(rideId, rideParameters);
```
A successful update returns a 204 for `response.getStatus()`.

Note: The `updateSandboxRide` method is not valid in the `PRODUCTION` `Environment`, where the ride status changes automatically. In a `PRODUCTION` `Environment`, the call throws an `IllegalStateException`.

## Getting Help
Uber developers actively monitor the [uber-api tag](http://stackoverflow.com/questions/tagged/uber-api) on StackOverflow. If you need help installing or using the library, ask a question there. Make sure to tag your question with `uber-api` and `java`!

## Contributing
We :heart: contributions. If you find a bug in the library or would like to add new features, go ahead and open
issues or pull requests against this repo. Before you do so, please sign the
[Uber CLA](https://docs.google.com/a/uber.com/forms/d/1pAwS_-dA1KhPlfxzYLBqK6rsSWwRwH95OCCZrcsY5rk/viewform).
Also, be sure to write a test for your bug fix or feature to show that it works as expected.
