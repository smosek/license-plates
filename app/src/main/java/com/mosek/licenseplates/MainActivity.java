package com.mosek.licenseplates;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mosek.licenseplates.myfirstapp.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> carMakes;
    private Dictionary<String, MakeData> carMakeData;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readFiles();
        setAutoComplete();
        setSuffixes();
    }

    private void setAutoComplete() {
        AutoCompleteTextView carMakeView = (AutoCompleteTextView) findViewById(R.id.carMake);
        carMakeView.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, carMakes));
        carMakeView.addTextChangedListener(new CarMakeTextWatcher());
    }

    private void setSuffixes() {
        Spinner spinner = (Spinner) findViewById(R.id.licenseSuffix);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.license_suffixes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private void readFiles() {
        //read all the files
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void readFile(int suffix) {
        File dataFile = getDataFile(suffix);
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        carMakes = new ArrayList<String>();
        carMakeData = new Hashtable<String, MakeData>();
        try {
            FileReader reader = new FileReader(dataFile);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                LicenseData data = null;
                try {
                    data = new LicenseData(line);
                    addMakeIfNeeded(data.getMake());
                    addMakePrefix(data.getMake(), data.getLicensePrefix());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            reader.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void addMakePrefix(String make, int prefix) {
        carMakeData.get(make).addPrefix(prefix);
    }

    private void addMakeIfNeeded(String make) {
        if (!carMakes.contains(make)) {
            carMakes.add(make);
            carMakeData.put(make, new MakeData(make));
        }
    }

    @NonNull
    private File getDataFile(int suffix) {
        return new File(this.getFilesDir(), String.format("data%02d.txt", suffix));
    }

    private File getBackupFile() {
        return new File(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "licenseData.txt");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void writeToFile(File dataFile, LicenseData data) throws IOException {
        FileWriter writer = new FileWriter(dataFile, true);
        writer.write(data.toLine() + System.lineSeparator());
        writer.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void saveNumber(View view) {
        AutoCompleteTextView carMakeView = (AutoCompleteTextView) findViewById(R.id.carMake);
        EditText licensePrefixText = (EditText) findViewById(R.id.licenseNumber);
        String make = carMakeView.getText().toString();
        LicenseData data = new LicenseData(make, Integer.parseInt(licensePrefixText.getText().toString()), new Date());
        try {
            writeToFile(getDataFile(getSuffix()), data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        carMakeView.setText("");
        licensePrefixText.setText("");
        if (!carMakes.contains(make)) {
            addMakeIfNeeded(make);
        }
        addMakePrefix(make, data.getLicensePrefix());
        TextView currentNumbers = (TextView) findViewById(R.id.currentNumbers);
        currentNumbers.setText("");
        carMakeView.requestFocus();
    }

    public void backupFile(View view)
    {
        try {
            File dataFile = getDataFile(getSuffix());
            File backupFile = getBackupFile();
            copyFile(dataFile, backupFile);
            Toast.makeText(this, "Backup saved successfully to " + backupFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        }
        catch (Exception ex) {
            Toast.makeText(this, "Error backing up", Toast.LENGTH_SHORT).show();
        }
    }

    public void restoreFile(View view)
    {
        try {
            File dataFile = getDataFile(getSuffix());
            File backupFile = getBackupFile();
            copyFile(backupFile, dataFile);
            Toast.makeText(this, "Backup restored successfully from " + backupFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        }
        catch (Exception ex) {
            Toast.makeText(this, "Error backing up", Toast.LENGTH_SHORT).show();
        }
    }

    private int getSuffix() {
        Spinner suffix = (Spinner) findViewById(R.id.licenseSuffix);
        return Integer.parseInt((String) suffix.getSelectedItem());
    }

    private void copyFile(File sourceFile, File targetFile) throws IOException {
        FileChannel source = new FileInputStream(sourceFile).getChannel();
        FileChannel target = new FileOutputStream(targetFile).getChannel();
        if (target != null && source != null) {
            target.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (target != null) {
            target.close();
        }
    }

    public void sendFile(View view)
    {
        File dataFile = getDataFile(getSuffix());
        Uri path = Uri.fromFile(dataFile);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent .setType("vnd.android.cursor.dir/email");
        String to[] = {"smosek@gmail.com"};
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
// the attachment
        emailIntent .putExtra(Intent.EXTRA_STREAM, path);
// the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "License backup");
        startActivity(Intent.createChooser(emailIntent , "Send email..."));
    }

    private class CarMakeTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            EditText carMake = (EditText) findViewById(R.id.carMake);
            TextView currentNumbers = (TextView) findViewById(R.id.currentNumbers);
            MakeData makeData = carMakeData.get(carMake.getText().toString());
            String text = "";
            if (makeData != null)
                text = makeData.getLicensePrefixesString();
            try {
                currentNumbers.setText(text);
            } catch (Exception ex) {
                Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
