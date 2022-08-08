package com.kanzu.flibook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.kanzu.flibook.databinding.FragmentDataBinding;
import com.kanzu.flibook.databinding.FragmentSearchBinding;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class DataFragment extends Fragment {
    BookData book = null;
    private @NonNull FragmentDataBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentDataBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ImageView img = (ImageView) root.findViewById(R.id.coverImage);
        TextView name = (TextView) root.findViewById(R.id.nameField);
        TextView author = (TextView) root.findViewById(R.id.authorField);
        TextView genres = (TextView) root.findViewById(R.id.genresField);
        TextView description = (TextView) root.findViewById(R.id.descriptionField);
        TextView errorMessage = (TextView) root.findViewById(R.id.ErrorMessage);
        Button downloadButton = (Button) root.findViewById(R.id.dowloadButton);
        book = new BookData();
        errorMessage.setVisibility(View.INVISIBLE);
        downloadButton.setEnabled(true);
        assert getArguments() != null;
        book.name = getArguments().getString("name");
        book.author = getArguments().getString("author");
        book.genres = getArguments().getString("genres");
        book.id = getArguments().getInt("id");
        try {
            book = (BookData) Network.getDataTask(book).get();
        } catch (Exception e) { e.printStackTrace(); }

        name.setText(book.name);
        author.setText(book.author);
        genres.setText(book.genres);
        description.setText(book.description);
        if (book.hasCover) {
            img.setImageBitmap(book.img);
        } else {
            img.setVisibility(View.GONE);
        }

        if (!book.downloadTypes.contains("epub") && !book.downloadTypes.contains("fb2") && !book.downloadTypes.contains("pdf")) {
            errorMessage.setVisibility(View.VISIBLE);
            downloadButton.setEnabled(false);
        }

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Скачивание началось.", Toast.LENGTH_SHORT).show();
                try {
                    Runnable r;
                    if (book.downloadTypes.contains("pdf")) {
                        r = new MyThread(book, getActivity(), "pdf");
                    } else {
                        r = new MyThread(book, getActivity(), "epub");
                    }
                    new Thread(r).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
        });

        return root;
    }
}
