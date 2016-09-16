package com.app.infideap.readcontact.controller.access.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app.infideap.readcontact.R;
import com.app.infideap.readcontact.util.Common;
import com.app.infideap.readcontact.util.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by InfiDeaP on 4/12/2015.
 */
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();
    private OnImageTakenListener listener;
    private File photo;
    //    private float IMAGE_SIZE = 720f;
    private String foldername = File.separator + "claim" + File.separator + "image";
    protected FirebaseDatabase database;
    protected FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (database==null){
//            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        }
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();



    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAppPermission();

        database.getReference(Constant.USER).child(Common.getSimSerialNumber(this))
                .child(Constant.STATUS).child(Constant.ACTIVE).setValue(1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        database.getReference(Constant.USER).child(Common.getSimSerialNumber(this))
                .child(Constant.STATUS).child(Constant.ACTIVE).setValue(0);
        database.getReference(Constant.USER).child(Common.getSimSerialNumber(this))
                .child(Constant.STATUS).child(Constant.LAST_SEEN).setValue(System.currentTimeMillis());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
//                overridePendingTransition(R.anim.do_nothing, R.anim.slid_right);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public boolean checkAppPermission() {
        final List<String> permissions = new ArrayList<>();
        boolean showMessage = readContact(permissions) || readPhoneState(permissions)
                || getAccount(permissions);


        if (permissions.size() > 0) {
            final String strings[] = new String[permissions.size()];

            if (showMessage) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setMessage(R.string.permissionrequestmessage)
                        .setPositiveButton(R.string.gotit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(BaseActivity.this,
                                        permissions.toArray(strings),
                                        Constant.PERMISSION_REQUEST);
                            }
                        });
                builder.create().show();
            } else
                ActivityCompat.requestPermissions(this,
                        permissions.toArray(strings),
                        Constant.PERMISSION_REQUEST);
        }

        return permissions.size() > 0;
    }

    private boolean writeExternalStorage(List<String> permissions) {
        if (ActivityCompat.checkSelfPermission(
                getApplication(), Manifest
                        .permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                return true;

            }

        }
        return false;
    }

    private boolean writeSetting(List<String> permissions) {
        if (ActivityCompat.checkSelfPermission(
                getApplication(), Manifest
                        .permission.WRITE_SETTINGS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            permissions.add(Manifest.permission.WRITE_SETTINGS);

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_SETTINGS)) {
                return true;

            }

        }
        return false;
    }

    private boolean recordAudio(List<String> permissions) {
        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

                return true;
            }

        }
        return false;
    }

    private boolean readPhoneState(List<String> permissions) {
        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

                return true;


            }
        }
        return false;
    }

    private boolean readContact(List<String> permissions) {
        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_CONTACTS);

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                return true;


            }
        }
        return false;
    }

    private boolean getAccount(List<String> permissions) {
        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.GET_ACCOUNTS);

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.GET_ACCOUNTS)) {

                return true;


            }
        }
        return false;
    }

    private boolean location(List<String> permissions) {
        if (ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                return true;

            }

        }
        return false;
    }

    protected boolean checkSystemWritePermission() {
        boolean retVal = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(this);
            Log.d(TAG, "Can Write Settings: " + retVal);
            if (retVal) {
                Toast.makeText(this, "Write allowed :-)", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Write not allowed :-(", Toast.LENGTH_LONG).show();
                openAndroidPermissionsMenu();
            }
        }
        return retVal;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void openAndroidPermissionsMenu() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    public void addFragment(int id, Fragment fragment) {
        getSupportFragmentManager().beginTransaction().add(id, fragment).commit();

    }

    public void displayFragment(int id, Fragment fragment) {
        try {
            getSupportFragmentManager().beginTransaction().replace(id, fragment).commitAllowingStateLoss();
        } catch (Exception e) {

        }

    }

    public void removeFragment(int id, Fragment fragment) {
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }


    public void captureImage(View view) {

    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    Uri mLocationForPhotos = Uri.fromFile(Environment.getExternalStorageDirectory());

    static final int REQUEST_IMAGE_GET = 2;

    public void selectImage(OnImageTakenListener listener) {
        this.listener = listener;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    public void capturePhoto(String targetFilename, OnImageTakenListener listener) {
//        mLocationForPhotos =
//        targetFilename = getResources().getString(R.string.app_name).concat("/")+targetFilename;
        this.listener = listener;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//        if (Environment.getExternalStorageDirectory().equals(Environment.MEDIA_MOUNTED)) {
//        String extStore = System.getenv("EXTERNAL_STORAGE");

        File direct = new File(Environment.getExternalStorageDirectory() + foldername);
        if (!direct.exists()) {
            direct.mkdirs();
        }

        photo = new File(Environment.getExternalStorageDirectory().toString() +
                foldername, targetFilename);
//        try {
//            mLocationForPhotos = convertFileToContentUri(this, photo);
//        } catch (Exception e) {
        Log.d(TAG, photo.getAbsolutePath());
        mLocationForPhotos = Uri.fromFile(photo);


//        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                mLocationForPhotos);
        if (intent.resolveActivity(getPackageManager()) != null) {
            Log.d(TAG, "REQUEST");
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }


    public interface OnImageTakenListener {
        void onTaken(Bitmap bitmap);
    }

    public interface OnItemDelecteListener {
        int onDeleted();

        void onCanceled();
    }
}
