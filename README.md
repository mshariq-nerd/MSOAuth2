## MSOAuth2 Android client ##
This repository contains an example of a sign up/sign in process using OAuth 2.0. An API client implementation that
follows OAuth 2.0 mechanism for user authentication using [Retrofit2](https://square.github.io/retrofit/).

### Configuration ###
* Compatible with Android API Level 17 (Android 4.2) and higher
* Version 1.0

### Library Used (Using gradle) ###
```
compile 'com.android.support:design:25.1.1'
compile 'com.wdullaer:materialdatetimepicker:3.1.0'
compile 'de.hdodenhof:circleimageview:2.1.0'
compile 'com.squareup.retrofit2:converter-gson:2.0.0'
compile 'com.squareup.retrofit2:retrofit:2.1.0'
compile 'com.squareup.okhttp3:logging-interceptor:3.3.1'
```

### Quick Setup ###
Once you are setup with the web part [authOauth](https://github.com/nerdapplabs/authOauth/blob/master/README.md) and had added a valid OAuth2.0 client.
Do required changes in /app.properties file in assets folder.
```
CLIENT_ID={client_id}
CLIENT_SECRET={client_secret}
AUTHENTICATION_SERVER_URL={server_url}
API_VERSION={api_version}
LOCALE={language_code}
```

#### Change in service ####
Change API method calls path inside **oauth.service** package classes to match your request parameters.
e.g.
```java
public interface IOauthService {
     @POST("user/access/token") // change  URL as per your configured API
     Call<AccessToken> getAccessToken(@Body AccessToken accessTokenRequest);
 }
```

### Localization ###
Change **LOCALE** value in **app.properties** file in assets folder to change application language.
e.g.
```
#hindi language support
LOCALE=hi
```

To add support for more locales, create additional directories inside **res/** folder.
Each directory's name should adhere to the following format:
```
<resource type>-b+<language code>[+<country code>]
```
e.g.
```
# Hindi strings (hi locale)
/values-hi/strings.xml:
```

Rebuild the app and localization done!. For more information please read [Supporting Different Languages](https://developer.android.com/training/basics/supporting-devices/languages.html#CreateDirs)

Stay tuned, more to come. Check **TODO** list.

### TODO ###
1. Refresh token implementation.
2. Allow language selection in settings. Then app.properties {Locale} value will work as a default value
and user can choose App language at run time via settings.
