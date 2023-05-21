package com.tanphuong.milktea.drink.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tanphuong.milktea.drink.data.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public final class IngredientFetcher {
    private static final String TAG = "IngredientFetcher";

    public static void fetchIngredients(Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ingredients").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Ingredient> ingredients = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        ingredients.add(new Ingredient(
                                document.getId(),
                                document.getString("name"),
                                document.getString("unit"),
                                document.getDouble("price_per_unit").intValue()
                        ));
                    }
                    callback.onLoaded(ingredients);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    callback.onFailed();
                }
            }
        });
    }

    public interface Callback {
        void onLoaded(List<Ingredient> ingredients);

        void onFailed();
    }
}
