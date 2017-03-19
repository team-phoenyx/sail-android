package io.phoenyx.sail;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TimelineEventsAdapter extends RecyclerView.Adapter<TimelineEventsAdapter.TimelineEventsViewHolder> {

    private List<TimelineEvent> timelineEvents;
    private DBHandler dbHandler;

    public TimelineEventsAdapter(List<TimelineEvent> timelineEvents) {
        this.timelineEvents = timelineEvents;
    }

    @Override
    public TimelineEventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        dbHandler = new DBHandler(parent.getContext());
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_card, parent, false);
        return new TimelineEventsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TimelineEventsViewHolder holder, int position) {
        final TimelineEvent timelineEvent = timelineEvents.get(position);

        String title = timelineEvent.getTitle();
        String description = timelineEvent.getDescription();

        if (title.length() > 40) {
            title = title.substring(0, 37) + "...";
        }
        if (description.length() > 90) {
            description = description.substring(0, 87) + "...";
        }

        holder.titleTextView.setText(title);

        if (description.equals("") || description.isEmpty()) {
            holder.descriptionTextView.setVisibility(View.GONE);
        } else {
            holder.descriptionTextView.setText(description);
        }

        holder.dateTextView.setText(timelineEvent.getDate());
        holder.timelineEventID = timelineEvent.getId();
    }

    public class TimelineEventsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView titleTextView;
        private TextView descriptionTextView;
        private TextView dateTextView;
        int timelineEventID;

        public TimelineEventsViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.timelineTitleTextView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.timelineDescriptionTextView);
            dateTextView = (TextView) itemView.findViewById(R.id.timelineDateTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent editTimelineEvent = new Intent(view.getContext().getApplicationContext(), EditTimelineEventActivity.class);
            editTimelineEvent.putExtra("timeline_event_id", timelineEventID);
            ((Activity) view.getContext()).startActivityForResult(editTimelineEvent, 1337);
        }
    }


    @Override
    public int getItemCount() {
        return timelineEvents.size();
    }
}
