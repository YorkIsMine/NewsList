package com.yorkismine.newslist.junk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.yorkismine.newslist.Article;
import com.yorkismine.newslist.R;
import com.yorkismine.newslist.parser.CsvParser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.List;

public class JunkActivity extends AppCompatActivity {
    private File myCacheFile;
    private String data = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_junk);

        myCacheFile = new File(getExternalCacheDir(), "test.txt");

        TextView textView = findViewById(R.id.control_text);

        Button createBtn = findViewById(R.id.create_btn);
        createBtn.setOnClickListener(v ->{
            try {
                FileOutputStream fos = new FileOutputStream(myCacheFile);
                fos.write("Hello, File!".getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        Button readFileBtn = findViewById(R.id.read_file_btn);
        readFileBtn.setOnClickListener(v ->{
            try {
                FileInputStream fis = new FileInputStream(myCacheFile);
                DataInputStream dis = new DataInputStream(fis);
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(dis));
                String line;
                while ((line = reader.readLine()) != null){
                    data += line;
                }
                textView.setText(data);
                fis.close();
                dis.close();
                reader.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        Button readCsv = findViewById(R.id.cvs_btn_reader);
        readCsv.setOnClickListener(v ->{
            try {
                File file = new File(getExternalCacheDir(), "news.csv");
                CsvParser parser = new CsvParser();
                List<Article> articles = parser.parse(file);
                textView.setText(articles.get(1).getTitle());

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
