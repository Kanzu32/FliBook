package com.kanzu.flibook.ui.fromfile;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.adobe.dp.epub.io.OCFContainerWriter;
import com.adobe.dp.epub.opf.Publication;
import com.adobe.dp.fb2.FB2Document;
import com.adobe.dp.fb2.convert.Converter;
import com.kanzu.flibook.R;
import com.kanzu.flibook.Storage;
import com.kanzu.flibook.databinding.FragmentFromfileBinding;
import com.obsez.android.lib.filechooser.ChooserDialog;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FromFileFragment extends Fragment {

    private FromFileViewModel fromFileViewModel;
    private FragmentFromfileBinding binding;
    File file;
    boolean fileLoaded = false;
    boolean isFb2;
    boolean isPdf;
    Button fileButton;
    Button addButton;
    TextView path;
    TextView typeError;
    TextView loadSuccess;
    EditText editName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fromFileViewModel =
                new ViewModelProvider(this).get(FromFileViewModel.class);

        binding = FragmentFromfileBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        fileButton = root.findViewById(R.id.fileButton);
        addButton = root.findViewById(R.id.addButton);
        path = root.findViewById(R.id.path);
        typeError = root.findViewById(R.id.typeError);
        editName = root.findViewById(R.id.editName);
        loadSuccess = root.findViewById(R.id.loadSuccess);

        typeError.setVisibility(View.INVISIBLE);
        addButton.setEnabled(false);

        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ChooserDialog chooserDialog;
                    chooserDialog = new ChooserDialog(getContext())
                            .withChosenListener(new ChooserDialog.Result() {
                                @Override
                                public void onChoosePath(String p, File pathFile) {
                                    file = pathFile;
                                    fileLoaded = true;
                                    String filePath = file.getPath();
                                    String type = filePath.substring(filePath.lastIndexOf('.'));
                                    String name = filePath.substring(filePath.lastIndexOf('/')+1, filePath.lastIndexOf('.'));
                                    if (type.equals(".fb2")) {
                                        typeError.setVisibility(View.INVISIBLE);
                                        addButton.setEnabled(true);
                                        path.setVisibility(View.VISIBLE);
                                        path.setText(file.getAbsolutePath());
                                        loadSuccess.setVisibility(View.VISIBLE);
                                        editName.setText(name);
                                        isFb2 = true;
                                        isPdf = false;
                                    } else if (type.equals(".epub")){
                                        typeError.setVisibility(View.INVISIBLE);
                                        addButton.setEnabled(true);
                                        path.setVisibility(View.VISIBLE);
                                        path.setText(file.getAbsolutePath());
                                        loadSuccess.setVisibility(View.VISIBLE);
                                        editName.setText(name);
                                        isFb2 = false;
                                        isPdf = false;
                                    } else if (type.equals(".pdf")) {
                                        typeError.setVisibility(View.INVISIBLE);
                                        addButton.setEnabled(true);
                                        path.setVisibility(View.VISIBLE);
                                        path.setText(file.getAbsolutePath());
                                        loadSuccess.setVisibility(View.VISIBLE);
                                        editName.setText(name);
                                        isPdf = true;
                                        isFb2 = false;
                                    }
                                    else {
                                        typeError.setVisibility(View.VISIBLE);
                                        loadSuccess.setVisibility(View.INVISIBLE);
                                        addButton.setEnabled(false);
                                        path.setText("");
                                        path.setVisibility(View.INVISIBLE);
                                    }
                                }
                            })
                            // to handle the back key pressed or clicked outside the dialog:
                            .withOnCancelListener(new DialogInterface.OnCancelListener() {
                                public void onCancel(DialogInterface dialog) {
                                    dialog.cancel(); // MUST have
                                }
                            })
                            .build()
                            .show();
                } catch (Exception e) {
                    fileLoaded = false;
                }

            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPdf) {
                    try {
                        File newFile = new File(getActivity().getExternalFilesDir("books"), editName.getText().toString() + ".pdf");
                        FileUtils.copyFile(file, newFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (isFb2) {
                    try {
                        convertFile(file, editName.getText().toString(), getActivity());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        File newFile = new File(getActivity().getExternalFilesDir("books"), editName.getText().toString() + ".epub");
                        FileUtils.copyFile(file, newFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        });

        return root;
    }


    public void getFile() throws Exception {
        ChooserDialog chooserDialog;
        chooserDialog = new ChooserDialog(getContext())
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        file = pathFile;
                    }
                })
                // to handle the back key pressed or clicked outside the dialog:
                .withOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        dialog.cancel(); // MUST have
                        throw new Error();
                    }
                })
                .build()
                .show();

    }

    public static void convertFile(File fileToConvert, String newFilename, Context context) throws IOException {
        String filename = fileToConvert.getName();
        filename = filename.substring(0, filename.length()-4);
        FB2Document fb2  = null;
        System.out.println(fileToConvert.getAbsolutePath());
        try {
            fb2 = new FB2Document(new FileInputStream(fileToConvert));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Publication epub = new Publication();

        Converter converter = new Converter();
        converter.convert(fb2, epub);
        System.out.println("Filename: " + filename);
        File outFile = new File(context.getExternalFilesDir("books"), filename + ".epub");
        try {
            OCFContainerWriter writer = new OCFContainerWriter(new FileOutputStream(outFile));
            epub.serialize(writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        };
        try {
            Storage.unzip(outFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Storage.update(new File(context.getExternalFilesDir("tmp/" + filename + ".epub/OPS"), "content.opf"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Storage.zip(new File(context.getExternalFilesDir("tmp"), filename+".epub"), newFilename, context);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}