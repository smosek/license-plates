package com.mosek.licenseplates;

import java.text.ParseException;
import java.util.Date;

class LicenseData {

    public static final String FIELD_SEP = "\t";

    public String getMake() {
        return make;
    }

    public int getLicensePrefix() {
        return licensePrefix;
    }

    public Date getAddDate() {
        return addDate;
    }

    private String make;
    private int licensePrefix;
    private Date addDate;

    public LicenseData(String make, int licensePrefix, Date addDate) {
        this.make = make;
        this.licensePrefix = licensePrefix;
        this.addDate = addDate;
    }

    public String toLine() {
        return make + FIELD_SEP + Integer.toString(licensePrefix) + FIELD_SEP + MainActivity.dateFormat.format(addDate);
    }

    public LicenseData(String line) throws ParseException {
        String[] parts = line.split(FIELD_SEP);
        make = parts[0];
        licensePrefix = Integer.parseInt(parts[1]);
        addDate = MainActivity.dateFormat.parse(parts[2]);
    }
}
