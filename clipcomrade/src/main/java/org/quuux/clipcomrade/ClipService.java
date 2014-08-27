package org.quuux.clipcomrade;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClipService extends Service implements ClipboardManager.OnPrimaryClipChangedListener {

    private static final String TAG = Log.buildTag(ClipService.class);
    public static final String EXTRAS_MATCHES = "matches";
    private ClipboardManager mClipboardManager;

    private static final ClipMatcher[] CLIP_MATCHERS = new ClipMatcher[] {
            new ClipMatcher(ClipMatcher.MatchType.PHONE, Patterns.PHONE),
            new ClipMatcher(ClipMatcher.MatchType.WEB, Patterns.WEB_URL),
            new ClipMatcher(ClipMatcher.MatchType.EMAIL, Patterns.EMAIL_ADDRESS),
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(this);
        Log.d(TAG, "service started...");
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public void onPrimaryClipChanged() {
        ClipData clip = mClipboardManager.getPrimaryClip();
        final List<ClipMatcher.Match> matches = match(clip);
        if (matches.size() > 0) {
            final Notification notification = buildNotification(matches);

            final NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(1, notification);
        }
    }

    private Notification buildNotification(final List<ClipMatcher.Match> matches) {

        final String summary = getString(R.string.matches_summary, matches.size());

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_matches_found)
                        .setContentTitle(getString(R.string.matches_found))
                        .setContentText(summary)
                        .setTicker(summary)
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(this, ClipActivity.class);
        resultIntent.putExtra(EXTRAS_MATCHES, (ArrayList<ClipMatcher.Match>) matches);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();

        for (ClipMatcher.Match match : matches)
        inboxStyle.addLine(String.format("%s: %s", match.type.toString().toLowerCase(), match.text));

        builder.setStyle(inboxStyle);

        return builder.build();
    }

    public static void start(final Context context) {
        context.startService(new Intent(context, ClipService.class));
    }

    private ArrayList<ClipMatcher.Match> match(final ClipData clip) {

        final Set<ClipMatcher.Match> matches = new HashSet<ClipMatcher.Match>();

        for (int i = 0; i < clip.getItemCount(); i++) {
            for (int j=0; j< CLIP_MATCHERS.length; j++) {
                if (CLIP_MATCHERS[j].match(clip.getItemAt(i).coerceToText(this))) {
                    matches.addAll(CLIP_MATCHERS[j].getMatches());
                }
            }

        }

        return new ArrayList<ClipMatcher.Match>(matches);
    }



}
