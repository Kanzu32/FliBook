package com.kanzu.flibook.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.kanzu.flibook.BookData;
import com.kanzu.flibook.CustomListAdapter;
import com.kanzu.flibook.Network;
import com.kanzu.flibook.R;
import com.kanzu.flibook.databinding.FragmentSearchBinding;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;
    private FragmentSearchBinding binding;

    boolean loaded = false;
    int pageNumber;
    int pageCount = 0;

    private View root;

    Bundle state = null;
    Bundle savedData = null;
    NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        ImageButton searchButton = (ImageButton) root.findViewById(R.id.searchButton);
        ImageButton prevPageButton = (ImageButton) root.findViewById(R.id.prevPageButton);
        ImageButton nextPageButton = (ImageButton) root.findViewById(R.id.nextPageButton);

        TextView errorMessage = (TextView) root.findViewById(R.id.errorMessage);
        errorMessage.setVisibility(View.GONE);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> names = new ArrayList<String>();
                ArrayList<String> authors = new ArrayList<String>();
                ArrayList<String> genres = new ArrayList<String>();
                TextView textField = (TextView) root.findViewById(R.id.searchField);
                EditText pageNumberField = (EditText) root.findViewById(R.id.pageNumberField);
                String numString = pageNumberField.getText().toString();

                if (numString.length() == 0) {
                    pageNumber = 0;
                } else {
                    pageNumber = Integer.parseInt(pageNumberField.getText().toString());
                }

                String text = textField.getText().toString();
                try {
                    pageCount = (int)Network.getPageCountTask(text).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (pageNumber == 0) pageNumber = 1;
                if (pageNumber > pageCount) pageNumber = pageCount;
                ArrayList<BookData> res = new ArrayList<BookData>();
                try {
                    res = (ArrayList<BookData>) Network.searchTask(text, pageNumber).get();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (res.size() == 0 && loaded) {
                    errorMessage.setVisibility(View.VISIBLE);
                } else {
                    errorMessage.setVisibility(View.GONE);
                }
                for (BookData item : res) {
                    names.add(item.name);
                    authors.add(item.author);
                    genres.add(item.genres);
                }

                TextView pageIndicator = (TextView) root.findViewById(R.id.pageIndicator);
                pageIndicator.setText("/" + (pageCount));

                CustomListAdapter adapter = new CustomListAdapter(getActivity(), names.toArray(new String[0]), authors.toArray(new String[0]), genres.toArray(new String[0]));
                ListView list = (ListView) root.findViewById(R.id.list);
                list.setAdapter(adapter);

                ArrayList<BookData> finalRes = res;
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        navController = Navigation.findNavController(root);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", finalRes.get(position).name);
                        bundle.putString("author", finalRes.get(position).author);
                        bundle.putString("genres", finalRes.get(position).genres);
                        bundle.putInt("id", finalRes.get(position).id);
                        state = navController.saveState();
                        savedData = new Bundle();
                        savedData.putParcelableArrayList("RES", finalRes);
                        navController.navigate(R.id.action_nav_add_to_nav_data, bundle);

                    }
                });

                loaded = true;
            }
        });

        prevPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loaded && pageNumber > 1) {
                    EditText field = (EditText) root.findViewById(R.id.pageNumberField);
                    field.setText(String.valueOf(pageNumber-1));
                    ImageButton button = (ImageButton) root.findViewById(R.id.searchButton);
                    button.performClick();
                }
            }
        });

        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loaded && pageNumber < pageCount) {
                    EditText field = (EditText) root.findViewById(R.id.pageNumberField);
                    field.setText(String.valueOf(pageNumber+1));
                    ImageButton button = (ImageButton) root.findViewById(R.id.searchButton);
                    button.performClick();
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (state != null) {
            navController.restoreState(state);
        }
        if (savedData != null) {
            ArrayList<String> names = new ArrayList<String>();
            ArrayList<String> authors = new ArrayList<String>();
            ArrayList<String> genres = new ArrayList<String>();

            ArrayList<BookData> savedRes = savedData.getParcelableArrayList("RES");
            for (BookData item : savedRes) {
                names.add(item.name);
                authors.add(item.author);
                genres.add(item.genres);
            }

            TextView pageIndicator = (TextView) root.findViewById(R.id.pageIndicator);
            pageIndicator.setText("/" + (pageCount));

            CustomListAdapter adapter = new CustomListAdapter(getActivity(), names.toArray(new String[0]), authors.toArray(new String[0]), genres.toArray(new String[0]));
            ListView list = (ListView) root.findViewById(R.id.list);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    navController = Navigation.findNavController(root);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", savedRes.get(position).name);
                    bundle.putString("author", savedRes.get(position).author);
                    bundle.putInt("id", savedRes.get(position).id);
                    bundle.putString("genres", savedRes.get(position).genres);
                    state = navController.saveState();
                    savedData = new Bundle();
                    savedData.putParcelableArrayList("RES", savedRes);
                    navController.navigate(R.id.action_nav_add_to_nav_data, bundle);

                }
            });

        }
    }
}