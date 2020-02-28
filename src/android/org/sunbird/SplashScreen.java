package org.sunbird;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sunbird.deeplinks.DeepLinkNavigation;
import org.sunbird.locales.Locale;
import org.sunbird.util.ImportExportUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashScreen extends CordovaPlugin {

    private static final String TAG = "SplashScreen";

    private static final String LOG_TAG = "SplashScreen";
    private static final String KEY_LOGO = "app_logo";
    private static final String KEY_NAME = "app_name";
    private static final String KEY_IS_FIRST_TIME = "is_first_time";

    private static final int DEFAULT_SPLASHSCREEN_DURATION = 3000;
    private static final int DEFAULT_FADE_DURATION = 500;

    private static final int IMPORT_SUCCESS = 1;
    private static final int IMPORT_ERROR = 2;
    private static final int IMPORT_PROGRESS = 3;
    private static final int IMPORTING_COUNT = 4;
    private static final int IMPORT_FAILED = 5;
    private static final int NOT_COMPATIBLE = 6;
    private static final int CONTENT_EXPIRED = 7;
    private static final int ALREADY_EXIST = 8;

    private static Dialog splashDialog;
    private ImageView splashImageView;
    private TextView importStatusTextView;
    private int orientation;
    private SharedPreferences splashSharedPreferences;
    private SharedPreferences appSharedPreferences;
    private volatile boolean importingInProgress;
    private DeepLinkNavigation mDeepLinkNavigation;
    private ArrayList<CallbackContext> mHandler = new ArrayList<>();
    private JSONObject mLastEvent;
    private String localeSelected;
    private Intent deepLinkIntent;
    private JSONArray actions = new JSONArray();

    private static int getIdOfResource(CordovaInterface cordova, String name, String resourceType) {
        return cordova.getActivity().getResources().getIdentifier(name, resourceType,
                cordova.getActivity().getApplicationInfo().packageName);
    }

    private String getRelevantMessage(String localeSelected, int type) {
        String message = null;
        switch (type) {
        case IMPORT_SUCCESS:
            if (localeSelected.equalsIgnoreCase(Locale.HINDI)) {
                message = Locale.Hi.IMPORT_SUCCESS;
            } else if (localeSelected.equalsIgnoreCase(Locale.MARATHI)) {
                message = Locale.Mr.IMPORT_SUCCESS;
            } else if (localeSelected.equalsIgnoreCase(Locale.TELUGU)) {
                message = Locale.Te.IMPORT_SUCCESS;
            } else if (localeSelected.equalsIgnoreCase(Locale.TAMIL)) {
                message = Locale.Ta.IMPORT_SUCCESS;
            } else {
                message = Locale.En.IMPORT_SUCCESS;
            }
            break;

        case IMPORT_ERROR:
            if (localeSelected.equalsIgnoreCase(Locale.HINDI)) {
                message = Locale.Hi.IMPORT_ERROR;
            } else if (localeSelected.equalsIgnoreCase(Locale.MARATHI)) {
                message = Locale.Mr.IMPORT_ERROR;
            } else if (localeSelected.equalsIgnoreCase(Locale.TELUGU)) {
                message = Locale.Te.IMPORT_ERROR;
            } else if (localeSelected.equalsIgnoreCase(Locale.TAMIL)) {
                message = Locale.Ta.IMPORT_ERROR;
            } else {
                message = Locale.En.IMPORT_ERROR;
            }
            break;

        case IMPORT_PROGRESS:
            if (localeSelected.equalsIgnoreCase(Locale.HINDI)) {
                message = Locale.Hi.IMPORT_PROGRESS;
            } else if (localeSelected.equalsIgnoreCase(Locale.MARATHI)) {
                message = Locale.Mr.IMPORT_PROGRESS;
            } else if (localeSelected.equalsIgnoreCase(Locale.TELUGU)) {
                message = Locale.Te.IMPORT_PROGRESS;
            } else if (localeSelected.equalsIgnoreCase(Locale.TAMIL)) {
                message = Locale.Ta.IMPORT_PROGRESS;
            } else {
                message = Locale.En.IMPORT_PROGRESS;
            }
            break;

        case IMPORTING_COUNT:
            if (localeSelected.equalsIgnoreCase(Locale.HINDI)) {
                message = Locale.Hi.IMPORTING_COUNT;
            } else if (localeSelected.equalsIgnoreCase(Locale.MARATHI)) {
                message = Locale.Mr.IMPORTING_COUNT;
            } else if (localeSelected.equalsIgnoreCase(Locale.TELUGU)) {
                message = Locale.Te.IMPORTING_COUNT;
            } else if (localeSelected.equalsIgnoreCase(Locale.TAMIL)) {
                message = Locale.Ta.IMPORTING_COUNT;
            } else {
                message = Locale.En.IMPORTING_COUNT;
            }
            break;

        case NOT_COMPATIBLE:
            if (localeSelected.equalsIgnoreCase(Locale.HINDI)) {
                message = Locale.Hi.NOT_COMPATIBLE;
            } else if (localeSelected.equalsIgnoreCase(Locale.MARATHI)) {
                message = Locale.Mr.NOT_COMPATIBLE;
            } else if (localeSelected.equalsIgnoreCase(Locale.TELUGU)) {
                message = Locale.Te.NOT_COMPATIBLE;
            } else if (localeSelected.equalsIgnoreCase(Locale.TAMIL)) {
                message = Locale.Ta.NOT_COMPATIBLE;
            } else {
                message = Locale.En.NOT_COMPATIBLE;
            }
            break;

        case CONTENT_EXPIRED:
            if (localeSelected.equalsIgnoreCase(Locale.HINDI)) {
                message = Locale.Hi.CONTENT_EXPIRED;
            } else if (localeSelected.equalsIgnoreCase(Locale.MARATHI)) {
                message = Locale.Mr.CONTENT_EXPIRED;
            } else if (localeSelected.equalsIgnoreCase(Locale.TELUGU)) {
                message = Locale.Te.CONTENT_EXPIRED;
            } else if (localeSelected.equalsIgnoreCase(Locale.TAMIL)) {
                message = Locale.Ta.CONTENT_EXPIRED;
            } else {
                message = Locale.En.CONTENT_EXPIRED;
            }
            break;

        case ALREADY_EXIST:
            if (localeSelected.equalsIgnoreCase(Locale.HINDI)) {
                message = Locale.Hi.ALREADY_EXIST;
            } else if (localeSelected.equalsIgnoreCase(Locale.MARATHI)) {
                message = Locale.Mr.ALREADY_EXIST;
            } else if (localeSelected.equalsIgnoreCase(Locale.TELUGU)) {
                message = Locale.Te.ALREADY_EXIST;
            } else if (localeSelected.equalsIgnoreCase(Locale.TAMIL)) {
                message = Locale.Ta.ALREADY_EXIST;
            } else {
                message = Locale.En.ALREADY_EXIST;
            }
            break;
        }

        return message;
    }

    // Helper to be compile-time compatible with both Cordova 3.x and 4.x.
    private View getView() {
        try {
            return (View) webView.getClass().getMethod("getView").invoke(webView);
        } catch (Exception e) {
            return (View) webView;
        }
    }

    private int getSplashId() {
        int drawableId = 0;
        String splashResource = "screen";
        drawableId = cordova.getActivity().getResources().getIdentifier(splashResource, "drawable",
                cordova.getActivity().getClass().getPackage().getName());
        if (drawableId == 0) {
            drawableId = cordova.getActivity().getResources().getIdentifier(splashResource, "drawable",
                    cordova.getActivity().getPackageName());
        }
        return drawableId;
    }

    @Override
    protected void pluginInitialize() {
        splashSharedPreferences = cordova.getActivity().getSharedPreferences("SUNBIRD_SPLASH", Context.MODE_PRIVATE);
        appSharedPreferences = cordova.getActivity().getSharedPreferences("org.ekstep.genieservices.preference_file",
                Context.MODE_PRIVATE);
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getView().setVisibility(View.INVISIBLE);
            }
        });
        // Save initial orientation.
        orientation = cordova.getActivity().getResources().getConfiguration().orientation;
        displaySplashScreen();

        mDeepLinkNavigation = new DeepLinkNavigation(cordova.getActivity());

        handleIntentForDeeplinking(cordova.getActivity().getIntent());
    }

    private int getFadeDuration() {
        return DEFAULT_FADE_DURATION;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause(boolean multitasking) {

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        this.hideSplashScreen(true);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("hide")) {
            if (!importingInProgress) {
                cordova.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        webView.postMessage("splashscreen", "hide");
                    }
                });
            }
        } else if (action.equals("show")) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    webView.postMessage("splashscreen", "show");
                }
            });
        } else if (action.equals("setContent")) {
            String appName = args.getString(0);
            String logoUrl = args.getString(1);
            cacheImageAndAppName(appName, logoUrl);
        } else if (action.equals("onDeepLink")) {
            mHandler.add(callbackContext);
            consumeEvents();
        } else if (action.equals("clearPrefs")) {
            if (splashSharedPreferences != null) {
                splashSharedPreferences.edit().clear().apply();
                callbackContext.success();
            }
        } else if (action.equals("setImportProgress")) {
            int currentCount = args.getInt(0);
            int totalCount = args.getInt(1);
            setImportProgress(currentCount, totalCount);
        } else if (action.equals("getActions")) {
            importingInProgress = true;
            callbackContext.success(actions.toString());
            actions = new JSONArray();
        } else if (action.equals("markImportDone")) {
            importingInProgress = false;
            hideSplashScreen(false);
            callbackContext.success();
        } else {
            return false;
        }

        return true;
    }

    private void setImportProgress(int currentCount, int totalCount) {
        localeSelected = appSharedPreferences.getString("sunbirdselected_language_code", "en");
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                String msg = getRelevantMessage(localeSelected, IMPORTING_COUNT);
                msg = msg + " (" + currentCount + "/" + totalCount + ")";
                importStatusTextView.setText(msg);
            }
        });
    }

    private void cacheImageAndAppName(String appName, String logoUrl) {
        int dim = getSplashDim(cordova.getActivity().getWindowManager().getDefaultDisplay());
        splashSharedPreferences.edit().putString(KEY_NAME, appName).putString(KEY_LOGO, logoUrl).apply();
        Glide.with(cordova.getActivity()).load(logoUrl).downloadOnly(dim, dim);
    }

    @Override
    public Object onMessage(String id, Object data) {
        if ("splashscreen".equals(id)) {
            if ("hide".equals(data.toString())) {
                hide();
            } else if ("show".equals(data.toString())) {
                this.displaySplashScreen();
            }
        }
        return null;
    }

    private void hide() {

        // To avoid black screen while content importing
        if (importingInProgress) {
            return;
        }

        this.hideSplashScreen(false);
        getView().setVisibility(View.VISIBLE);
    }

    // Don't add @Override so that plugin still compiles on 3.x.x for a while
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation != orientation) {
            orientation = newConfig.orientation;

            // Splash drawable may change with orientation, so reload it.
            if (splashImageView != null) {
                int drawableId = getSplashId();
                if (drawableId != 0) {
                    splashImageView.setImageDrawable(cordova.getActivity().getResources().getDrawable(drawableId));
                }
            }
        }
    }

    private void hideSplashScreen(final boolean forceHideImmediately) {
        // To avoid black screen while content importing
        if (importingInProgress) {
            return;
        }

        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (splashDialog != null) {
                    splashDialog.dismiss();
                    splashDialog = null;
                    splashImageView = null;
                }
            }
        });
    }

    private void generateTelemetry() throws JSONException {
        JSONObject impression = new JSONObject();
        impression.put("eid", "IMPRESSION");

        JSONObject impresionAction = new JSONObject();
        impresionAction.put("type", "TELEMETRY");
        impresionAction.put("payload", impression);

        actions.put(impresionAction);

        boolean isFirstTime = splashSharedPreferences.getBoolean(KEY_IS_FIRST_TIME, true);
        if (isFirstTime) {
            splashSharedPreferences.edit().putBoolean(KEY_IS_FIRST_TIME, false).apply();
        }

        JSONObject interact = new JSONObject();
        interact.put("eid", "INTERACT");

        JSONObject extraInfo = new JSONObject();
        extraInfo.put("isFirstTime", isFirstTime);
        interact.put("extraInfo", extraInfo);

        JSONObject interactAction = new JSONObject();
        interactAction.put("type", "TELEMETRY");
        interactAction.put("payload", interact);
        actions.put(interactAction);
    }

    /**
     * Shows the splash screen over the full Activity
     */
    @SuppressWarnings("deprecation")
    private void displaySplashScreen() {
        try {
            generateTelemetry();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final int splashscreenTime = DEFAULT_SPLASHSCREEN_DURATION;
        final int drawableId = getSplashId();

        final String appName = splashSharedPreferences.getString(KEY_NAME,
                cordova.getActivity().getString(getIdOfResource(cordova, "_app_name", "string")));
        final String logoUrl = splashSharedPreferences.getString(KEY_LOGO, "");

        final int fadeSplashScreenDuration = getFadeDuration();
        final int effectiveSplashDuration = Math.max(0, splashscreenTime - fadeSplashScreenDuration);

        // Prevent to show the splash dialog if the activity is in the process of
        // finishing
        if (cordova.getActivity().isFinishing()) {
            return;
        }
        // If the splash dialog is showing don't try to show it again
        if (splashDialog != null && splashDialog.isShowing()) {
            return;
        }

        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                // Get reference to display
                Display display = cordova.getActivity().getWindowManager().getDefaultDisplay();
                Context context = webView.getContext();
                int splashDim = getSplashDim(display);

                LinearLayout splashContent = createParentContentView(context);

                createLogoImageView(context, splashDim, drawableId, logoUrl);
                createImportStatusView(context);
                TextView appNameTextView = createAppNameView(context, appName);

                splashContent.addView(splashImageView);
                splashContent.addView(appNameTextView);
                splashContent.addView(importStatusTextView);

                // Create and show the dialog
                splashDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
                // check to see if the splash screen should be full screen
                if ((cordova.getActivity().getWindow().getAttributes().flags
                        & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
                    splashDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
                splashDialog.setContentView(splashContent);
                splashDialog.setCancelable(false);
                splashDialog.show();
            }
        });
    }

    private int getSplashDim(Display display) {
        return display.getWidth() < display.getHeight() ? display.getWidth() : display.getHeight();
    }

    @NonNull
    private TextView createAppNameView(Context context, String appName) {
        TextView appNameTextView = new TextView(context);
        LinearLayout.LayoutParams textViewParam = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        textViewParam.setMargins(10, 10, 10, 10);
        appNameTextView.setText(appName);
        appNameTextView.setTextSize(20);
        appNameTextView.setTextColor(Color.GRAY);
        appNameTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        appNameTextView.setLayoutParams(textViewParam);

        setTypeFace(context, appNameTextView);
        return appNameTextView;
    }

    private void setTypeFace(Context context, TextView textView) {
        try {
            String NOTO_COMBINED = "www/assets/fonts/natosans/" + "NotoSans-Regular.ttf";
            Typeface tf = Typeface.createFromAsset(context.getAssets(), NOTO_COMBINED);
            textView.setTypeface(tf);
        } catch (Exception exception) {
            System.out.println(exception);
        }
    }

    @NonNull
    private void createImportStatusView(Context context) {
        importStatusTextView = new TextView(context);
        LinearLayout.LayoutParams textViewParam = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        textViewParam.setMargins(10, 10, 10, 10);
        importStatusTextView.setTextSize(10);
        importStatusTextView.setTextColor(Color.GRAY);
        importStatusTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        importStatusTextView.setLayoutParams(textViewParam);
        setTypeFace(context, importStatusTextView);
    }

    private void createLogoImageView(Context context, int splashDim, int drawableId, String logoUrl) {
        splashImageView = new ImageView(context);
        // splashImageView.setImageResource(drawableId);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(splashDim, splashDim);
        layoutParams.setMargins(10, splashDim / 4, 10, 0);
        splashImageView.setLayoutParams(layoutParams);

        splashImageView.setMinimumHeight(splashDim);
        splashImageView.setMinimumWidth(splashDim);

        // TODO: Use the background color of the webView's parent instead of using the
        // preference.

        splashImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        if (TextUtils.isEmpty(logoUrl)) {
            splashImageView.setImageResource(drawableId);
        } else {
            Glide.with(context).load(logoUrl).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(drawableId).into(splashImageView);
        }
    }

    @NonNull
    private LinearLayout createParentContentView(Context context) {
        LinearLayout splashContent = new LinearLayout(context);
        splashContent.setOrientation(LinearLayout.VERTICAL);
        splashContent.setBackgroundColor(Color.WHITE);
        LayoutParams parentParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        splashContent.setLayoutParams(parentParams);
        return splashContent;
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntentForDeeplinking(intent);
    }

    private void consumeEvents() {
        if (this.mHandler.size() == 0 || mLastEvent == null) {
            return;
        }

        for (CallbackContext callback : this.mHandler) {
            final PluginResult result = new PluginResult(PluginResult.Status.OK, mLastEvent);
            result.setKeepCallback(true);
            callback.sendPluginResult(result);
        }

        mLastEvent = null;
    }

    private void handleIntentForDeeplinking(Intent intent) {
        // get the locale set by user from the mobile
        // localeSelected =
        // GenieService.getService().getKeyStore().getString("sunbirdselected_language_code",
        // "en");
        deepLinkIntent = intent;
        mDeepLinkNavigation.validateAndHandleDeepLink(intent, new DeepLinkNavigation.IValidateDeepLink() {
            @Override
            public void validLocalDeepLink() {

                try {
                    Uri intentUri = intent.getData();

                    JSONObject response = new JSONObject();

                    response.put("type", "contentDetails");

                    if (intentUri != null) {
                        response.putOpt("id", intentUri.getLastPathSegment());
                        for (String key : intentUri.getQueryParameterNames()) {
                            response.putOpt(key, intentUri.getQueryParameter(key));
                        }
                    }

                    mLastEvent = response;
                } catch (JSONException ex) {
                    Log.e("SplashScreen", ex.toString());
                }
            }

            @Override
            public void validServerDeepLink() {
                if (intent.getData() == null) {
                    return;
                }

                String url = intent.getData().toString();
                // String customScheme = DeepLinkUtility.getStringResourceByName(context, "custom_scheme_url")
                // String newString = url.replace("https://", "").replace("http://", "").replace("staging.diksha.app://", "");
                String newString = url.replace("//", "");
                String[] pair = newString.split("/");

                if (pair[1].equalsIgnoreCase("public")) {
                    addDeepLinkAction(url, "content", null);
                } else if (pair[1].equalsIgnoreCase("dial") || pair[1].equalsIgnoreCase("QR")) {
                    addDeepLinkAction(url, "dial", null);
                } else if (pair[1].equalsIgnoreCase("play")
                        && (pair[2].equalsIgnoreCase("collection") || pair[2].equalsIgnoreCase("content"))) {
                    addDeepLinkAction(url, "content", null);
                } else if ((pair[1].equalsIgnoreCase("learn") || pair[1].equalsIgnoreCase("explore-course"))
                        && pair[2].equalsIgnoreCase("course")) {
                    addDeepLinkAction(url, "content", null);
                } else if (pair[2].equalsIgnoreCase("play") && pair[3].equalsIgnoreCase("content")) { // NCERT unlisted
                    addDeepLinkAction(url, "content", pair[4]);
                }
            }

            @Override
            public void notAValidDeepLink() {
                Uri uri = intent.getData();

                if (uri == null) {
                    addImportAction(intent);
                } else if ((cordova.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
                    addImportAction(intent);
                } else {
                    importingInProgress = true;
                    displaySplashScreen();
                    cordova.requestPermission(SplashScreen.this, 100, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

                consumeEvents();
            }

            @Override
            public void onTagDeepLinkFound(String tagName, String description, String startDate, String endDate) {
                consumeEvents();
            }
        });
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults)
            throws JSONException {
        if (requestCode == 100) {
            addImportAction(deepLinkIntent);
            deepLinkIntent = null;
        } else {
            addImportAction(deepLinkIntent);
        }
    }

    private void addImportAction(Intent deepLinkIntent) {
        try {
            String filePath = getFilePathFromIntent(deepLinkIntent);
            if (filePath != null && !filePath.isEmpty()) {
                JSONObject importPayload = new JSONObject();
                importPayload.put("filePath", filePath);

                JSONObject importAction = new JSONObject();
                importAction.put("type", "IMPORT");
                importAction.put("payload", importPayload);

                actions.put(importAction);
            }
        } catch (JSONException e) {

        }
    }

    private void addDeepLinkAction(String url, String type, String identifier) {
        try {
            if (identifier == null || identifier.isEmpty()) {
                identifier = url.substring(url.lastIndexOf('/') + 1, url.length());
            }
            if (identifier != null && !identifier.isEmpty()) {
                JSONObject deeplinkPayload = new JSONObject();
                deeplinkPayload.put("identifier", identifier);
                deeplinkPayload.put("type", type);

                JSONObject deeplinkAction = new JSONObject();
                deeplinkAction.put("type", "DEEPLINK");
                deeplinkAction.put("payload", deeplinkPayload);
                actions.put(deeplinkAction);
            }
        } catch (JSONException e) {

        }
    }

    private String getFilePathFromIntent(Intent intent) {
        Uri uri = intent.getData();

        if (uri == null) {
            return "";
        }

        if (intent.getScheme().equals("content")) {
            return ImportExportUtil.getAttachmentFilePath(cordova.getActivity().getApplicationContext(), uri);
        } else if (intent.getScheme().equals("file")) {
            return uri.getPath();
        } else {
            return "";
        }
    }

}
