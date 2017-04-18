package com.deitel.doodlz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BackgroundDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BackgroundDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BackgroundDialogFragment extends DialogFragment {
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private View backgroundView;
    private int backgroundColor;

    // create an AlertDialog and return it
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        // create dialog
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        View backgroundDialogView = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_background, null);
        builder.setView(backgroundDialogView); // add GUI to dialog

        // set the AlertDialog's message
        builder.setTitle(R.string.title_color_dialog);

        // get the color SeekBars and set their onChange listeners
        redSeekBar = (SeekBar) backgroundDialogView.findViewById(
                R.id.redBGSeekBar);
        greenSeekBar = (SeekBar) backgroundDialogView.findViewById(
                R.id.greenBGSeekBar);
        blueSeekBar = (SeekBar) backgroundDialogView.findViewById(
                R.id.blueBGSeekBar);
        backgroundView = backgroundDialogView.findViewById(R.id.backgroundView);

        // register SeekBar event listeners
        redSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        greenSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        blueSeekBar.setOnSeekBarChangeListener(colorChangedListener);

        // use current background color to set SeekBar values
        final DoodleView doodleView = getDoodleFragment().getDoodleView();

        this.backgroundColor = doodleView.getMyBackgroundColor();
        redSeekBar.setProgress(Color.red(this.backgroundColor));
        greenSeekBar.setProgress(Color.green(this.backgroundColor));
        blueSeekBar.setProgress(Color.blue(this.backgroundColor));

        // add Set Color Button
        builder.setPositiveButton(R.string.button_set_color,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        doodleView.setMyBackground(BackgroundDialogFragment.this.backgroundColor);
                    }
                }
        );

        return builder.create(); // return dialog
    }

    // gets a reference to the MainActivityFragment
    private MainActivityFragment getDoodleFragment() {
        return (MainActivityFragment) getFragmentManager().findFragmentById(
                R.id.doodleFragment);
    }

    // tell MainActivityFragment that dialog is now displayed
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MainActivityFragment fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(true);
    }

    // tell MainActivityFragment that dialog is no longer displayed
    @Override
    public void onDetach() {
        super.onDetach();
        MainActivityFragment fragment = getDoodleFragment();

        if (fragment != null)
            fragment.setDialogOnScreen(false);
    }

    // OnSeekBarChangeListener for the SeekBars in the color dialog
    private final SeekBar.OnSeekBarChangeListener colorChangedListener =
            new SeekBar.OnSeekBarChangeListener() {
                // display the updated color
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {

                    if (fromUser) // user, not program, changed SeekBar progress
                        BackgroundDialogFragment.this.backgroundColor = Color.rgb(
                                redSeekBar.getProgress(), greenSeekBar.getProgress(),
                                blueSeekBar.getProgress());
                    backgroundView.setBackgroundColor(BackgroundDialogFragment.this.backgroundColor);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {} // required

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {} // required
            };
}