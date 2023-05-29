package edu.jxut.meteormemo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class EditFileActivity extends AppCompatActivity {

    private EditText fileContentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_file);

        // 设置Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // 获取文件名和内容
        String fileName = getIntent().getStringExtra("fileName");
        String fileContent = getIntent().getStringExtra("fileContent");

        // 设置Toolbar标题为文件名
        getSupportActionBar().setTitle(fileName);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));

        // 获取文件内容的EditText
        fileContentEditText = findViewById(R.id.file_content_edit_text);
        fileContentEditText.setText(fileContent);

        // 根据当前主题设置EditText文字颜色
        updateEditTextTextColor();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 加载菜单布局文件
        getMenuInflater().inflate(R.menu.edit_file_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            // 保存文件
            saveFile();
            return true;
        } else if (id == android.R.id.home) {
            // 返回上一级Activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // 保存文件
    private void saveFile() {
        // 获取文件内容
        String fileContent = fileContentEditText.getText().toString();

        // 获取当前文件名
        String fileName = getIntent().getStringExtra("fileName");

        // 获取文件路径
        File file = new File(getFilesDir(), fileName);

        try {
            // 创建文件写入流
            FileWriter writer = new FileWriter(file);

            // 写入文件内容
            writer.write(fileContent);

            // 关闭流
            writer.close();

            Toast.makeText(this, "文件保存成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "文件保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    // 根据当前主题设置EditText文字颜色
    private void updateEditTextTextColor() {
        int textColor;
        if (isDarkThemeEnabled()) {
            textColor = ContextCompat.getColor(this, android.R.color.white);
        } else {
            textColor = ContextCompat.getColor(this, android.R.color.black);
        }
        fileContentEditText.setTextColor(textColor);
    }

    // 检查是否为夜间模式
    private boolean isDarkThemeEnabled() {
        int nightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        return nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateEditTextTextColor();
    }
}
