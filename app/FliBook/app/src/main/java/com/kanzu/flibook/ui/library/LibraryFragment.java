package com.kanzu.flibook.ui.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.folioreader.Config;
import com.folioreader.FolioReader;
import com.folioreader.model.HighLight;
import com.folioreader.model.locators.ReadLocator;
import com.folioreader.ui.base.OnSaveHighlight;
import com.folioreader.util.AppUtil;
import com.folioreader.util.OnHighlightListener;
import com.folioreader.util.ReadLocatorListener;
import com.kanzu.flibook.CustomFileListAdapter;
import com.kanzu.flibook.HighlightData;
import com.kanzu.flibook.MainActivity;
import com.kanzu.flibook.PdfActivity;
import com.kanzu.flibook.R;
import com.kanzu.flibook.Storage;
import com.kanzu.flibook.databinding.FragmentLibraryBinding;

import org.readium.r2.streamer.parser.EpubParser;
import org.readium.r2.streamer.parser.PubBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LibraryFragment extends Fragment implements OnHighlightListener, ReadLocatorListener, FolioReader.OnClosedListener{

    private LibraryViewModel libraryViewModel;
    private FragmentLibraryBinding binding;
    private FolioReader folioReader;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public Activity context = getActivity();

    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> fileNames = new ArrayList<String>();

    CustomFileListAdapter adapter;
    ListView list;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        libraryViewModel =
                new ViewModelProvider(this).get(LibraryViewModel.class);

        binding = FragmentLibraryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        folioReader = FolioReader.get()
                .setOnHighlightListener(this)
                .setReadLocatorListener(this)
                .setOnClosedListener(this);




        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        File folder = getActivity().getExternalFilesDir("books");
        searchForFiles(folder);

        adapter = new CustomFileListAdapter(getActivity(), names.toArray(new String[0]));
        list = (ListView) getView().findViewById(R.id.list);
        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (fileNames.get(position).endsWith(".pdf")) {
                    Intent intent = new Intent(getActivity(), PdfActivity.class);
                    intent.putExtra("name", fileNames.get(position));
                    startActivity(intent);
                } else {
                    ReadLocator readLocator = null;
                    try {
                        readLocator = getLastReadLocator(position);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Config config = AppUtil.getSavedConfig(getContext());
                    if (config == null)
                        config = new Config();
                    config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL);

                    folioReader.setReadLocator(readLocator);
                    folioReader.setConfig(config, true)
                            .openBook(new File(getActivity().getExternalFilesDir("books"), fileNames.get(position)).getPath());
                }
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                String[] lst = {"Rename", "Delete"};
                builder.setItems(lst, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                final String[] newName = {""};
                                AlertDialog.Builder innerBuilder = new AlertDialog.Builder(getActivity());
                                innerBuilder.setTitle("Input new name");
                                final EditText input = new EditText(getActivity());
                                input.setInputType(InputType.TYPE_CLASS_TEXT);
                                innerBuilder.setView(input);

                                innerBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        newName[0] = input.getText().toString();
                                        if (!newName[0].equals("")) {
                                            if (fileNames.get(position).endsWith(".pdf")) {
                                                Storage.rename(new File(getActivity().getExternalFilesDir("books"), fileNames.get(position)), newName[0]+".pdf");
                                            } else if (fileNames.get(position).endsWith(".epub")) {
                                                Storage.rename(new File(getActivity().getExternalFilesDir("books"), fileNames.get(position)), newName[0] + ".epub");
                                            }
                                            searchForFiles(folder);
                                            reloadAdapter();
                                        }
                                    }
                                });
                                innerBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog dialog1 = innerBuilder.create();
                                dialog1.show();
                                break;
                            case 1:
                                Storage.delete(new File(getActivity().getExternalFilesDir("books"), fileNames.get(position)));
                                searchForFiles(folder);
                                reloadAdapter();
                                break;
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }

        });

    }

    private ReadLocator getLastReadLocator(int position) throws IOException {
        String id = getIdentifier(position);
        File jsonFile = new File(getActivity().getExternalFilesDir("bookmarks"), id+".json");

        String jsonString = new String(Files.readAllBytes(jsonFile.toPath()));
        return ReadLocator.fromJson(jsonString);
    }

    @Override
    public void saveReadLocator(ReadLocator readLocator) {
        Log.i(LOG_TAG, "-> saveReadLocator -> " + readLocator.toJson());
        try {
            PrintWriter out = new PrintWriter(new FileWriter(new File(getActivity().getExternalFilesDir("bookmarks"), readLocator.getBookId()+".json")));
            out.write(readLocator.toJson());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getHighlightsAndSave() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<HighLight> highlightList = null;
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    highlightList = objectMapper.readValue(
                            loadAssetTextAsString("highlights/highlights_data.json"),
                            new TypeReference<List<HighlightData>>() {
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (highlightList == null) {
                    folioReader.saveReceivedHighLights(highlightList, new OnSaveHighlight() {
                        @Override
                        public void onFinished() {
                            //You can do anything on successful saving highlight list
                        }
                    });
                }
            }
        }).start();
    }

    private String loadAssetTextAsString(String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = getActivity().getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e("HomeActivity", "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("HomeActivity", "Error closing asset " + name);
                }
            }
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        super.onDestroyView();
        FolioReader.clear();
        binding = null;
    }

    @Override
    public void onHighlight(HighLight highlight, HighLight.HighLightAction type) {
        Toast.makeText(getActivity(),
                "Заметка сохранена.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFolioReaderClosed() {
        Log.v(LOG_TAG, "-> onFolioReaderClosed");
    }

    public void searchForFiles(File folder) {
        fileNames.clear();
        names.clear();
        File file;
        for (String item : Objects.requireNonNull(folder.list())) {
            if (item.endsWith(".epub")) {
                fileNames.add(item);
                try {
                    file = new File(getActivity().getExternalFilesDir("books"), item);
                    Path tmp = Storage.unzip(file, getActivity());
                    names.add(item.substring(0, item.length() - 5));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (item.endsWith(".pdf")) {
                fileNames.add(item);
                names.add(item.substring(0, item.length()-4));
            }
        }
    }

    public void reloadAdapter() {
        adapter = new CustomFileListAdapter(getActivity(), names.toArray(new String[0]));
        list.setAdapter(adapter);
    }

    public String getIdentifier(int position) {
        EpubParser parser = new EpubParser();
        PubBox box = parser.parse(new File(getActivity().getExternalFilesDir("books"), fileNames.get(position)).getAbsolutePath(), "");
        return box.getPublication().getMetadata().identifier;
    }

//    public void downloadButtonClick(View view) {
//        Intent intent = new Intent(context, searchActivity.class);
//        startActivity(intent);
//    }
}