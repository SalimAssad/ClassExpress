package com.chacostak.salim.classexpress.Configuration.Backup_and_restore;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.chacostak.salim.classexpress.Data_Base.DB_Helper;
import com.chacostak.salim.classexpress.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by Salim on 18/02/2016.
 */
public class FilePickerDialog extends Dialog implements AdapterView.OnItemClickListener {

    ListView fileList;
    ArrayAdapter adapter;

    ArrayList<String> directories;

    File root;
    File currentFolder;

    public FilePickerDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_picker_dialog);

        setTitle(R.string.restore);

        root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        currentFolder = root;

        directories = new ArrayList<String>();

        setDirectories(root);

        fileList = (ListView) findViewById(R.id.fileList);
        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, directories);
        fileList.setAdapter(adapter);

        fileList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File selected = new File(String.valueOf(fileList.getItemAtPosition(position)));
        if (selected.isDirectory()) {
            setDirectories(selected);
            currentFolder = selected;
        } else if (selected.getName().equals(DB_Helper.DB_Name)) {
            restore(selected.getAbsolutePath());
        } else { //When it gets something that isn't the directory or the backup, say ..parent, it will return to the directory's parent
            currentFolder = currentFolder.getParentFile();
            setDirectories(currentFolder);
        }

        adapter.notifyDataSetChanged();
    }

    private void setDirectories(File selected) {
        File files[] = selected.listFiles();
        directories.clear();

        if(selected.getParentFile() != null)
            directories.add("..parent");

        for (File file : files) {
            if (file.isDirectory() || file.getName().equals(DB_Helper.DB_Name))
                directories.add(file.getPath());
        }
    }

    private void restore(String absolutePath) {
        try {
            File data = Environment.getDataDirectory();

            String currentDBPath = "//data//com.chacostak.salim.classexpress//databases//Class scheduler";
            File currentDB = new File(data, currentDBPath);
            File restoreDB = new File(absolutePath);

            if (restoreDB.exists()) {
                FileChannel src = new FileInputStream(restoreDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getContext(), getContext().getString(R.string.restore_successful), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d("Exception: ", e.toString());
        }
    }
}
