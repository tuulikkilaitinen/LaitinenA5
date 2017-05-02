package edu.sdccd.laitinena5;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Tuulikki Laitinen on 4/21/2017.
 */

public class SelectImageDialogFragment extends DialogFragment {

    private String[] mFileList;
    private File mPath = new File(Environment.getExternalStorageDirectory() + "//yourdir//");
    private String mChosenFile;
    private static final String FTYPE = ".png";
    private static final int DIALOG_LOAD_FILE = 1000;
    private static final int FILE_SELECT_CODE = 2;

    // create an Dialog and return it
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        // create dialog
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        View backgroundDialogView = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_select_image, null);
        builder.setView(backgroundDialogView); // add GUI to dialog

        // set the AlertDialog's message
        builder.setTitle(R.string.title_select_file);
/*
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
*/
        // use current background color to set SeekBar values
        final DoodleView doodleView = getDoodleFragment().getDoodleView();
/*
        this.backgroundColor = doodleView.getBGColor();
        redSeekBar.setProgress(Color.red(this.backgroundColor));
        greenSeekBar.setProgress(Color.green(this.backgroundColor));
        blueSeekBar.setProgress(Color.blue(this.backgroundColor));


*/
        //builder.setTitle("Choose your file");


        builder.setItems(mFileList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mChosenFile = mFileList[which];
                //you can do stuff with the file here too
            }
        });



        // add Set Color Button
        builder.setPositiveButton(R.string.button_select_file,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //doodleView.setBGColor(BackgroundDialogFragment.this.backgroundColor);
                        SelectImageDialogFragment.this.showFileChooser();
                    }
                }
        );

        return builder.create(); // return dialog
    }

    private void showFileChooser() {

        Intent intent = new Intent (Intent.ACTION_GET_CONTENT);
        //Intent intent = new Intent ( );
        //intent.setType("image/png");
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {

            getActivity().startActivityForResult(Intent.createChooser(intent, "Select Image For Background"),
                                    FILE_SELECT_CODE);
        } catch (ActivityNotFoundException exception) {
            Toast.makeText(getContext(), "Please install File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println (resultCode);
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == FILE_SELECT_CODE) {

                    //get the URI of the selected file
                    Uri uri = data.getData();
                    //get the path
                    //String path = File
                    String path = uri.getPath();
                    System.out.println (path);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    private void loadFileList() {
        try {
            mPath.mkdirs();
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
        if(mPath.exists()) {
            FilenameFilter filter = new FilenameFilter() {

                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    return filename.contains(FTYPE) || sel.isDirectory();
                }

            };
            mFileList = mPath.list(filter);
        }
        else {
            mFileList= new String[0];
        }
    }

    // OnSeekBarChangeListener for the SeekBars in the color dialog
    private final SeekBar.OnSeekBarChangeListener colorChangedListener =
            new SeekBar.OnSeekBarChangeListener() {
                // display the updated color
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {

                    if (fromUser){} // user, not program, changed SeekBar progress
                        //BackgroundDialogFragment.this.backgroundColor = Color.rgb(
                                //redSeekBar.getProgress(), greenSeekBar.getProgress(),
                                //blueSeekBar.getProgress());
                    //backgroundView.setBackgroundColor(BackgroundDialogFragment.this.backgroundColor);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {} // required

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {} // required
            };
}
