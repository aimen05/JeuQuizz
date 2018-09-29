package com.bochenchleba.mapquiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bochenchleba on 12/03/18.
 */

public class SettingsDialog extends DialogFragment{

    SharedPreferences prefs;

    CheckBox chkAF;
    CheckBox chkAS;
    CheckBox chkEU;
    CheckBox chkSA;
    CheckBox chkNA;
    CheckBox chkOT;

    RadioGroup radioGroup;

    GridLayout continentsGridLayout;

    Button btnSave;

    Set<String> defaultContinents;
    Set<String> selectedContinents;
    String selectedDifficulty;

    SettingsListener callback;


    public SettingsDialog(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        findViews(view);

        setCurrentSettings();

        btnSave.setOnClickListener(saveClickListener);

        return view;
    }

    private void findViews(View view){

        chkAF = view.findViewById(R.id.africaChkBox);
        chkAS = view.findViewById(R.id.asiaChkBox);
        chkEU = view.findViewById(R.id.europaChkBox);
        chkSA = view.findViewById(R.id.southAmericaChkBox);
        chkNA = view.findViewById(R.id.northAmericaChkBox);
        chkOT = view.findViewById(R.id.otherChkBox);

        radioGroup = view.findViewById(R.id.radio_group);

        continentsGridLayout = view.findViewById(R.id.continents_grid_layout);

        btnSave = view.findViewById(R.id.settings_save_btn);

        callback = (SettingsListener) getActivity();

        prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
    }

    private void setCurrentSettings(){

        defaultContinents = new HashSet<>(Arrays.asList("AF", "AS", "EU", "OTHER", "SA", "US"));
        selectedContinents = prefs.getStringSet(Fields.PREFS_KEY_CONTINENTS, defaultContinents);
        selectedDifficulty = prefs.getString(Fields.PREFS_KEY_DIFFICULTY, "2");

        for (String continent : selectedContinents) {
            switch (continent){

                case "AF":
                    chkAF.setChecked(true);
                    break;
                case "AS":
                    chkAS.setChecked(true);
                    break;
                case "EU":
                    chkEU.setChecked(true);
                    break;
                case "OTHER":
                    chkOT.setChecked(true);
                    break;
                case "SA":
                    chkSA.setChecked(true);
                    break;
                case "US":
                    chkNA.setChecked(true);
                    break;
                default:
                    break;
            }
        }

        switch (selectedDifficulty){

            case "1":
                radioGroup.check(R.id.easy_radio);
                break;
            case "2":
                radioGroup.check(R.id.medium_radio);
                break;
            case "3":
                radioGroup.check(R.id.hard_radio);
                break;
            default:
                break;
        }
    }

    View.OnClickListener saveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Set<String> continentsToSave = new HashSet<>();

            for (int i=0; i<continentsGridLayout.getChildCount(); i++){

                CheckBox checkBox = (CheckBox) continentsGridLayout.getChildAt(i);

                if (checkBox.isChecked()){
                    continentsToSave.add(checkBox.getTag().toString());
                }
            }

            RadioButton selectedRadio = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());

            String difficultyToSave = selectedRadio.getTag().toString();

            if ( !continentsToSave.equals(selectedContinents) || !difficultyToSave.equals(selectedDifficulty) ){

                if (continentsToSave.size() > 0){

                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putStringSet(Fields.PREFS_KEY_CONTINENTS, continentsToSave);
                    editor.putString(Fields.PREFS_KEY_DIFFICULTY, difficultyToSave);
                    editor.apply();

                    callback.dataSaved();
                    dismiss();
                }
                else {
                    Toast.makeText(getContext(), getString(R.string.toast_no_continents),
                            Toast.LENGTH_SHORT).show();
                }
            }
            else
                dismiss();
        }
    };

    public interface SettingsListener {
        void dataSaved ();
    }
}
