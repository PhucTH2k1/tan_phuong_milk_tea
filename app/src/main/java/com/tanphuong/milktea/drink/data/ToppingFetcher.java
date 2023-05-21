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
import com.tanphuong.milktea.drink.data.model.RealIngredient;

import java.util.ArrayList;
import java.util.List;

public final class ToppingFetcher {
    private static final String TAG = "ToppingFetcher";

    public static void fetchToppings(List<Ingredient> ingredients, Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("toppings").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<RealIngredient> toppings = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        DocumentReference ingredientRef = document.getDocumentReference("ingredient");
                        if (ingredientRef == null) {
                            continue;
                        }
                        for (Ingredient ingredient : ingredients) {
                            if (!ingredient.getId().equals(ingredientRef.getId())) {
                                continue;
                            }
                            toppings.add(new RealIngredient(
                                    document.getId(),
                                    ingredient.getName(),
                                    ingredient.getUnit(),
                                    ingredient.getPricePerUnit(),
                                    document.getDouble("quantity").floatValue()
                            ));
                        }
                    }
                    callback.onLoaded(toppings);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    callback.onFailed();
                }
            }
        });
    }

    public interface Callback {
        void onLoaded(List<RealIngredient> toppings);

        void onFailed();
    }
}
