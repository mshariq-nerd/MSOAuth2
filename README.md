## MSOAuth2 client example ##
This repository contains example of a sign up process using OAuth 2.0. This is an API client that
follows OAuth 2.0 mechanisms for user authentication using [Retrofit2](https://square.github.io/retrofit/).

### Configuration ###
* Compatible with Android API Level 17 (Android 4.2) and higher
* Version 1.0

### Library Used ###
compile 'com.android.support:design:25.1.1'
compile 'com.wdullaer:materialdatetimepicker:3.1.0'
compile 'de.hdodenhof:circleimageview:2.1.0'
compile 'com.squareup.retrofit2:converter-gson:2.0.0'
compile 'com.squareup.retrofit2:retrofit:2.1.0'
compile 'com.squareup.okhttp3:logging-interceptor:3.3.1'

### Quick Setup ###
Do required changes in app.properties file in assets folder.
```
CLIENT_ID={client_id}
CLIENT_SECRET={client_secret}
AUTHENTICATION_SERVER_URL={server_url}
API_VERSION={api_version}
LOCALE={language_code}
```

#### Chang in service ####
Change API method calls path inside aouth.service package classes to match your request parameters.
e.g.
```
public interface IOauthService {
     @POST("user/access/token") // change Post perameters as per your API need
     Call<AccessToken> getAccessToken(@Body AccessToken accessTokenRequest);
 }
 ```
