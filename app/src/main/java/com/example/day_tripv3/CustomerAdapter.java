package com.example.day_tripv3;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class CustomerAdapter extends FirestoreRecyclerAdapter<Customer, CustomerAdapter.CostumerHolder>{
    public CustomerAdapter(@NonNull FirestoreRecyclerOptions<Customer> options) {
        super(options);
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private OnItemClickListenerDelete mListener;

    private TextInputEditText updateInfoName;
    private TextInputEditText updateInfoHotel;
    private TextInputEditText updateInfoSurname;
    private TextInputEditText updateInfoPhone;
    private String Trip, ID;

    private Button updateInfoButton;

    @Override
    //Here we write the data from the database to each card!!
    protected void onBindViewHolder(@NonNull CostumerHolder holder, int position, @NonNull Customer model) {

        holder.textViewName.setText(model.getName());
        holder.textViewSurname.setText(model.getSirname());
        holder.textViewHotel.setText(model.getHotel());
        holder.textViewPhone.setText(model.getPhone());

        holder.textViewPhone.setText(model.getPhone());

        holder.update_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetDialog dialog = new BottomSheetDialog(holder.textViewName.getContext());
                dialog.setContentView(R.layout.update_dialog);
                dialog.setCanceledOnTouchOutside(true);

                    //INSTEAD OF VIEW, USE DIALOG.
                updateInfoName = dialog.findViewById(R.id.update_info_name_id);
                updateInfoSurname = dialog.findViewById(R.id.update_info_surname_id);
                updateInfoHotel = dialog.findViewById(R.id.update_info_hotel_id);
                updateInfoPhone = dialog.findViewById(R.id.update_info_phone_id);
                updateInfoButton = dialog.findViewById(R.id.update_info_button);

                //THE ORDER OF THESE SHOUD BE THE SAME AS THE CONSTRUCTOR OF CUSTOMER
                updateInfoName.setText(model.getName());
                updateInfoSurname.setText(model.getSirname());
                updateInfoHotel.setText(model.getHotel());
                updateInfoPhone.setText(model.getPhone());
                ID = model.getId();
                Trip = model.getTrip();

                dialog.show();

                //TODO remove
                Toast.makeText(holder.textViewName.getContext(), "ID: "+ID+"  "+"Trip: "+Trip, Toast.LENGTH_LONG).show();

                updateInfoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Customer costumer = new Customer(
                                updateInfoName.getText().toString(),
                                updateInfoSurname.getText().toString(),
                                updateInfoHotel.getText().toString(),
                                updateInfoPhone.getText().toString(),
                                ID,
                                Trip
                                );

                        db.collection(Trip).document(ID).set(costumer)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(holder.textViewName.getContext(), "Εκσυγχρόνιση Επιτυχής", Toast.LENGTH_LONG).show();
                                         //dialogPlus.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(holder.textViewName.getContext(), "Δοκιμάστε Ξανά", Toast.LENGTH_LONG).show();
                                //dialogPlus.dismiss();
                            }
                        });
                    }
                });

            }
        });
    }

    @NonNull
    @Override
    public CostumerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,
                parent,false);
        return new CostumerHolder(v, mListener);
    }

    public void setOnItemClickListenerDelete(OnItemClickListenerDelete listener){
        mListener = listener;
    }

    //Custom Listener for each card
    public interface OnItemClickListenerDelete{
        void onKlickDelete(int position);
    }

    //Here we make some variables that point to the card id's!!
    //View item is the card's name, item.xml
    class CostumerHolder extends RecyclerView.ViewHolder{

        TextView textViewName;
        TextView textViewHotel;
        TextView textViewSurname;
        TextView textViewPhone;

        ImageButton delete_bttn;
        ImageButton update_bttn;

        public CostumerHolder(View item, OnItemClickListenerDelete listener){
            super(item);

            textViewName = item.findViewById(R.id.TextViewName);
            textViewSurname = item.findViewById(R.id.TextViewSurname);
            textViewHotel = item.findViewById(R.id.TextViewHotel);
            textViewPhone = item.findViewById(R.id.TextViewPhone);

            update_bttn = item.findViewById(R.id.edit_button);
            delete_bttn = item.findViewById(R.id.delete_button);

            //Here we get the position of the card clicked
            delete_bttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAbsoluteAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onKlickDelete(position);
                        }
                    }
                }
            });
        }
    }

    //When called it will delete the card clicked
    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
