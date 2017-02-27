package com.nerdapplabs.msoauth2.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.system.ErrnoException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.nerdapplabs.msoauth2.MSOAuth2;
import com.nerdapplabs.msoauth2.R;
import com.nerdapplabs.msoauth2.oauth.client.UserServiceClient;
import com.nerdapplabs.msoauth2.oauth.constant.OAuthConstant;
import com.nerdapplabs.msoauth2.pojo.User;
import com.nerdapplabs.msoauth2.utility.ErrorType;
import com.nerdapplabs.msoauth2.utility.ImageCompress;
import com.nerdapplabs.msoauth2.utility.MessageSnackbar;
import com.nerdapplabs.msoauth2.utility.NetworkConnectivity;
import com.nerdapplabs.msoauth2.utility.Preferences;
import com.nerdapplabs.msoauth2.utility.ReadProperties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity implements NetworkConnectivity.ConnectivityReceiverListener,
        View.OnClickListener {
    private static final String TAG = UserProfileActivity.class.getSimpleName();
    private TextView txtUserProfileName, txtUserName,
            txtUserEmail, txtUserDOB;
    FloatingActionButton btnEditProfile;

    private ImageView userProfilePic;
    User user = null;

    private Uri mCropImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        txtUserProfileName = (TextView) findViewById(R.id.txt_user_profile_name);
        txtUserName = (TextView) findViewById(R.id.txt_user_name);
        txtUserEmail = (TextView) findViewById(R.id.txt_user_email);
        txtUserDOB = (TextView) findViewById(R.id.txt_user_dob);
        btnEditProfile = (FloatingActionButton) findViewById(R.id.btn_change_profile_pic);
        userProfilePic = (ImageView) findViewById(R.id.user_profile_photo);

        Button btnLogout = (Button) findViewById(R.id.btn_logout);
        TextView btnChangePassword = (TextView) findViewById(R.id.btn_change_password);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
            mTitle.setText(getString(R.string.user_profile_activity_title));
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        btnLogout.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);
        btnEditProfile.setOnClickListener(this);

        new UserProfileAsyncTaskRunner().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register internet connection status listener
        MSOAuth2.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        NetworkConnectivity.showNetworkConnectMessage(this, isConnected);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_logout) {
            logout();
        }

        if (view.getId() == R.id.btn_change_profile_pic) {
            startActivityForResult(getPickImageChooserIntent(), 200);
        }

        if (view.getId() == R.id.btn_change_password) {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private class UserProfileAsyncTaskRunner extends AsyncTask<Void, Void, Boolean> {
        final ProgressDialog progressDialog = new ProgressDialog(UserProfileActivity.this,
                R.style.AppTheme_Dark_Dialog);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean isNetworkConnected = false;
            if (NetworkConnectivity.isConnected()) {
                try {
                    isNetworkConnected = true;
                    // Get user profile details
                    user = new UserServiceClient().getUserProfile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return isNetworkConnected;
        }

        @Override
        protected void onPostExecute(Boolean isConnected) {
            super.onPostExecute(isConnected);
            progressDialog.dismiss();
            if (!isConnected) {
                NetworkConnectivity.showNetworkConnectMessage(UserProfileActivity.this, false);
                return;
            }

            switch (user.getCode()) {
                case OAuthConstant.HTTP_INTERNAL_SERVER_ERROR:
                    MessageSnackbar.showMessage(UserProfileActivity.this, getString(R.string.server_error), ErrorType.ERROR);
                    break;
                case OAuthConstant.HTTP_UNAUTHORIZED:
                    Preferences.clear();
                    pageNavigationActions(OAuthConstant.HTTP_UNAUTHORIZED);
                    break;
                case OAuthConstant.HTTP_SERVER_NOT_FOUND_ERROR:
                    Preferences.clear();
                    pageNavigationActions(OAuthConstant.HTTP_SERVER_NOT_FOUND_ERROR);
                    break;
                case OAuthConstant.HTTP_OK:
                case OAuthConstant.HTTP_CREATED:
                    updateUserProfile();
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.invalidateOptionsMenu();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_edit_profile).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            pageNavigationActions(id);

        }
        if (id == R.id.action_edit_profile) {
            Intent intent = new Intent(this, EditProfileActivity.class);
            intent.putExtra("User", user);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.logout);
        builder.setMessage(R.string.logout_alert_message);
        builder.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, " " + which);
                Preferences.clear();

                pageNavigationActions(which);
            }
        });
        builder.setNegativeButton(R.string.alert_cancel, null);
        builder.show();
    }

    private void pageNavigationActions(int code) {
        Intent intent;
        switch (code) {
            case OAuthConstant.HTTP_UNAUTHORIZED:
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra("failure_msg", getString(R.string.session_expired_message));
                break;
            case OAuthConstant.HTTP_SERVER_NOT_FOUND_ERROR:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("failure_msg", getString(R.string.server_not_found_error));
                break;
            default:
                intent = new Intent(getApplicationContext(), MainActivity.class);
        }
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * Method to update user profile data
     */
    private void updateUserProfile() {
        txtUserProfileName.setText(user.getFirstName() + " " + user.getLastName());
        txtUserName.setText(user.getUserName());
        txtUserEmail.setText(user.getEmailAddress());
        txtUserDOB.setText(user.getDob());

        // load user profile image
        try {
            Uri uri = Uri.parse(ReadProperties.buildURL() + user.getImageURL());
            Preferences.putString(OAuthConstant.IMAGE_URI, uri.toString());
            Log.d(TAG, uri.toString());

            File file = new File(user.getImageURL());
            Glide.with(this).load(uri).centerCrop().
                    placeholder(R.drawable.ic_face_black_48dp)
                    .transform(new ImageCompress(UserProfileActivity.this))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .signature(new StringSignature(file.length() + "@" + file.lastModified()))
                    .into(userProfilePic);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = getPickImageResultUri(data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
            boolean requirePermissions = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    isUriRequiresPermissions(imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }

            if (!requirePermissions) {
                String imagePath;
                if (null != data) {
                    imagePath = ImageCompress.getRealPathFromUri(this, imageUri);
                } else {
                    imagePath = imageUri.getPath();
                }

                File file = new File(imagePath);
                Glide.with(this).load(imageUri).centerCrop().
                        placeholder(R.drawable.ic_face_black_48dp)
                        .transform(new ImageCompress(UserProfileActivity.this))
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .signature(new StringSignature(file.length() + "@" + file.lastModified()))
                        .into(userProfilePic);

                new UploadImageAsyncTask().execute(imagePath);
            }
        }
    }

    private class UploadImageAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                String imageName = params[0].substring(params[0].lastIndexOf("/") + 1);
                Log.d("Image Name ", imageName);
                String image = ImageCompress.resizeAndCompressImageBeforeSend(UserProfileActivity.this, params[0], imageName);

                Log.d("Image Name to upload", imageName);
                new UserServiceClient().editProfilePic(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            userProfilePic.setImageURI(mCropImageUri);
        } else {
            Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }


        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }


    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    /**
     * Test if we can open the given Android URI to test if permission required error is thrown.<br>
     */
    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }
}