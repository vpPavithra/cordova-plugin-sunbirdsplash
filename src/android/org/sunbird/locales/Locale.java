package org.sunbird.locales;

public class Locale {

    public static final String ENGLISH = "en";
    public static final String HINDI = "hi";
    public static final String MARATHI = "mr";
    public static final String TAMIL = "ta";
    public static final String TELUGU = "te";
    public static String appName = "Sunbird";

    public void setAppName(String appName) {
        Locale.appName = appName;
    }

    public interface En {
        String IMPORT_SUCCESS = "Successfully imported";
        String IMPORT_ERROR = "Import Failed";
        String IMPORT_PROGRESS = "Importing.. Please wait";
        String IMPORTING_COUNT = "Importing ";
        String NOT_COMPATIBLE = "Import failed. Lesson not supported.";
        String CONTENT_EXPIRED = "Import failed. Lesson expired";
        String ALREADY_EXIST = "The file is already imported. Please select a new file";
        String IMPORT_ECAR = "Incompatible file format. You can only import .ecar , .epar and .gsa files";
        String WELCOME = "Welcome to " + appName+"!";
    }

    public interface Hi {
        String IMPORT_SUCCESS = "आयात सफल रही।";
        String IMPORT_ERROR = "आयात असफल।";
        String IMPORT_PROGRESS = "आयात कर रहा.. कृपया प्रतीक्षा करें";
        String IMPORTING_COUNT = "Importing ";
        String NOT_COMPATIBLE = "Import failed. Lesson not supported.";
        String CONTENT_EXPIRED = "Import failed. Lesson expired";
        String ALREADY_EXIST = "The file is already imported. Please select a new file";
        String IMPORT_ECAR = "Incompatible file format. You can only import .ecar , .epar and .gsa files";
        String WELCOME = appName + " में आपका स्वागत है!";
    }

    public interface Mr {
        String IMPORT_SUCCESS = "इम्पोर्ट यशस्वी";
        String IMPORT_ERROR = "ची आयात अयशस्वी";
        String IMPORT_PROGRESS = "इम्पोर्ट होत आहे, थोडा वेळ थांबा.";
        String IMPORTING_COUNT = "Importing ";
        String NOT_COMPATIBLE = "Import failed. Lesson not supported.";
        String CONTENT_EXPIRED = "Import failed. Lesson expired";
        String ALREADY_EXIST = "The file is already imported. Please select a new file";
        String IMPORT_ECAR = "Incompatible file format. You can only import .ecar , .epar and .gsa files";
        String WELCOME = appName + " वर तुमचे स्वागत!";
    }

    public interface Ta {
        String IMPORT_SUCCESS = "வெற்றிகரமாக தகவல்கள் வந்து அடைந்தது";
        String IMPORT_ERROR = "Import Failed";
        String IMPORT_PROGRESS = "Importing.. Please wait";
        String IMPORTING_COUNT = "Importing ";
        String NOT_COMPATIBLE = "Import failed. Lesson not supported.";
        String CONTENT_EXPIRED = "Import failed. Lesson expired";
        String ALREADY_EXIST = "The file is already imported. Please select a new file";
        String IMPORT_ECAR = "Incompatible file format. You can only import .ecar , .epar and .gsa files";
        String WELCOME = appName + "!  க்கு வருக!";
    }

    public interface Te {
        String IMPORT_SUCCESS = "దిగుమతి విజయవంతమైనది";
        String IMPORT_ERROR = "దిగుమతి జరగలేదు";
        String IMPORT_PROGRESS = "దిగుమతి అవుతున్నది. దయచేసి వేచి ఉండండి&#8230;";
        String IMPORTING_COUNT = "Importing ";
        String NOT_COMPATIBLE = "Import failed. Lesson not supported.";
        String CONTENT_EXPIRED = "Import failed. Lesson expired";
        String ALREADY_EXIST = "The file is already imported. Please select a new file";
        String IMPORT_ECAR = "Incompatible file format. You can only import .ecar , .epar and .gsa files";
        String WELCOME = appName + " to కు స్వాగతం!";
    }
}
