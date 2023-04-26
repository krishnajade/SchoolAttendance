package com.example.schoolattendence;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    private Button searchButton;
    private TableLayout attendanceTable;
    private Spinner classSpinner;
    private Spinner sectionSpinner;
    private String selectedClass, selectedSection;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        TextInputEditText DateEditText = rootView.findViewById(R.id.date_picker);

        // Get the current date and set it as the default text for the fromDateEditText
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
        DateEditText.setText(currentDate);

        //Get the values of class and section from spinner
        classSpinner = rootView.findViewById(R.id.spinner_class);
        sectionSpinner = rootView.findViewById(R.id.spinner_section);
        TextView classTextview=rootView.findViewById(R.id.text_view_class_label);
        TextView sectionTextview=rootView.findViewById(R.id.text_view_section_label);

        //Fetch data for class and section spinner using endpoint
        String endpointUrl = "https://api.trifrnd.in/testing/student/student.php?apicall=readclass";
        new SpinnerDataFetcher(classSpinner).execute(endpointUrl);

        SpinnerDataFetcherSection spinnerDataFetchersection = new SpinnerDataFetcherSection(sectionSpinner);
        spinnerDataFetchersection.execute("https://api.trifrnd.in/testing/student/student.php?apicall=readsection");



        //use selected values of spinners
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                 selectedClass = (String)adapterView.getItemAtPosition(position);
                // Use the selectedClass variable in your code
                classTextview.setText(selectedClass);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        sectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedSection = (String)adapterView.getItemAtPosition(position);
                // Use the selectedSection variable in your code
                sectionTextview.setText(selectedSection);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Get references to the search button and the machine table
        searchButton = rootView.findViewById(R.id.search_button);
        attendanceTable = rootView.findViewById(R.id.attendance1_table);

        // Set an OnClickListener to the search button
        searchButton.setOnClickListener(v -> {
            // Remove all rows except the header row
            int childCount = attendanceTable.getChildCount();
            attendanceTable.removeViews(1, childCount - 1);
            // Make the API call using Volley library
            String url = "https://api.trifrnd.in/testing/student/student.php?apicall=readall";
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                    response -> {
                        // Process the JSON response and update the attendance table
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject student = response.getJSONObject(i);

                                String sclass = student.getString("class");
                                String section = student.getString("section");

                                String rollNo = student.getString("roll_no");
                                String name = student.getString("stud_name");

                                RadioGroup radioGroup = new RadioGroup(getContext());
                                radioGroup.setOrientation(RadioGroup.HORIZONTAL);

                                RadioButton presentRadioButton = new RadioButton(getContext());
                                presentRadioButton.setText("Present");
                                presentRadioButton.setId(View.generateViewId());

                                RadioButton absentRadioButton = new RadioButton(getContext());
                                absentRadioButton.setText("Absent");
                                absentRadioButton.setId(View.generateViewId());

                                radioGroup.addView(presentRadioButton);
                                radioGroup.addView(absentRadioButton);

                                TableRow.LayoutParams layoutParamsStatus = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 3f);
                                radioGroup.setLayoutParams(layoutParamsStatus);

                                TableRow row = new TableRow(getContext());
                                //only show the students of user selected class and section
                                if(sclass.equals(selectedClass)&&section.equals(selectedSection)){
                                    TextView rollNoTextView = new TextView(getContext());
                                    rollNoTextView.setText(rollNo);
                                    TableRow.LayoutParams layoutParamsRoll = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                                    rollNoTextView.setLayoutParams(layoutParamsRoll);

                                    TextView nameTextView = new TextView(getContext());
                                    nameTextView.setText(name);
                                    TableRow.LayoutParams layoutParamsName = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 3f);
                                    nameTextView.setLayoutParams(layoutParamsName);
                                    nameTextView.setPadding(20,0,0,0);

                                    row.addView(rollNoTextView);
                                    row.addView(nameTextView);
                                    row.addView(radioGroup);
                                }

                                attendanceTable.addView(row);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    Throwable::printStackTrace);

            // Add the request to the RequestQueue
            RequestQueue queue = Volley.newRequestQueue(getContext());
            queue.add(request);
        });
        return rootView;
    }
    }