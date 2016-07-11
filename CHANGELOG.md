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
