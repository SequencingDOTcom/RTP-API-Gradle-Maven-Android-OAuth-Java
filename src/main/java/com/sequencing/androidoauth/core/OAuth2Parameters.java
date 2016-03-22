package com.sequencing.androidoauth.core;

import com.sequencing.oauth.config.AuthenticationParameters;
import com.sequencing.oauth.core.DefaultSequencingOAuth2Client;
import com.sequencing.oauth.core.SequencingOAuth2Client;

/**
 * Definition parameters for Sequencing oAuth2 authentication
 * and files API
 */
public class OAuth2Parameters {

    /**
     * Sequencing oAuth2 authentication client
     */
    private SequencingOAuth2Client oauth;

    /**
     * Define basic parameters for authentication
     */
    private AuthenticationParameters parameters;

    private static final OAuth2Parameters instance = new OAuth2Parameters();

    private OAuth2Parameters(){}

    public static OAuth2Parameters getInstance(){
        return instance;
    }

    public AuthenticationParameters getAppConfig() {
        if(parameters == null){
            parameters = new AuthenticationParameters.ConfigurationBuilder()
                    .withRedirectUri("authapp://Default/Authcallback")
                    .withClientId("oAuth2 Demo ObjectiveC")
                    .withClientSecret("RZw8FcGerU9e1hvS5E-iuMb8j8Qa9cxI-0vfXnVRGaMvMT3TcvJme-Pnmr635IoE434KXAjelp47BcWsCrhk0g")
                    .build();
        }
        return parameters;
    }

    public SequencingOAuth2Client getOauth(){
        if(oauth == null){
            oauth = new DefaultSequencingOAuth2Client(getAppConfig());
        }
        return oauth;
    }
}
