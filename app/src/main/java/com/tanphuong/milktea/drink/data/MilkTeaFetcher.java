package com.tanphuong.milktea.drink.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tanphuong.milktea.drink.data.model.Ingredient;
import com.tanphuong.milktea.drink.data.model.MilkTea;
import com.tanphuong.milktea.drink.data.model.RealIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MilkTeaFetcher {
    private static final String TAG = "MilkTeaFetcher";

    public static void fetchMilkTeas(List<Ingredient> ingredients, Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("milk_teas").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<MilkTea> milkTeas = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        List<RealIngredient> milkTeaIngredients = new ArrayList<>();
                        List refs = (List) document.get("ingredients");
                        for (Object object : refs) {
                            Map values = (Map) object;
                            DocumentReference ingredientRef = (DocumentReference) values.get("ingredient");
                            float quantity = ((Double) values.get("quantity")).floatValue();
                            for (Ingredient ingredient : ingredients) {
                                if (!ingredient.getId().equals(ingredientRef.getId())) {
                                    continue;
                                }
                                milkTeaIngredients.add(new RealIngredient(
                                        ingredientRef.getId(),
                                        ingredient.getName(),
                                        ingredient.getUnit(),
                                        ingredient.getPricePerUnit(),
                                        quantity
                                ));
                            }
                        }
                        milkTeas.add(new MilkTea(
                                document.getId(),
                                document.getString("name"),
                                document.getString("describe"),
                                document.getString("recipe"),
                                document.getDouble("labor_cost").intValue(),
                                document.getString("cover_image"),
                                milkTeaIngredients
                        ));
                    }
                    callback.onLoaded(milkTeas);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    callback.onFailed();
                }
            }
        });
    }

    public interface Callback {
        void onLoaded(List<MilkTea> milkTeas);

        void onFailed();
    }
}
