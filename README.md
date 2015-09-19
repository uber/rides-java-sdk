# Uber Rides

Official Java SDK (beta) to support the Uber Rides API.

## Installation

To use the Uber Rides Java SDK, add the compile dependency with the latest version of the Uber SDK in the `build.gradle` file:
```
dependencies {
    compile ‘com.uber.sdk:rides:0.1.0’
}
```

## Sync v.s. Async

The Uber Rides Java SDK supports both Synchronous and Asynchronous use. Asynchronous calls are returned on a platform appropriate thread and can be accessed through callbacks. Instantiate your Service as appropriate with
`UberRidesServices.createSync(session)` or `UberRidesServices.createAsync(session)`

## Read-Only Use

If you just need read-only access to Uber API resources, create a Session with the server token you received after [registering your app](https://developer.uber.com/dashboard).
```
Session session = new Session.Builder().setServerToken(“yourServerToken”).build();
```
Use this Session to create an UberRidesService and fetch API resources:
```
UberRidesService service = UberRidesServices.createSync(session);
ProductsResponse products = client.getProducts(37.775, -122.417).getResponseObject();
```
## Authorization

If you need to access protected resources or modify resources (like getting a user’s ride history or requesting a ride), you will need the user to grant access to your application through the OAuth 2.0 Authorization Code flow.

The Authorization Code flow is a two-step authorization process. The first step is having the user authorize your app and the second involves requesting an OAuth 2.0 access token from Uber.

The Authorization Code flow is mandatory if you want to request rides on behalf of users or access their profile and history information.

Start by creating an OAuth2Credentials object that has your clientd, clientSecret, scopes, and a redirectUri that is used to capture the user’s authorization code.

```
OAuth2Credentials credentials = new OAuth2Credentials.Builder()
        .setClientSecrets(clientId, clientSecret)
        .setScopes(yourScopes)
        .setRedirectUri(redirectUri)
        .build()
```
The `redirectUri` must match the value you provided when you registered your application

Then, navigate the user to the authorization URL created from the OAuth2Credentials object.
```
String authorizationUrl = credentials.getAuthorizationUrl();
// Instruct user to open the URL contained within authorizationUrl
```
Once the user logs in and confirms the OAuth2 dialog, they will be redirected to your server which will be listening for authorization code. Exchange that authorization code to retrieve an access token (contained within credential).
```
// Retrieve authorization code from server at redirectUri
// Authenticate the user with the authorization code.
Credential credential = credentials.authenticate(authorizationCode, userId);
```
Last, use the credential object to create a session, which can then be used to create a service client instance.

```
Session session = new Session.Builder().setCredential(credential).build(); 
UberRidesService service = UberRidesServices.createSync(session); 
```

Keep each user's access token in a secure data store. Reuse the same token to make API calls on behalf of your user without repeating the authorization flow each time the user visits your app. The SDK will handle the token refresh for you automatically when it makes API requests with an `UberRidesService`.

## Samples

There are sample java classes in the `samples` folder.  Before you can run a sample, you need to edit the appriopriate `secrets.properties` file and add your app credentials.

To run the command line sample, navigate to `samples/cmdline-sample` and run `$ ../../gradlew clean build run`

This will store user credentials in your home directory under `.uber_credentials`.

For full documentation, visit our [Developer Site](https://developer.uber.com/v1/endpoints/).

#### Get Available Products
```
ProductsResponse productsResponse = service.get_products(37.77f, -122.41f).getResponseObject();
List<Product> products = productsResponse.getProducts();
String productId = products.get(0).getProductId();
```
#### Request a Ride
```
Location startLocation = new Location(37.77f, -122.41f);
Location endLocation = new Location(37.49f, -122.41f);
RideRequestParameters rideRequestParameters = new RideRequestParameters.Builder().setStartLocation(startLocation)
       .setProductId(productId)
       .setEndLocation(endLocation)
       .build();
Ride ride = service.requestRide(rideRequestParameters).getResponseObject();
String rideId = ride.getRideId();
```
This will make a real-world request and send an Uber driver to the start location specified.

To develop and test against request endpoints in a sandbox environment, make sure to instantiate your `UberRidesService` with a `Session` whose `Environment` whose is set to `SANDBOX`.
```
Session session = new Session.Builder().setCredential(credential).setEnvironment(Environment.SANDBOX).build();
UberRidesService service = UberRidesServices.createSync(session);
```
The default `Environment` of a `Session` is set to `PRODUCTION`. See our [documentation](https://developer.uber.com/v1/sandbox/) to read more about using the Sandbox Environment.

#### Update Sandbox Ride

If you are requesting sandbox rides, you will need to step through the different states of a ride.
```
SandboxRideRequestParameters rideParameters = new SandboxRideRequestParameters.Builder().setStatus(“accepted”).build(); 
Response<Void> response = client.updateSandboxRide(rideId, rideParameters);
```
If the update is successful, `response.getStatus()` will be 204.

The `updateSandboxRide` method is not valid in the `PRODUCTION` `Environment`, where the ride status will change automatically.  If it is used in a `PRODUCTION` `Environment`, the call will throw an `IllegalStateException`.
## Getting help

Uber developers actively monitor the [Uber tag](http://stackoverflow.com/questions/tagged/uber-api) on StackOverflow. If you need help installing or using the library, you can ask a question there.  Make sure to tag your question with `Uber-API` and `Java`!

## Contributing

We :heart: contributions. If you've found a bug in the library or would like new features added, go ahead and open issues or pull requests against this repo.  Write a test to show your bug was fixed or the feature works as expected.