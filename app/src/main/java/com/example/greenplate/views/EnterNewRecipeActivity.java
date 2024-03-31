package com.example.greenplate.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.greenplate.R;
import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Ingredient;
import com.example.greenplate.models.Recipe;
import com.example.greenplate.viewmodels.RecipeViewModel;
import com.example.greenplate.viewmodels.listeners.OnRecipeAddedListener;
import com.example.greenplate.viewmodels.managers.CookbookManager;

import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnterNewRecipeActivity extends AppCompatActivity {

    private RecipeViewModel recipeViewModel;
    private LinearLayout ingredientsContainer;
    private Button buttonAddIngredient;
    private Button submitRecipe;
    private EditText recipeNameEditText;
    private LinearLayout instructionsContainer;
    private Button buttonAddInstruction;
    private OnRecipeAddedListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_new_recipe);

        recipeViewModel = new RecipeViewModel(); // Initialize your RecipeViewModel
        recipeNameEditText = findViewById(R.id.recipe_name);
        ingredientsContainer = findViewById(R.id.ingredients_container);
        instructionsContainer = findViewById(R.id.instructions_container);
        buttonAddIngredient = findViewById(R.id.btn_add_ingredient);
        buttonAddInstruction = findViewById(R.id.btn_add_instruction);
        submitRecipe = findViewById(R.id.submit_recipe);
        submitRecipe.setEnabled(false);
        buttonAddIngredient.setOnClickListener(v -> addIngredientField());
        buttonAddInstruction.setOnClickListener(v -> addInstructionField());
        submitRecipe.setOnClickListener(v -> submitRecipe());

        Button cancelRecipeButton = findViewById(R.id.cancelRecipe);
        cancelRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Just finish the current activity to return to the previous screen
                finish();
            }
        });
        Button submitRecipe = findViewById(R.id.submit_recipe);
        submitRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRecipe();
            }
        });
    }

    private void addIngredientField() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final ViewGroup ingredientView = (ViewGroup) inflater.inflate(
                R.layout.recipe_ingredient_list, ingredientsContainer, false);


        EditText ingredientName = ingredientView.findViewById(R.id.ingredientName);
        EditText ingredientQuantity = ingredientView.findViewById(R.id.ingredientQuantity);

        // Optionally set up a button to remove this ingredient field
        Button removeButton = new Button(this);
        removeButton.setText("-");
        removeButton.setOnClickListener(v -> ingredientsContainer.removeView(ingredientView));

        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ingredientView.addView(removeButton, buttonLayoutParams);

        ingredientsContainer.addView(ingredientView);

        // Enable the submit button if it's not already enabled
        if (!submitRecipe.isEnabled()) {
            submitRecipe.setEnabled(true);
        }
    }
    private void addInstructionField() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final ViewGroup instructionView = (ViewGroup) inflater.inflate(
                R.layout.instruction_list, instructionsContainer, false);


        EditText instruction = instructionView.findViewById(R.id.recipe_instruction);

        // Optionally set up a button to remove this ingredient field
        Button removeButton = new Button(this);
        removeButton.setText("-");
        removeButton.setOnClickListener(v -> instructionsContainer.removeView(instructionView));

        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        instructionView.addView(removeButton, buttonLayoutParams);

        instructionsContainer.addView(instructionView);
    }

    private void submitRecipe() {
        String recipeNameStr = recipeNameEditText.getText().toString().trim();

        List<Ingredient> ingredients = collectIngredients();
        if (ingredients == null) {
            showToast("Error in ingredient input. Please correct and try again.");
            return;
        }
        List<String> instructions = collectInstructions();


        // Validate each ingredient
        GreenPlateStatus status = recipeViewModel.validateRecipeData(
                recipeNameStr, instructions, ingredients);
        if (!status.isSuccess()) {
            showToast(status.getMessage());
            return;
        }

        Recipe recipe = new Recipe(recipeNameStr, ingredients, new ArrayList<>());
        recipeViewModel.addRecipe(recipe, new OnRecipeAddedListener() {
            @Override
            public void onRecipeAdded(boolean success) {
                if (success) {
                    showToast("Recipe added successfully!");
                    finish();
                } else {
                    showToast("Failed to add recipe.");
                }
            }
        });
    }

    private List<Ingredient> collectIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientsContainer.getChildCount(); i++) {
            View ingredientView = ingredientsContainer.getChildAt(i);
            EditText ingredientNameEditText = ingredientView.findViewById(R.id.ingredientName);
            EditText ingredientQuantityEditText = ingredientView.findViewById(
                    R.id.ingredientQuantity);
            EditText ingredientCaloriesEditText = ingredientView.findViewById(
                    R.id.ingredientCalories);

            String ingredientName = ingredientNameEditText.getText().toString().trim();
            String quantityStr = ingredientQuantityEditText.getText().toString().trim();
            String caloriesStr = ingredientCaloriesEditText.getText().toString().trim();

            if (!ingredientName.isEmpty() && !quantityStr.isEmpty() && !caloriesStr.isEmpty()) {
                try {
                    double ingredientQuantity = Double.parseDouble(quantityStr);
                    double ingredientCalorie = Double.parseDouble(caloriesStr);
                    if (ingredientQuantity > 0) {
                        ingredients.add(new Ingredient(ingredientName, ingredientCalorie,
                                ingredientQuantity, null));
                    }
                } catch (NumberFormatException e) {
                    Log.e("EnterNewRecipeActivity", "Invalid number format", e);
                    showToast("Please enter a valid number for quantity and calories.");
                    return null;
                }
            }
        }
        return ingredients;
    }

    private List<String> collectInstructions() {
        List<String> instructions = new ArrayList<>();
        LinearLayout instructionsContainer = findViewById(R.id.instructions_container);
        for (int i = 0; i < instructionsContainer.getChildCount(); i++) {
            View view = instructionsContainer.getChildAt(i);
            if (view instanceof EditText) {
                String instruction = ((EditText) view).getText().toString().trim();
                if (!instruction.isEmpty()) {
                    instructions.add(instruction);
                }
            }
        }
        return instructions;
    }

    private void showToast(String message) {
        Toast.makeText(EnterNewRecipeActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
