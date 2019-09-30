package com.example.foodrecipes.repositories;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.foodrecipes.AppExecutors;
import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.persistence.RecipeDao;
import com.example.foodrecipes.persistence.RecipeDatabase;
import com.example.foodrecipes.requests.ServiceGenerator;
import com.example.foodrecipes.requests.responses.ApiResponse;
import com.example.foodrecipes.requests.responses.RecipeSearchResponse;
import com.example.foodrecipes.util.Constants;
import com.example.foodrecipes.util.NetworkBoundResource;
import com.example.foodrecipes.util.Resource;

import java.util.List;

public class RecipeRepository {

    private static final String TAG = "RecipeRepository";

    private static RecipeRepository instance;
    private RecipeDao recipeDao;

    public static RecipeRepository getInstance(Context context){
        if (instance == null){
            instance = new RecipeRepository(context);
        }
        return instance;
    }

    private RecipeRepository(Context context) {
        recipeDao = RecipeDatabase.getInstance(context).getRecipeDao();
    }

    public LiveData<Resource<List<Recipe>>> searchRecipesAPI (final String query, final int pageNumber){
        return new NetworkBoundResource<List<Recipe>, RecipeSearchResponse>(AppExecutors.getInstance()){
            @Override
            protected void saveCallResult(@NonNull RecipeSearchResponse item) {
                if (item.getRecipes() != null){ //recipe list will be null if api key is expired

                    Recipe[] recipes = new Recipe[item.getRecipes().size()];
                    int index = 0;
                    for (long rowid: recipeDao.insertRecipes((Recipe[]) (item.getRecipes().toArray(recipes)))){
                        if (rowid == -1){
                            Log.d(TAG, "saveCallResult: CONFLICT...this recipe is already in the cache");
                            //if the recipe already exists then we don't have to set the ingredients or timestamp b/c they will be erased
                            recipeDao.updateRecipe(
                                    recipes[index].getRecipe_id(),
                                    recipes[index].getTitle(),
                                    recipes[index].getPublisher(),
                                    recipes[index].getImage_url(),
                                    recipes[index].getSocial_rank()

                            );
                        }
                        index++;
                    }

                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Recipe> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Recipe>> loadFromDb() {
                return recipeDao.searchRecipes(query, pageNumber);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<RecipeSearchResponse>> createCall() {
                return ServiceGenerator.getRecipeApi()
                        .searchRecipe(
                                Constants.API_KEY,
                                query,
                                String.valueOf(pageNumber)
                        );
            }
        }.getAsLiveData();
    }
}
