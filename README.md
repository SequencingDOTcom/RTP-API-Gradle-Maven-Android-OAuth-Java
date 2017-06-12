# Gradle plugin for quickly adding Sequencing.com's OAuth2 to Android apps coded in Java
======================================

This repo contains Gradle plugin code for implementing Sequencing.com's OAuth2 authentication for your Android app so that your app can securely access to [Sequencing.com's](https://sequencing.com/) API and app chains.

* oAuth flow is explained [here](https://github.com/SequencingDOTcom/OAuth2-code-with-demo)
* Example that uses this Gradle module is located [here](https://github.com/SequencingDOTcom/OAuth2-code-with-demo/tree/master/android)


Contents
======================================

* Gradle integration
* Authentication flow
* Resources
* Maintainers
* Contribute


Gradle integration
======================================

You need to follow instructions below if you want to build in and use OAuth logic in your existing or new project.

* create a new Android Gradle based project (i.e. in Android Studio or Eclipse)
* add gradle dependency
	* see [gradle guides](https://docs.gradle.org/current/userguide/artifact_dependencies_tutorial.html) 
	* add dependency into build.gradle file in dependencies section. Here is dependency declaration example:
	```
	dependencies {
   		compile 'com.sequencing:android-oauth:1.0.22' 
    }
	```
* integrate autherization functionality
	* add imports
	```java
    import com.sequencing.androidoauth.core.OAuth2Parameters;
	import com.sequencing.androidoauth.core.ISQAuthCallback;
	import com.sequencing.androidoauth.core.SQUIoAuthHandler;
	import com.sequencing.oauth.core.Token;
    ```
    * for authorization you need to specify your application parameters at [AuthenticationParameters](https://github.com/SequencingDOTcom/Maven-OAuth-Java/blob/master/src/main/java/com/sequencing/oauth/config/AuthenticationParameters.java). Authorization plugin use custom url schema which you should define. For example:
    ```java
    AuthenticationParameters parameters = new AuthenticationParameters.ConfigurationBuilder()
                .withRedirectUri("[your custom url schema]/Default/Authcallback")
                .withClientId("[your client id]")
                .withClientSecret("[your client secret]")
                .build();
    ```
    * implement [ISQAuthCallback](https://github.com/SequencingDOTcom/Maven-Android-OAuth-Java/blob/master/src/main/java/com/sequencing/androidoauth/core/ISQAuthCallback.java).
    ```java
     /**
     * Callback for handling success authentication
     * @param token token of success authentication
     */
    void onAuthentication(Token token);

    /**
     * Callback of handling failure authentication
     * @param e exception of failure
     */
    void onFailedAuthentication(Exception e);
    ```
    * create View that will serve as initial element for authentication flow. It can be a Button or an extension of View class. Do not define onClickListener for this View.
    * create [SQUIoAuthHandler](https://github.com/SequencingDOTcom/Maven-Android-OAuth-Java/blob/master/src/main/java/com/sequencing/androidoauth/core/SQUIoAuthHandler.java) instance that is handling authentication process
    * register your authentication handler by invoking ```authenticate``` method with view, callback and app configuration
    
    ```java
	public void authenticate(View viewLogin, final ISQAuthCallback authCallback, AuthenticationParameters parameters);
    ```





Authentication flow
======================================

Sequencing.com uses standard OAuth approach which enables applications to obtain limited access to user accounts on an HTTP service from 3rd party applications without exposing the user's password. OAuth acts as an intermediary on behalf of the end user, providing the service with an access token that authorizes specific account information to be shared.

[Authentication sequence diagram](https://github.com/SequencingDOTcom/oAuth2-code-and-demo/blob/master/screenshots/oauth_activity.png)


Steps
======================================

### Step 1: Authorization Code Link

First, the user is given an authorization code link that looks like the following:

```
https://sequencing.com/oauth2/authorize?redirect_uri=REDIRECT_URL&response_type=code&state=STATE&client_id=CLIENT_ID&scope=SCOPES
```

Here is an explanation of the link components:

* https://sequencing.com/oauth2/authorize: the API authorization endpoint
* client_id=CLIENT_ID: the application's client ID (how the API identifies the application)
* redirect_uri=REDIRECT_URL: where the service redirects the user-agent after an authorization code is granted
* response_type=code: specifies that your application is requesting an authorization code grant
* scope=CODES: specifies the level of access that the application is requesting

![login dialog](https://github.com/SequencingDOTcom/oAuth2-code-and-demo/blob/master/screenshots/oauth_auth.png)

### Step 2: User Authorizes Application

When the user clicks the link, they must first log in to the service, to authenticate their identity (unless they are already logged in). Then they will be prompted by the service to authorize or deny the application access to their account. Here is an example authorize application prompt

![grant dialog](https://github.com/SequencingDOTcom/oAuth2-code-and-demo/blob/master/screenshots/oauth_grant.png)

### Step 3: Application Receives Authorization Code

If the user clicks "Authorize Application", the service redirects the user-agent to the application redirect URI, which was specified during the client registration, along with an authorization code. The redirect would look something like this (assuming the application is "php-oauth-demo.sequencing.com"):

```
https://php-oauth-demo.sequencing.com/index.php?code=AUTHORIZATION_CODE
```

### Step 4: Application Requests Access Token

The application requests an access token from the API, by passing the authorization code along with authentication details, including the client secret, to the API token endpoint. Here is an example POST request to Sequencing.com token endpoint:

```
https://sequencing.com/oauth2/token
```

Following POST parameters have to be sent

* grant_type='authorization_code'
* code=AUTHORIZATION_CODE (where AUTHORIZATION_CODE is a code acquired in a "code" parameter in the result of redirect from sequencing.com)
* redirect_uri=REDIRECT_URL (where REDIRECT_URL is the same URL as the one used in step 1)

### Step 5: Application Receives Access Token

If the authorization is valid, the API will send a JSON response containing the access token  to the application.




Resources
======================================
* [App chains](https://sequencing.com/app-chains)
* [File selector code](https://github.com/SequencingDOTcom/File-Selector-code)
* [Developer center](https://sequencing.com/developer-center)
* [Developer documentation](https://sequencing.com/developer-documentation/)

Maintainers
======================================
This repo is actively maintained by [Sequencing.com](https://sequencing.com/). Email the Sequencing.com bioinformatics team at gittaca@sequencing.com if you require any more information or just to say hola.

Contribute
======================================
We encourage you to passionately fork us. If interested in updating the master branch, please send us a pull request. If the changes contribute positively, we'll let it ride.
