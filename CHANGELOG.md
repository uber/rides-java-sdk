v0.8.4 - 09/19/2023
------------
### Bug fixes
- Fixed NPE happening inside LoginPushedAuthorizationRequest

v0.8.2 - 07/14/2023
------------
### Added support for authorization code oauth flow with pkce
Following this rfc https://datatracker.ietf.org/doc/html/rfc7636 added a token endpoint to fetch oauth tokens using authorization code and verifier.
- Added AuthorizationCodeGrantFlow class to send a network request to fetch oauth tokens

v0.8.1 - 05/05/2023
------------
### Added
Prefilling User Information
ProfileHint is a new class added to supply user's personal information to the `SessionConfiguration` object.
- It will be used by the underlying components to pre-populate user information during signup.
- Partial information is accepted.
```
SessionConfiguration configuration = new SessionConfiguration.Builder()
        .setClientId(CLIENT_ID)
        .setRedirectUri(REDIRECT_URI)
        .setScopes(Arrays.asList(Scope.PROFILE, Scope.RIDE_WIDGETS))
        .setProfileHint(new ProfileHint
                .Builder()
                .email("john@doe.com")
                .firstName("John")
                .lastName("Doe")
                .phone("1234567890")
                .build())
        .build();
```
v0.8.0 - 03/19/2018
------------

### Added
 - Fixed Release Script for proper versioning
 - Fixed NPE in token refresh
 - Updating Versioning headers to pull from BuildConfig
 - Changed README to use Maven Badges

v0.7.0 - 10/28/17
------------
This release refactors the SDK binary into three binaries and deprecates the previous one. This is a breaking change for imports for the majority of classes as we prepare for the 1.0 roadmap release with a stable API. This allows downstream dependencies to be able to avoid the oauth-client library dependency. 

Check the [README](https://github.com/uber/rides-java-sdk/blob/master/README.md) for the latest integration instructions to update your gradle/maven dependency.

### Breaking
- Refactored SDK into three modules, uber-core, uber-rides, and uber-core-oauth-client-adapter. [Issue #34](https://github.com/uber/rides-java-sdk/issues/34)
  - Repackaged com.uber.sdk.rides.auth.* classes to com.uber.sdk.core.auth.*
  - Repackaged com.uber.sdk.rides.client.[AccessTokenSession, ServerTokenSession, Session, SessionConfiguration] to com.uber.sdk.core.client.*
  - Moved Maven Artifact ID from 'sdk' to 'uber-rides', 'uber-core-oauth-client-adapter', and 'uber-core'. Old binary will no longer be updated.

v0.6.0 - 11/11/2016
------------
This release moves all our endpoints to the new 1.2 version! This update brings support to upfront pricing and fares, which is now reflected in RideEstimate and usable in RideRequestParameters.

### Added
- Fare to RideEstimate, this will be returned in the case of a product that supports upfront pricing.

- [Pull #24](https://github.com/uber/rides-java-sdk/pull/24) Add Ride.Status
- [Pull #24](https://github.com/uber/rides-java-sdk/pull/24) Add SMS number field to Driver

### Breaking
 - Price in RideEstimate is now Estimate and will be null for products that support upfront pricing.
 - PriceEstimate uses BigDecimal in place of Integer for our low and high estimates.
 - RideRequestButton now requires both a pickup and a dropoff for estimating.
 - Removed all China endpoint support.

v0.5.2 - 7/11/2016
------------
### Added
- [Issue #22](https://github.com/uber/rides-java-sdk/issues/22) Support for Uber Pool

v0.5.1 - 6/7/2016
-----------------
### Fixed
 - [Issue #14](https://github.com/uber/rides-java-sdk/issues/14) Adjust RefreshAuthenticator to ignore Invalid scope


v0.5.0 - 6/2/2016
------------------
This release sets up the Java SDK for the Uber Android SDK to utilize it as a third party dependency by adding common interfaces and removing heavier weight components.

### Added

#### SessionConfiguration
SessionConfiguration is a new class to hold on to client information for authentication. This is used by underlying components.

#### UberRidesApi

`UberServices` has been replaced with `UberRidesApi`, which uses a `Session` to construct Api Services

#### Sessions
`AccessTokenSession`, `ServerTokenSession`, and `CredentialSession` have been added for the three types of authentication.

#### AccessTokenStorage
A common interface to store access tokens

#### RidesService
Replaces `UberRidesSyncService` and `UberRidesAsyncService` with a Retrofit 2 based API Service. Both sync and async can be utilized directly on the resulting `Call<T>`

### Changed
    - Split packaging into core and rides
    - `Oauth2Credentials` now accepts a `SessionConfiguration`
    - Updated from Retrofit 1 to Retrofit 2
    - Updated from OkHttp2 to OkHttp3
    - Removed Gauva dependency

### Breaking  
  - Removed `UberServices` in favor of `UberRidesApi`
  - Removed `UberRidesSyncService` and `UberRidesAsyncService` in favor of `RidesService`

v0.3.0 - 5/9/2016
------------------
  - Merged #7
  - Merged #10

v0.2.0 - 2/25/2016
------------------
  - Updated to cover new endpoints and scopes.
  - Added China region support for login and endpoints.
  - Merged #4

v0.1.0 - 9/23/2015
------------------
  - Initial version.
