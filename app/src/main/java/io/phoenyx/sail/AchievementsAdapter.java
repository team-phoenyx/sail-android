package io.phoenyx.sail;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.AchievementsViewHolder> {

    private List<Achievement> achievements;
    private DBHandler dbHandler;

    public AchievementsAdapter(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    @Override
    public AchievementsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        dbHandler = new DBHandler(parent.getContext());
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.achievement_card, parent, false);
        return new AchievementsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AchievementsViewHolder holder, int position) {
        final Achievement achievement = achievements.get(position);

        String title = achievement.getTitle();
        String description = achievement.getDescription();

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

        holder.descriptionTextView.setText(achievement.getDescription());
        holder.dateTextView.setText(achievement.getDate());
        holder.achievementID = achievement.getId();
        if (achievement.isStarred()) {
            holder.starImageButton.setBackgroundResource(R.drawable.star);
        } else {
            holder.starImageButton.setBackgroundResource(R.drawable.star_outline);
        }
        holder.starImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int beforePosition = holder.getAdapterPosition();

                if (achievement.isStarred()) {
                    holder.starImageButton.setBackgroundResource(R.drawable.star_outline);
                    achievement.setStarred(false);
                } else {
                    holder.starImageButton.setBackgroundResource(R.drawable.star);
                    achievement.setStarred(true);
                }
                dbHandler.updateAchievement(achievement);

                int achievementID = achievement.getId();
                achievements = dbHandler.getAllAchievements();

                int insertPosition = -1;

                for (int i = 0; i < achievements.size(); i++) {
                    if (achievements.get(i).getId() == achievementID) insertPosition = i;
                }

                if (insertPosition != beforePosition) {
                    notifyItemRemoved(beforePosition);
                    notifyItemRangeChanged(beforePosition, getItemCount() - beforePosition);
                    notifyItemInserted(insertPosition);
                }
            }
        });
    }


    public class AchievementsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView titleTextView;
        protected TextView descriptionTextView;
        protected TextView dateTextView;
        protected ImageButton starImageButton;
        int achievementID;

        public AchievementsViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.achievementTitleTextView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.achievementDescriptionTextView);
            dateTextView = (TextView) itemView.findViewById(R.id.goalDateTextView);
            starImageButton = (ImageButton) itemView.findViewById(R.id.achievementStarButton);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent editAchievement = new Intent(view.getContext().getApplicationContext(), EditAchievementActivity.class);
            editAchievement.putExtra("achievement_id", achievementID);
            ((Activity) view.getContext()).startActivityForResult(editAchievement, 1337);
        }
    }


    @Override
    public int getItemCount() {
        return achievements.size();
    }
}
