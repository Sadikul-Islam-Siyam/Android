package com.siyam.travelschedulemanager.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.model.UnifiedRoute;

public class RouteAdapter extends ListAdapter<UnifiedRoute, RouteAdapter.RouteViewHolder> {

    private final OnRouteActionListener listener;

    public interface OnRouteActionListener {
        void onRouteClick(UnifiedRoute route);
        void onEditClick(UnifiedRoute route);
        void onDeleteClick(UnifiedRoute route);
    }

    public RouteAdapter(OnRouteActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<UnifiedRoute> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<UnifiedRoute>() {
                @Override
                public boolean areItemsTheSame(@NonNull UnifiedRoute oldItem, @NonNull UnifiedRoute newItem) {
                    return oldItem.getId() != null && oldItem.getId().equals(newItem.getId())
                            && oldItem.getRouteType().equals(newItem.getRouteType());
                }

                @Override
                public boolean areContentsTheSame(@NonNull UnifiedRoute oldItem, @NonNull UnifiedRoute newItem) {
                    return oldItem.getDisplayName() != null && oldItem.getDisplayName().equals(newItem.getDisplayName())
                            && oldItem.getOrigin() != null && oldItem.getOrigin().equals(newItem.getOrigin())
                            && oldItem.getDestination() != null && oldItem.getDestination().equals(newItem.getDestination())
                            && oldItem.getStatus() != null && oldItem.getStatus().equals(newItem.getStatus());
                }
            };

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route_card, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        UnifiedRoute route = getItem(position);
        holder.bind(route, listener);
    }

    static class RouteViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView routeTypeBadge;
        private final TextView routeName;
        private final TextView statusBadge;
        private final TextView routeNumber;
        private final TextView originStation;
        private final TextView originDistrict;
        private final TextView destinationStation;
        private final TextView destinationDistrict;
        private final TextView departureTime;
        private final TextView duration;
        private final TextView fare;
        private final MaterialButton btnEdit;
        private final MaterialButton btnDelete;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            routeTypeBadge = itemView.findViewById(R.id.routeTypeBadge);
            routeName = itemView.findViewById(R.id.routeName);
            statusBadge = itemView.findViewById(R.id.statusBadge);
            routeNumber = itemView.findViewById(R.id.routeNumber);
            originStation = itemView.findViewById(R.id.originStation);
            originDistrict = itemView.findViewById(R.id.originDistrict);
            destinationStation = itemView.findViewById(R.id.destinationStation);
            destinationDistrict = itemView.findViewById(R.id.destinationDistrict);
            departureTime = itemView.findViewById(R.id.departureTime);
            duration = itemView.findViewById(R.id.duration);
            fare = itemView.findViewById(R.id.fare);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(UnifiedRoute route, OnRouteActionListener listener) {
            // Set route type badge
            if (route.isBus()) {
                routeTypeBadge.setText("🚌 BUS");
                routeTypeBadge.setBackgroundResource(R.drawable.badge_background);
            } else {
                routeTypeBadge.setText("🚂 TRAIN");
                routeTypeBadge.setBackgroundResource(R.drawable.badge_background);
            }

            // Set route name
            routeName.setText(route.getDisplayName() != null ? route.getDisplayName() : "Unknown Route");

            // Set status badge
            String status = route.getStatus();
            if ("APPROVED".equals(status)) {
                statusBadge.setText("APPROVED");
                statusBadge.setBackgroundResource(R.drawable.badge_background_success);
                statusBadge.setVisibility(View.VISIBLE);
            } else if ("PENDING".equals(status)) {
                statusBadge.setText("PENDING");
                statusBadge.setBackgroundResource(R.drawable.badge_background_pending);
                statusBadge.setVisibility(View.VISIBLE);
            } else if ("DRAFT".equals(status)) {
                statusBadge.setText("DRAFT");
                statusBadge.setBackgroundResource(R.drawable.badge_background);
                statusBadge.setVisibility(View.VISIBLE);
            } else {
                statusBadge.setVisibility(View.GONE);
            }

            // Set route number
            String routeNum = route.getRouteNumber();
            if (routeNum != null && !routeNum.isEmpty()) {
                routeNumber.setText("Route #: " + routeNum);
                routeNumber.setVisibility(View.VISIBLE);
            } else {
                routeNumber.setVisibility(View.GONE);
            }

            // Set origin
            originStation.setText(route.getOrigin() != null ? route.getOrigin() : "-");
            originDistrict.setVisibility(View.GONE);

            // Set destination
            destinationStation.setText(route.getDestination() != null ? route.getDestination() : "-");
            destinationDistrict.setVisibility(View.GONE);

            // Set departure time
            departureTime.setText(route.getDepartureTime() != null ? route.getDepartureTime() : "-");

            // Set duration (now stored as string like "4:30h")
            String durationStr = route.getDuration();
            if (durationStr != null && !durationStr.isEmpty()) {
                duration.setText(durationStr);
            } else {
                duration.setText("-");
            }

            // Set fare
            double fareValue = route.getFare();
            if (fareValue > 0) {
                fare.setText("৳ " + String.format("%.0f", fareValue));
            } else {
                fare.setText("-");
            }

            // Set click listeners
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRouteClick(route);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(route);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(route);
                }
            });
        }
    }
}
