package io.phoenyx.sail;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class PromisesAdapter extends RecyclerView.Adapter<PromisesAdapter.PromisesViewHolder> {

    private List<Promise> promises;
    private DBHandler dbHandler;

    public PromisesAdapter(List<Promise> promises) {
        this.promises = promises;
    }

    @Override
    public PromisesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        dbHandler = new DBHandler(parent.getContext());
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.promise_card, parent, false);
        return new PromisesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PromisesViewHolder holder, int position) {
        final Promise promise = promises.get(position);

        String title = promise.getTitle();
        String description = promise.getDescription();
        String name = promise.getPerson();

        if (title.length() > 40) {
            title = title.substring(0, 37) + "...";
        }
        if (description.length() > 90) {
            description = description.substring(0, 87) + "...";
        }
        if (name.length() > 20) {
            name = shortenName(name);
        }

        holder.titleTextView.setText(title);

        if (description.equals("") || description.isEmpty()) {
            holder.descriptionTextView.setVisibility(View.GONE);
        } else {
            holder.descriptionTextView.setText(description);
        }

        if (name.equals("") || name.isEmpty()) {
            holder.personTextView.setVisibility(View.GONE);
        } else {
            holder.personTextView.setText(name);
        }

        holder.dateTextView.setText(promise.getDate());
        holder.promiseID = promise.getId();
        holder.doneImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promise.setCompleted(true);
                dbHandler.updatePromise(promise);
            }
        });
        if (promise.isStarred()) {
            holder.starImageButton.setBackgroundResource(R.drawable.star);
        } else {
            holder.starImageButton.setBackgroundResource(R.drawable.star_outline);
        }
        holder.starImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int beforePosition = holder.getAdapterPosition();

                if (promise.isStarred()) {
                    holder.starImageButton.setBackgroundResource(R.drawable.star_outline);
                    promise.setStarred(false);
                } else {
                    holder.starImageButton.setBackgroundResource(R.drawable.star);
                    promise.setStarred(true);
                }
                dbHandler.updatePromise(promise);

                int promiseID = promise.getId();
                promises = dbHandler.getAllPromises();

                int insertPosition = -1;
                for (int i = 0; i < promises.size(); i++) {
                    if (promises.get(i).getId() == promiseID) insertPosition = i;
                }

                if (insertPosition != beforePosition) {
                    notifyItemRemoved(beforePosition);
                    notifyItemRangeChanged(beforePosition, getItemCount() - beforePosition);
                    notifyItemInserted(insertPosition);
                }

            }
        });
        holder.doneImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] months = new String[]{"Jan.","Feb.","Mar.","Apr.","May","Jun.","Jul.","Aug.","Sep.","Oct.","Nov.","Dec."};
                String date = months[Calendar.getInstance().get(Calendar.MONTH)] + " " + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + " " + Calendar.getInstance().get(Calendar.YEAR);
                Achievement achievement = new Achievement(promise.getTitle(), promise.getDescription(), date, promise.isStarred());
                dbHandler.createAchievement(achievement);
                dbHandler.deletePromise(promise.getId());
                promises.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount() - holder.getAdapterPosition());
            }
        });
    }

    public class PromisesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView titleTextView;
        private TextView descriptionTextView;
        private TextView dateTextView;
        private TextView personTextView;
        private ImageButton starImageButton;
        private ImageButton doneImageButton;
        int promiseID;

        public PromisesViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.promiseTitleTextView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.promiseDescriptionTextView);
            dateTextView = (TextView) itemView.findViewById(R.id.goalDateTextView);
            personTextView = (TextView) itemView.findViewById(R.id.promisePersonTextView);
            starImageButton = (ImageButton) itemView.findViewById(R.id.promiseStarButton);
            doneImageButton = (ImageButton) itemView.findViewById(R.id.promiseDoneButton);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent editPromise = new Intent(view.getContext().getApplicationContext(), EditPromiseActivity.class);
            editPromise.putExtra("promise_id", promiseID);
            view.getContext().startActivity(editPromise);
        }
    }


    @Override
    public int getItemCount() {
        return promises.size();
    }

    private String shortenName(String name) {
        if (name.contains(" ")) {
            String[] names = name.split(" ");
            String aggregateName = "";
            int i = -1;
            do {
                i++;
                aggregateName += names[i] + " ";
            } while (aggregateName.length() <= 20);
            return aggregateName.substring(0, 17) + "...";
        } else {
            return name.substring(0, 17) + "...";
        }
    }
}
