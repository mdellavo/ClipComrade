package org.quuux.clipcomrade;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class ClipActivity extends ListActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ClipService.start(this);

        final Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(ClipService.EXTRAS_MATCHES)) {
            finish();
            return;
        }

        final List<ClipMatcher.Match> matches = (List<ClipMatcher.Match>) intent.getSerializableExtra(ClipService.EXTRAS_MATCHES);
        final List<ClipLauncher.Launcher> launchers = ClipLauncher.query(this, matches);
        final Adapter adapter = new Adapter(this, launchers);
        setListAdapter(adapter);
    }

    static class Holder {
        TextView text;
        ImageView icon;
    }

    class Adapter extends ArrayAdapter<ClipLauncher.Launcher> {
        Adapter(final Context context, final List<ClipLauncher.Launcher> launchers) {
            super(context, 0);
            addAll(launchers);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final View rv = convertView == null ? newView(parent) : convertView;
            bindView(rv, position);
            return rv;
        }
        private View newView(final ViewGroup parent) {
            final View view = getLayoutInflater().inflate(R.layout.match_item, parent, false);
            final Holder holder = new Holder();
            holder.text = (TextView) view.findViewById(R.id.text);
            holder.icon = (ImageView) view.findViewById(R.id.icon);
            view.setTag(holder);
            return view;
        }

        private void bindView(final View view, final int position) {
            final Holder holder = (Holder) view.getTag();
            final ClipLauncher.Launcher launcher = getItem(position);
            holder.text.setText(launcher.getText());
            holder.icon.setImageDrawable(launcher.getIcon());
        }

    }

}
