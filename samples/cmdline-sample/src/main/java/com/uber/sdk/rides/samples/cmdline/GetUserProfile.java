package com.uber.sdk.rides.samples.cmdline;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.util.store.AbstractDataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.uber.sdk.rides.auth.OAuth2Credentials;
import com.uber.sdk.rides.client.Session;
import com.uber.sdk.rides.client.UberRidesServices;
import com.uber.sdk.rides.client.UberRidesSyncService;
import com.uber.sdk.rides.client.model.UserProfile;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Properties;

/**
 * Demonstrates how to authenticate the user and load their profile via the command line.
 */
public final class GetUserProfile {

    private GetUserProfile() {}

    private static LocalServerReceiver localServerReceiver;

    public static void main(String[] args) throws Exception {
        // Create or load a credential for the user.
        Credential credential = authenticate(System.getProperty("user.name"));

        // Create session for the user
        Session session = new Session.Builder()
                .setCredential(credential)
                .setEnvironment(Session.Environment.SANDBOX)
                .build();

        // Create the Uber API service object once the User is authenticated
        UberRidesSyncService uberRidesService = UberRidesServices.createSync(session);

        // Fetch the user's profile.
        System.out.println("Calling API to get the user's profile");
        UserProfile userProfile = uberRidesService.getUserProfile().getBody();

        System.out.printf("Logged in as %s%n", userProfile.getEmail());
        System.exit(0);
    }

    /**
     * Authenticate the given user. If you are distributing an installed application, this method
     * should exist on your server so that the client ID and secret are not shared with the end
     * user.
     */
    private static Credential authenticate(String userId) throws Exception {

        OAuth2Credentials oAuth2Credentials = createOAuth2Credentials();

        // First try to load an existing Credential. If that credential is null, authenticate the user.
        Credential credential = oAuth2Credentials.loadCredential(userId);
        if (credential == null || credential.getAccessToken() == null) {
            // Send user to authorize your application.
            System.out.printf("Add the following redirect URI to your developer.uber.com application: %s%n",
                    oAuth2Credentials.getRedirectUri());
            System.out.println("Press Enter when done.");

            System.in.read();

            // Generate an authorization URL.
            String authorizationUrl = oAuth2Credentials.getAuthorizationUrl();
            System.out.printf("In your browser, navigate to: %s%n", authorizationUrl);
            System.out.println("Waiting for authentication...");

            // Wait for the authorization code.
            String authorizationCode = localServerReceiver.waitForCode();
            System.out.println("Authentication received.");

            // Authenticate the user with the authorization code.
            credential = oAuth2Credentials.authenticate(authorizationCode, userId);
        }
        localServerReceiver.stop();

        return credential;
    }

    /**
     * Creates an {@link OAuth2Credentials} object that can be used by any of the servlets.
     */
    public static OAuth2Credentials createOAuth2Credentials() throws Exception {
        // Load the client ID and secret from {@code resources/secrets.properties}. Ideally, your
        // secrets would not be kept local. Instead, have your server accept the redirect and return
        // you the accessToken for a userId.
        Properties secrets = loadSecretProperties();

        String clientId = secrets.getProperty("clientId");
        String clientSecret = secrets.getProperty("clientSecret");

        if (clientId.equals("INSERT_CLIENT_ID_HERE") || clientSecret.equals("INSERT_CLIENT_SECRET_HERE")) {
            throw new IllegalArgumentException(
                    "Please enter your client ID and secret in the resources/secrets.properties file.");
        }

        // Store the users OAuth2 credentials in their home directory.
        File credentialDirectory =
                new File(System.getProperty("user.home") + File.separator + ".uber_credentials");
        credentialDirectory.setReadable(true, true);
        credentialDirectory.setWritable(true, true);
        // If you'd like to store them in memory or in a DB, any DataStoreFactory can be used.
        AbstractDataStoreFactory dataStoreFactory = new FileDataStoreFactory(credentialDirectory);

        // Start a local server to listen for the OAuth2 redirect.
        localServerReceiver = new LocalServerReceiver.Builder().setPort(8181).build();
        String redirectUri = localServerReceiver.getRedirectUri();

        // Build an OAuth2Credentials object with your secrets.
        return new OAuth2Credentials.Builder()
                .setCredentialDataStoreFactory(dataStoreFactory)
                .setRedirectUri(redirectUri)
                .setClientSecrets(clientId, clientSecret)
                .build();
    }

    /**
     * Loads the application's secrets.
     */
    private static Properties loadSecretProperties() throws Exception {
        Properties properties = new Properties();
        InputStream propertiesStream = GetUserProfile.class.getClassLoader().getResourceAsStream("secrets.properties");
        if (propertiesStream == null) {
            // Fallback to file access in the case of running from certain IDEs.
            File buildPropertiesFile = new File("src/main/resources/secrets.properties");
            if (buildPropertiesFile.exists()) {
                properties.load(new FileReader(buildPropertiesFile));
            } else {
                buildPropertiesFile = new File("samples/cmdline-sample/src/main/resources/secrets.properties");
                if (buildPropertiesFile.exists()) {
                    properties.load(new FileReader(buildPropertiesFile));
                } else {
                    throw new IllegalStateException("Could not find secrets.properties");
                }
            }
        } else {
            properties.load(propertiesStream);
        }
        return properties;
    }
}
