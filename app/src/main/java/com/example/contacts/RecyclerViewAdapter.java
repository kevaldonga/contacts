package com.example.contacts;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.viewHolder> {

    ArrayList<Contact> contacts = new ArrayList<>();
    ArrayList<Contact> selected_contacts = new ArrayList<>();
    TextView selectedItems,appTitle;
    ImageButton remove_all;
    Context context;

    public RecyclerViewAdapter(Context context, TextView selectedItems, TextView appTitle, ImageButton remove_all) {
        this.context = context;
        this.remove_all = remove_all;
        this.appTitle = appTitle;
        this.selectedItems = selectedItems;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.name.setText(contacts.get(position).getName());
        holder.phone_no.setText(contacts.get(position).getPhone_no());
        appTitle.setVisibility(View.VISIBLE);
        remove_all.setVisibility(View.INVISIBLE);
        selectedItems.setVisibility(View.INVISIBLE);
        Glide.with(context).asBitmap().load(contacts.get(position).getImageUrl()).into(holder.image);
        Log.i("Glide", "Image url for "+contacts.get(position).getName() + " is "+contacts.get(position).getImageUrl());
        holder.parent.setOnClickListener(v -> {
            if (Contact.selection && !contacts.get(position).isSelected()) {
                selected(holder,position);
                return;
            }
            if (contacts.get(position).isSelected()) {
                unselected(holder,position, false);
                return;
            }
            if (contacts.get(position).isExpanded()) {
                contacts.get(position).setExpanded(false);
                holder.expanded.setVisibility(View.GONE);

                Log.i("animation", "item abstracted");
                holder.parent.setBackground(AppCompatResources.getDrawable(context, R.drawable.item_background));
            } else {
                contacts.get(position).setExpanded(true);
                holder.expanded.setVisibility(View.VISIBLE);

                Log.i("animation", "item expanded");
                holder.parent.setBackground(AppCompatResources.getDrawable(context, R.drawable.item_background_expanded));
            }
        });
        remove_all.setOnClickListener(v -> deselectAll(holder));
        holder.parent.setOnLongClickListener(v -> {
            if (!contacts.get(position).isSelected()) {
                selected(holder,position);
            }
            return true;
        });
    }

    private void selected(viewHolder holder, int position) {
        selected_contacts.add(contacts.get(position));
        contacts.get(holder.getAdapterPosition()).setSelected(true);
        holder.parent.setBackground(AppCompatResources.getDrawable(context, R.drawable.selected_item_background));
        Drawable drawable = AppCompatResources.getDrawable(context, R.drawable.item_selected);
        holder.image2.setBackground(drawable);
        AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) drawable;

        Contact.selection = true;
        if (Contact.selectedItems == 0) {
            Log.i("items", "selection of items is true");
        }

        Contact.selectedItems++;
        Log.i("items", "numbers of selected items are updated to " + Contact.selectedItems);

        String top_text = Contact.selectedItems + " Contacts selected";
        selectedItems.setText(top_text);
        if(Contact.selectedItems >= 0) {
            appTitle.setVisibility(View.INVISIBLE);
            remove_all.setVisibility(View.VISIBLE);
            selectedItems.setVisibility(View.VISIBLE);
        }
        animatedVectorDrawable.start();

        Log.i("animation", "animation of item no " + position + " is selected");
    }

    private void unselected(viewHolder holder, int position, boolean b) {
        if(!b){
        selected_contacts.remove(contacts.get(position));
        }
        contacts.get(position).setSelected(false);
        if (contacts.get(position).isExpanded()) {
            holder.parent.setBackground(AppCompatResources.getDrawable(context, R.drawable.item_background_expanded));
        } else {
            holder.parent.setBackground(AppCompatResources.getDrawable(context, R.drawable.item_background));
        }
        AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) AppCompatResources.getDrawable(context, R.drawable.item_unselected);
        holder.image2.setBackground(animatedVectorDrawable);
        animatedVectorDrawable.start();

        Contact.selectedItems--;
        Log.i("items", "numbers of selected items are updated to " + Contact.selectedItems);

        String top_text = Contact.selectedItems + " Contacts selected";
        selectedItems.setText(top_text);

        if (Contact.selectedItems == 0) {
            Contact.selection = false;
            selectedItems.setVisibility(View.INVISIBLE);
            remove_all.setVisibility(View.INVISIBLE);
            appTitle.setVisibility(View.VISIBLE);
            Log.i("items", "selection of items is false");
        }

        Log.i("animation", "animation of item no " + position + " is unselected");
    }
    private void deselectAll(viewHolder holder) {
        for(int i = 0; i < selected_contacts.size(); i++){
            unselected(holder, contacts.indexOf(selected_contacts.get(i)),true);
        }
        selected_contacts.clear();
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView phone_no;
        private final CircleImageView image, image2;
        private final ConstraintLayout expanded, parent;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            phone_no = itemView.findViewById(R.id.phone_no);
            image = itemView.findViewById(R.id.image);
            image2 = itemView.findViewById(R.id.image2);
            expanded = itemView.findViewById(R.id.expanded_constraint_layout);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}
