package org.quuux.clipcomrade;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class ClipLauncher {

    interface Launcher {
        Drawable getIcon();
        CharSequence getText();
    }

    static class InfoLauncher implements Launcher {

        private final Drawable icon;
        private final CharSequence label;
        private final CharSequence match;

        InfoLauncher(final CharSequence match, final PackageManager packageManager, final ResolveInfo info) {
            this.match = match;
            this.icon = info.loadIcon(packageManager);
            this.label = info.loadLabel(packageManager);
        }

        @Override
        public Drawable getIcon() {
            return icon;
        }

        @Override
        public CharSequence getText() {
            return String.format("Open %s with %s", this.match, this.label);
        }
    }

    public static List<Launcher> query(final Context context, final List<ClipMatcher.Match> matches) {

        final List<Launcher> rv = new ArrayList<Launcher>();
        final PackageManager packageManager = context.getPackageManager();
        for (final ClipMatcher.Match match : matches) {

            Intent intent = null;

            switch (match.type) {
                case PHONE:
                    intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + match.text.toString()));
                    break;

                case WEB:
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(match.text.toString()));
                    break;

                case EMAIL:
                    intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", match.text.toString(), null));
                    break;
            }

            if (intent != null) {
                for (ResolveInfo activity : packageManager.queryIntentActivities(intent, 0)) {
                    rv.add(new InfoLauncher(match.text, packageManager, activity));
                }
            }

        }

        return rv;
    }
}
