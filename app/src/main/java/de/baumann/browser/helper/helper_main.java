/*
    This file is part of the Browser WebApp.

    Browser WebApp is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Browser WebApp is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Browser webview app.

    If not, see <http://www.gnu.org/licenses/>.
 */

package de.baumann.browser.helper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mobapphome.mahencryptorlib.MAHEncryptor;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.baumann.browser.R;

public class helper_main {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static final int REQUEST_CODE_ASK_PERMISSIONS_1 = 1234;
    private static String pw;
    private static String protect;

    public static void grantPermissionsStorage(final Activity from) {

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(from);

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (sharedPref.getBoolean ("perm_notShow", false)){
                int hasWRITE_EXTERNAL_STORAGE = from.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                    if (!from.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        new AlertDialog.Builder(from)
                                .setTitle(R.string.app_permissions_title)
                                .setMessage(helper_main.textSpannable(from.getString(R.string.app_permissions)))
                                .setNeutralButton(R.string.toast_notAgain, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        sharedPref.edit()
                                                .putBoolean("perm_notShow", false)
                                                .apply();
                                    }
                                })
                                .setPositiveButton(from.getString(R.string.toast_yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (android.os.Build.VERSION.SDK_INT >= 23)
                                            from.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                    REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                })
                                .setNegativeButton(from.getString(R.string.toast_cancel), null)
                                .show();
                        return;
                    }
                    from.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_PERMISSIONS);
                }
            }
        }
    }

    public static void grantPermissionsLoc(final Activity from) {

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(from);

        if (android.os.Build.VERSION.SDK_INT >= 23) {

            if (sharedPref.getBoolean ("perm_notShow", false)){
                int hasACCESS_FINE_LOCATION = from.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                if (hasACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
                    if (!from.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        new AlertDialog.Builder(from)
                                .setTitle(R.string.app_permissions_title)
                                .setMessage(helper_main.textSpannable(from.getString(R.string.app_permissions)))
                                .setNeutralButton(R.string.toast_notAgain, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        sharedPref.edit()
                                                .putBoolean("perm_notShow", false)
                                                .apply();
                                    }
                                })
                                .setPositiveButton(from.getString(R.string.toast_yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (android.os.Build.VERSION.SDK_INT >= 23)
                                            from.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                    REQUEST_CODE_ASK_PERMISSIONS_1);
                                    }
                                })
                                .setNegativeButton(from.getString(R.string.toast_cancel), null)
                                .show();
                        return;
                    }
                    from.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_ASK_PERMISSIONS_1);
                }
            }
        }
    }

    public static String createDate () {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return  format.format(date);
    }

    static String createDateSecond () {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return  format.format(date);
    }

    public static void switchToActivity(Activity from, Class to, String Extra, boolean finishFromActivity) {
        Intent intent = new Intent(from, to);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("URL", Extra);
        from.startActivity(intent);
        if (finishFromActivity) {
            from.finish();
        }
    }

    public static void closeApp (Activity from, Class to, WebView webView) {
        helper_webView.closeWebView(from, webView);
        Intent intent = new Intent(from, to);
        intent.setAction("closeAPP");
        from.startActivity(intent);
        from.finishAffinity();
    }

    public static void onStart (final Activity from) {

        PreferenceManager.setDefaultValues(from, R.xml.user_settings, false);
        PreferenceManager.setDefaultValues(from, R.xml.user_settings_search, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(from);

        if (sharedPref.getString ("fullscreen", "2").equals("1") || sharedPref.getString ("fullscreen", "2").equals("3")){
            from.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        if (sharedPref.getString("orientation", "auto").equals("landscape")) {
            from.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (sharedPref.getString("orientation", "auto").equals("portrait")) {
            from.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public static void checkPin (final Activity from) {

        PreferenceManager.setDefaultValues(from, R.xml.user_settings, false);
        PreferenceManager.setDefaultValues(from, R.xml.user_settings_search, false);
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(from);

        if (sharedPref.getString("protect_PW", "").length() > 0) {
            try {
                final MAHEncryptor mahEncryptor = MAHEncryptor.newInstance(sharedPref.getString("saved_key", ""));
                pw = mahEncryptor.decode(sharedPref.getString("protect_PW", ""));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(from, R.string.toast_error, Toast.LENGTH_SHORT).show();
            }
        }

        if (pw != null  && pw.length() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(from, R.style.YourStyle);
            final View dialogView = View.inflate(from, R.layout.dialog_password, null);

            final TextView text = (TextView) dialogView.findViewById(R.id.pass_userPin);

            Button ib0 = (Button) dialogView.findViewById(R.id.button0);
            assert ib0 != null;
            ib0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "0");
                }
            });

            Button ib1 = (Button) dialogView.findViewById(R.id.button1);
            assert ib1 != null;
            ib1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "1");
                }
            });

            Button ib2 = (Button) dialogView.findViewById(R.id.button2);
            assert ib2 != null;
            ib2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "2");
                }
            });

            Button ib3 = (Button) dialogView.findViewById(R.id.button3);
            assert ib3 != null;
            ib3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "3");
                }
            });

            Button ib4 = (Button) dialogView.findViewById(R.id.button4);
            assert ib4 != null;
            ib4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "4");
                }
            });

            Button ib5 = (Button) dialogView.findViewById(R.id.button5);
            assert ib5 != null;
            ib5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "5");
                }
            });

            Button ib6 = (Button) dialogView.findViewById(R.id.button6);
            assert ib6 != null;
            ib6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "6");
                }
            });

            Button ib7 = (Button) dialogView.findViewById(R.id.button7);
            assert ib7 != null;
            ib7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "7");
                }
            });

            Button ib8 = (Button) dialogView.findViewById(R.id.button8);
            assert ib8 != null;
            ib8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "8");
                }
            });

            Button ib9 = (Button) dialogView.findViewById(R.id.button9);
            assert ib9 != null;
            ib9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "9");
                }
            });

            try {
                final MAHEncryptor mahEncryptor = MAHEncryptor.newInstance(sharedPref.getString("saved_key", ""));
                protect = mahEncryptor.decode(sharedPref.getString("protect_PW", ""));
            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(ib0, R.string.toast_error, Snackbar.LENGTH_SHORT).show();
            }


            ImageButton enter = (ImageButton) dialogView.findViewById(R.id.imageButtonEnter);
            assert enter != null;


            final ImageButton cancel = (ImageButton) dialogView.findViewById(R.id.imageButtonCancel);
            assert cancel != null;
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text.setText("");
                }
            });

            final Button clear = (Button) dialogView.findViewById(R.id.buttonReset);
            assert clear != null;
            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar snackbar = Snackbar
                            .make(clear, from.getString(R.string.pw_forgotten_dialog), Snackbar.LENGTH_LONG)
                            .setAction(from.getString(R.string.toast_yes), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        // clearing app data
                                        Runtime runtime = Runtime.getRuntime();
                                        runtime.exec("pm clear de.baumann.browser");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    snackbar.show();
                }
            });

            builder.setView(dialogView);
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    from.finishAffinity();
                }
            });

            final AlertDialog dialog = builder.create();
            // Display the custom alert dialog on interface
            dialog.show();

            enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Password = text.getText().toString().trim();

                    if (Password.equals(protect)) {
                        sharedPref.edit().putBoolean("isOpened", false).apply();
                        dialog.dismiss();
                    } else {
                        Snackbar.make(text, R.string.toast_wrongPW, Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
        if (sharedPref.getString ("fullscreen", "2").equals("1") || sharedPref.getString ("fullscreen", "2").equals("3")){
            from.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        if (sharedPref.getString("orientation", "auto").equals("landscape")) {
            from.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (sharedPref.getString("orientation", "auto").equals("portrait")) {
            from.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private static void enterNum (View view, String number) {
        TextView text = (TextView) view.findViewById(R.id.pass_userPin);
        String textNow = text.getText().toString().trim();
        String pin = textNow + number;
        text.setText(pin);
    }

    public static SpannableString textSpannable (String text) {
        SpannableString s;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            s = new SpannableString(Html.fromHtml(text,Html.FROM_HTML_MODE_LEGACY));
        } else {
            //noinspection deprecation
            s = new SpannableString(Html.fromHtml(text));
        }
        Linkify.addLinks(s, Linkify.WEB_URLS);
        return s;
    }

    public static File newFile (WebView webView) {
        final  String domain;

        if(Uri.parse(webView.getUrl()).getHost().length() == 0) {
            domain = "";
        } else {
            domain = Uri.parse(webView.getUrl()).getHost();
        }

        String shortDomain = domain.substring(0, Math.min(domain.length(), 20));
        String title = shortDomain.replaceAll("\\P{L}+", "-");

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        String filename = dateFormat.format(date) + "_" + title + ".jpg";
        return  new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + filename);
    }

    public static String newFileName () {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        return  dateFormat.format(date) + ".jpg";
    }

    public static void open (String extension, Activity activity, File pathFile, View view) {
        File file = new File(pathFile.getAbsolutePath());
        final String fileExtension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
        String text = activity.getString(R.string.toast_extension) + ": " + fileExtension;
        switch (extension) {
            case ".gif":
            case ".bmp":
            case ".tiff":
            case ".svg":
            case ".png":
            case ".jpg":
            case ".JPG":
            case ".jpeg":
                helper_main.openFile(activity, pathFile, "image/*", view);
                break;
            case ".m3u8":
            case ".mp3":
            case ".wma":
            case ".midi":
            case ".wav":
            case ".aac":
            case ".aif":
            case ".amp3":
            case ".weba":
                helper_main.openFile(activity, pathFile, "audio/*", view);
                break;
            case ".mpeg":
            case ".mp4":
            case ".ogg":
            case ".webm":
            case ".qt":
            case ".3gp":
            case ".3g2":
            case ".avi":
            case ".f4v":
            case ".flv":
            case ".h261":
            case ".h263":
            case ".h264":
            case ".asf":
            case ".wmv":
                helper_main.openFile(activity, pathFile, "video/*", view);
                break;
            case ".rtx":
            case ".csv":
            case ".txt":
            case ".vcs":
            case ".vcf":
            case ".css":
            case ".ics":
            case ".conf":
            case ".config":
            case ".java":
                helper_main.openFile(activity, pathFile, "text/*", view);
                break;
            case ".html":
                helper_main.openFile(activity, pathFile, "text/html", view);
                break;
            case ".apk":
                helper_main.openFile(activity, pathFile, "application/vnd.android.package-archive", view);
                break;
            case ".pdf":
                helper_main.openFile(activity, pathFile, "application/pdf", view);
                break;
            case ".doc":
                helper_main.openFile(activity, pathFile, "application/msword", view);
                break;
            case ".xls":
                helper_main.openFile(activity, pathFile, "application/vnd.ms-excel", view);
                break;
            case ".ppt":
                helper_main.openFile(activity, pathFile, "application/vnd.ms-powerpoint", view);
                break;
            case ".docx":
                helper_main.openFile(activity, pathFile, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", view);
                break;
            case ".pptx":
                helper_main.openFile(activity, pathFile, "application/vnd.openxmlformats-officedocument.presentationml.presentation", view);
                break;
            case ".xlsx":
                helper_main.openFile(activity, pathFile, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", view);
                break;
            case ".odt":
                helper_main.openFile(activity, pathFile, "application/vnd.oasis.opendocument.text", view);
                break;
            case ".ods":
                helper_main.openFile(activity, pathFile, "application/vnd.oasis.opendocument.spreadsheet", view);
                break;
            case ".odp":
                helper_main.openFile(activity, pathFile, "application/vnd.oasis.opendocument.presentation", view);
                break;
            case ".zip":
                helper_main.openFile(activity, pathFile, "application/zip", view);
                break;
            case ".rar":
                helper_main.openFile(activity, pathFile, "application/x-rar-compressed", view);
                break;
            case ".epub":
                helper_main.openFile(activity, pathFile, "application/epub+zip", view);
                break;
            case ".cbz":
                helper_main.openFile(activity, pathFile, "application/x-cbz", view);
                break;
            case ".cbr":
                helper_main.openFile(activity, pathFile, "application/x-cbr", view);
                break;
            case ".fb2":
                helper_main.openFile(activity, pathFile, "application/x-fb2", view);
                break;
            case ".rtf":
                helper_main.openFile(activity, pathFile, "application/rtf", view);
                break;
            case ".opml":
                helper_main.openFile(activity, pathFile, "application/opml", view);
                break;

            default:
                Snackbar snackbar = Snackbar
                        .make(view, text, Snackbar.LENGTH_LONG);
                snackbar.show();

                break;
        }
    }

    private static void openFile(Activity activity, File file, String string, View view) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", file);
            intent.setDataAndType(contentUri,string);

        } else {
            intent.setDataAndType(Uri.fromFile(file),string);
        }

        try {
            activity.startActivity (intent);
        } catch (ActivityNotFoundException e) {
            Snackbar.make(view, R.string.toast_install_app, Snackbar.LENGTH_LONG).show();
        }
    }
}
