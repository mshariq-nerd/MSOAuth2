package com.nerdapplabs.forumapp.oauth.client;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by mohd on 23/01/17.
 */

public class TokenAuthenticator implements Authenticator {
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        return null;
    }
}