package com.chacostak.salim.classexpress.Configuration.Backup_and_restore;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chacostak.salim.classexpress.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by Salim on 01/06/2015.
 */
public class Fragment_backup extends Fragment implements View.OnClickListener {

    View v;
    String backupDBPath;

    FilePickerDialog filePickerDialog = null;

    static final int REQUEST_EXTERNAL_STORAGE = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_backup, container, false);

        v.findViewById(R.id.backupButton).setOnClickListener(this);
        v.findViewById(R.id.restoreButton).setOnClickListener(this);

        backupDBPath = Environment.getExternalStorageDirectory() + File.separator + "ClassExpress";

        if (verifyStoragePermissions(getActivity())) {
            File file = new File(backupDBPath);
            if (!file.exists()) {
                Log.d("Created directory: ", String.valueOf(file.mkdirs()));
            }
        }

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backupButton:
                try {
                    File data = Environment.getDataDirectory();

                    String currentDBPath = "//data//com.chacostak.salim.classexpress//databases//Class scheduler";
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(backupDBPath + File.separator + "Class scheduler");

                    if (currentDB.exists()) {
                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                        MediaScannerConnection.scanFile(getActivity(), new String[]{backupDBPath + File.separator + "Class scheduler"}, null, null);
                        Toast.makeText(getActivity(), getString(R.string.backup_successful), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.d("Exception: ", e.toString());
                }
                break;
            case R.id.restoreButton:
                if(filePickerDialog == null)
                    filePickerDialog = new FilePickerDialog(getActivity());

                filePickerDialog.show();
                break;
        }
    }

    public static boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE
            );

            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // permission was granted
                    File file = new File(backupDBPath);
                    if (!file.exists()) {
                        Log.d("Created directory: ", String.valueOf(file.mkdirs()));
                    }
                } else { // permission denied
                    getActivity().finish();
                }
                return;
            }
        }
    }
}