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



public class SettingsDialog extends DialogFragment{

    SharedPreferences prefs;
    CheckBox chkAF,chkAS,chkEU,chkSA,chkNA,chkOT;
    RadioGroup radioGroup;
    GridLayout continentsGridLayout;
    Button btnSave;
    Set<String> defaultContinents;
    Set<String> selectedContinents;
    String selectedDifficulty;
    SettingsListener callback;


    public SettingsDialog(){ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        findViews(view);

        setCurrentSettings();

        btnSave.setOnClickListener(saveClickListener);

        return view;
    }

    private void findViews(View view){

        chkAF = view.findViewById(R.id.afchb);
        chkAS = view.findViewById(R.id.aschb);
        chkEU = view.findViewById(R.id.euchp);
        chkSA = view.findViewById(R.id.sachp);
        chkNA = view.findViewById(R.id.nachp);
        chkOT = view.findViewById(R.id.ochb);

        radioGroup = view.findViewById(R.id.radio_group);

        continentsGridLayout = view.findViewById(R.id.continents_grid_layout);

        btnSave = view.findViewById(R.id.saveboutton);

        callback = (SettingsListener) getActivity();

        prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
    }

    private void setCurrentSettings(){

        defaultContinents = new HashSet<>(Arrays.asList("AF", "AS", "EU", "OTHER", "SA", "US"));
        selectedContinents = prefs.getStringSet(Fields.KEYCONT, defaultContinents);
        selectedDifficulty = prefs.getString(Fields.KEYDIFF, "2");

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
                radioGroup.check(R.id.facileradio);
                break;
            case "2":
                radioGroup.check(R.id.moyenradio);
                break;
            case "3":
                radioGroup.check(R.id.difficileradio);
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

                    editor.putStringSet(Fields.KEYCONT, continentsToSave);
                    editor.putString(Fields.KEYDIFF, difficultyToSave);
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
