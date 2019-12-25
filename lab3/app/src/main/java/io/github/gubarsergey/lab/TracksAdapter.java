package io.github.gubarsergey.lab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

@FunctionalInterface
interface ItemClickListener<T> {
    void onClick(T item, int position);
}

public class TracksAdapter extends RecyclerView.Adapter<TracksAdapter.TrackViewHolder> {

    private List<Track> tracks;
    private ItemClickListener<Track> itemClickListener;

    public TracksAdapter(List<Track> tracks, ItemClickListener<Track> itemClickListener) {
        this.tracks = tracks;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrackViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        holder.bind(tracks.get(position));
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {

        private TextView trackNameTextView;
        private TextView trackDurationTextView;
        private TextView trackBandNameTextView;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            trackDurationTextView = itemView.findViewById(R.id.item_track_duration);
            trackNameTextView = itemView.findViewById(R.id.item_track_track_name);
            trackBandNameTextView = itemView.findViewById(R.id.item_track_band_name);
        }

        void bind(final Track track) {
            itemView.setOnClickListener(view -> itemClickListener.onClick(track, getAdapterPosition()));
            trackNameTextView.setText(track.name);
            trackBandNameTextView.setText(track.author);
            trackDurationTextView.setText(Integer.toString(track.totalTime));
        }
    }
}
