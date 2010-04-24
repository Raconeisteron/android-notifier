package org.damazio.notifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import android.text.ClipboardManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Shows the user a form to report a bug, giving him easy access to the
 * application's log.
 *
 * @author rdamazio
 */
public class BugReporter {
  /** Pattern to extract the tag */
  private static final Pattern TAG_PATTERN = Pattern.compile("[VDIWE]/([^(]+).*");

  // Patterns to match the relevant tags
  private static final String SYSTEM_TAG = "AndroidRuntime";
  private static final String BLUETOOTH_TAG_REGEX = "Bluetooth.*";
  private static final String WIFI_TAG_REGEX = "Wifi.*";
  private static final String CALL_TAG_REGEX = ".*Call.*";
  private static final String PHONE_TAG_REGEX = "Phone.*";
  private static final String[] FILTER_TAGS = {
      SYSTEM_TAG, BLUETOOTH_TAG_REGEX, WIFI_TAG_REGEX, CALL_TAG_REGEX, PHONE_TAG_REGEX,
      NotifierConstants.LOG_TAG };

  // URL for reporting the issue
  // TODO(rdamazio): Use the project hosting GData api instead (how to get login?)
  private static final String ISSUE_URI_SCHEME = "http";
  private static final String ISSUE_URI_HOST = "code.google.com";
  private static final String ISSUE_URI_PATH = "/p/android-notifier/issues/entry";
  private static final String ISSUE_PHONE_TEMPLATE = "Defect report from phone";
  private static final String ISSUE_URI_TEMPLATE_PARAM = "template";

  public static void reportBug(Context context) {
    try {
      // Copy the log to the clipboard - it's likely too large to put in the URL
      String log = readLog(FILTER_TAGS);
      Log.d(NotifierConstants.LOG_TAG, "Read log");

      ClipboardManager clipboard =
          (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
      clipboard.setText(log);

      Toast.makeText(context, R.string.report_bug_toast, Toast.LENGTH_LONG).show();
    } catch (IOException e) {
      Log.e(NotifierConstants.LOG_TAG, "Unable to read logs", e);
    }

    // Now open the bug report page
    Builder uriBuilder = new Uri.Builder();
    uriBuilder.scheme(ISSUE_URI_SCHEME);
    uriBuilder.authority(ISSUE_URI_HOST);
    uriBuilder.path(ISSUE_URI_PATH);
    uriBuilder.appendQueryParameter(ISSUE_URI_TEMPLATE_PARAM, ISSUE_PHONE_TEMPLATE);
    context.startActivity(new Intent(Intent.ACTION_VIEW, uriBuilder.build()));
  }

  private static String readLog(String... tags) throws IOException {
    // Run logcat
    String[] args = new String[] { "logcat", "-d" };
    Process process = Runtime.getRuntime().exec(args);
    BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(process.getInputStream()));

    // Get its output
    StringBuilder log = new StringBuilder();
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      if (tags.length > 0) {
        Matcher tagMatcher = TAG_PATTERN.matcher(line);
        if (!tagMatcher.matches()) {
          // Logging here could cause an infinite logging loop
          continue;
        }
  
        // For each line, see if the tag matches what we're looking for
        String tagName = tagMatcher.group(1);
        for (String tag : tags) {
          if (tagName.matches(tag)) {
            log.append(line);
            log.append("\n");
            break;
          }
        }
      } else {
        log.append(line);
        log.append("\n");
      }
    }
    return log.toString();
  }
}
