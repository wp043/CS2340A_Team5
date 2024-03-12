package com.example.greenplate.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.greenplate.R;
import com.example.greenplate.models.GreenPlateStatus;
import com.example.greenplate.models.Meal;
import com.example.greenplate.viewmodels.InputMealViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InputMealFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputMealFragment extends Fragment {

    // Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private InputMealViewModel inputMealVM;

    // Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText nameEditText;
    private EditText caloriesEditText;
    private Button submitButton;

    public InputMealFragment() {
        // Required empty public constructor
        inputMealVM = new InputMealViewModel();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InputMealFragment.
     */
    // Rename and change types and number of parameters
    public static InputMealFragment newInstance(String param1, String param2) {
        InputMealFragment fragment = new InputMealFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_input_meal, container, false);
        nameEditText = view.findViewById(R.id.im_mealname_input);
        caloriesEditText = view.findViewById(R.id.im_calorie_input);
        submitButton = view.findViewById(R.id.im_submit);

        TextView date = (TextView) view.findViewById(R.id.im_date);
        TextView height = (TextView) view.findViewById(R.id.im_height_display);
        TextView weight = (TextView) view.findViewById(R.id.im_weight_display);
        TextView gender = (TextView) view.findViewById(R.id.im_gender_display);
        TextView goal = (TextView) view.findViewById(R.id.im_goal_display);
        TextView intake = (TextView) view.findViewById(R.id.im_daily_intake);

        String dateText = "Today's Date: " + inputMealVM.getDateToday();
        date.setText(dateText);

        inputMealVM.getUserHeight(height);
        inputMealVM.getUserWeight(weight);
        inputMealVM.getUserGender(gender);
        inputMealVM.getCalorieGoal(goal);
        inputMealVM.getIntakeToday(intake);

        submitButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String calories = caloriesEditText.getText().toString();
            if (inputMealVM.isInputDataValid(name, calories, nameEditText, caloriesEditText)) {
                Meal currMeal = new Meal(nameEditText.getText().toString(),
                        Long.parseLong(calories));
                GreenPlateStatus status = inputMealVM.addMealToDatabase(currMeal);
                nameEditText.getText().clear();
                caloriesEditText.getText().clear();
                hideKeyboardFrom(getContext(), view);
                Toast.makeText(getContext(),
                                "Meal added successfully.",
                                Toast.LENGTH_SHORT)
                        .show();
                Log.d("Info", status.toString());
            }
        });

        // Button Listeners
        Button dailyCaloriesButton = view.findViewById(R.id.daily_calorie_intake_graph_button);
        Button calorieGoalButton = view.findViewById(R.id.calorie_goal_graph_button);

        dailyCaloriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MealBreakdownChartActivity.class);
                startActivity(intent);
            }
        });
        calorieGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DailyGoalMealInputChartActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    // Method for closing the keyboard in fragment.
    private void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}