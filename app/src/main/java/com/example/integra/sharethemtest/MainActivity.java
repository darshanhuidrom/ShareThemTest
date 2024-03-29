package com.example.integra.sharethemtest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.integra.sharethem.reciever.ReceiverActivity;
import com.example.integra.sharethem.sender.SHAREthemActivity;
import com.example.integra.sharethem.sender.SHAREthemService;
import com.example.integra.sharethem.utils.HotspotControl;
import com.example.integra.sharethem.utils.Utils;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;

import static com.example.integra.sharethem.utils.Utils.DEFAULT_PORT_OREO;

public class MainActivity extends AppCompatActivity {

    FilePickerDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));
    }

    public void sendFiles(View view) {
        if (Utils.isShareServiceRunning(getApplicationContext())) {
            startActivity(new Intent(getApplicationContext(), SHAREthemActivity.class));
            return;
        }
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.MULTI_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        dialog = new FilePickerDialog(this, properties);
        dialog.setTitle("Select files to share");

        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (null == files || files.length == 0) {
                    Toast.makeText(MainActivity.this, "Select at least one file to start Share Mode", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), SHAREthemActivity.class);
                intent.putExtra(SHAREthemService.EXTRA_FILE_PATHS, files);
                /*
                 * PORT value is hardcoded for Oreo & above since it's not possible to set SSID with which port info can be extracted on Receiver side.
                 */
                intent.putExtra(SHAREthemService.EXTRA_PORT, DEFAULT_PORT_OREO);
                /*
                 * Sender name can't be relayed to receiver for Oreo & above
                 */
                intent.putExtra(SHAREthemService.EXTRA_SENDER_NAME, "Sri");
                startActivity(intent);
            }
        });
        dialog.show();
    }

    public void receiveFiles(View view) {
        HotspotControl hotspotControl = HotspotControl.getInstance(getApplicationContext());
        if (null != hotspotControl && hotspotControl.isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Sender(Hotspot) mode is active. Please disable it to proceed with Receiver mode");
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.show();
            return;
        }
        startActivity(new Intent(getApplicationContext(), ReceiverActivity.class));
    }

    //Add this method to show Dialog when the required permission has been granted to the app.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (dialog != null) {   //Show dialog if the read permission has been granted.
                        dialog.show();
                    }
                } else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(this, "Permission is Required for getting list of files", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
