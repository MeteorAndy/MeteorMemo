package edu.jxut.meteormemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> fileNames; // 文件名列表
    private ArrayAdapter<String> fileAdapter; // 文件名列表的适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化文件列表
        ListView fileListView = findViewById(R.id.file_list_view);
        fileNames = new ArrayList<>();
        fileAdapter = new ArrayAdapter<>(this, R.layout.file_list_item, fileNames);
        fileListView.setAdapter(fileAdapter);

        // 设置文件列表点击事件
        fileListView.setOnItemClickListener((parent, view, position, id) -> {
            String fileName = fileNames.get(position);
            // 打开选中的Markdown文件
            openMarkdownFile(fileName);
        });

        // 设置文件列表长按事件
        fileListView.setOnItemLongClickListener((parent, view, position, id) -> {
            String fileName = fileNames.get(position);
            // 显示文件操作菜单
            showFileMenu(view, fileName);
            return true;
        });

        // 设置FloatingActionButton点击事件
        FloatingActionButton fab = findViewById(R.id.fab);
        // 显示PopMenu
        fab.setOnClickListener(this::showPopupMenu);

        // 在onCreate方法中刷新文件列表
        refreshFileList();
    }

    // 显示文件操作菜单
    private void showFileMenu(View view, final String fileName) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.file_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_delete) {
                // 删除文件
                deleteMarkdownFile(fileName);
                return true;
            } else if (item.getItemId() == R.id.menu_rename) {
                // 重命名文件
                showRenameFileDialog(fileName);
                return true;
            } else if (item.getItemId() == R.id.menu_preview) {
                // 预览文件
                previewMarkdownFile(fileName);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    // 显示PopMenu
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_create_file) {
                // 创建文件
                showCreateFileDialog();
                return true;
            } else if (item.getItemId() == R.id.menu_settings) {
                // 跳转到设置Activity
                goToSettingsActivity();
                return true;
            } else if (item.getItemId() == R.id.menu_help) {
                // 跳转到帮助Activity
                goToHelpActivity();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    // 显示创建文件对话框
    private void showCreateFileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("创建文件");
        builder.setMessage("输入文件名:");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("创建", (dialog, which) -> {
            String fileName = input.getText().toString();
            // 创建Markdown文件
            createMarkdownFile(fileName);
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // 显示重命名文件对话框
    private void showRenameFileDialog(final String oldFileName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("重命名文件");
        builder.setMessage("输入新的文件名:");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("重命名", (dialog, which) -> {
            String newFileName = input.getText().toString();
            // 重命名Markdown文件
            renameMarkdownFile(oldFileName, newFileName);
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // 创建Markdown文件
    private void createMarkdownFile(String fileName) {
        // 在文件系统中创建对应的Markdown文件
        File file = new File(getFilesDir(), fileName);
        try {
            boolean created = file.createNewFile();
            if (created) {
                Toast.makeText(this, "成功创建Markdown文件", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Markdown文件已存在", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "创建Markdown文件失败", Toast.LENGTH_SHORT).show();
        }

        // 刷新文件列表
        refreshFileList();
    }

    // 删除Markdown文件
    private void deleteMarkdownFile(String fileName) {
        // 从文件系统中删除对应的Markdown文件
        File file = new File(getFilesDir(), fileName);
        boolean deleted = file.delete();

        if (deleted) {
            Toast.makeText(this, "成功删除Markdown文件", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "删除Markdown文件失败", Toast.LENGTH_SHORT).show();
        }

        // 刷新文件列表
        refreshFileList();
    }

    // 重命名Markdown文件
    private void renameMarkdownFile(String oldFileName, String newFileName) {
        // 在文件系统中重命名Markdown文件
        File oldFile = new File(getFilesDir(), oldFileName);
        File newFile = new File(getFilesDir(), newFileName);

        boolean renamed = oldFile.renameTo(newFile);

        if (renamed) {
            Toast.makeText(this, "成功重命名Markdown文件", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "重命名Markdown文件失败", Toast.LENGTH_SHORT).show();
        }

        // 刷新文件列表
        refreshFileList();
    }

    // 打开Markdown文件
    private void openMarkdownFile(String fileName) {
        // 构建文件路径
        File file = new File(getFilesDir(), fileName);

        if (file.exists()) {
            // 读取文件内容
            StringBuilder contentBuilder = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    contentBuilder.append(line).append("\n");
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "无法打开Markdown文件", Toast.LENGTH_SHORT).show();
                return;
            }

            // 打开文件编辑Activity
            Intent intent = new Intent(this, EditFileActivity.class);
            intent.putExtra("fileName", fileName);
            intent.putExtra("fileContent", contentBuilder.toString());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Markdown文件不存在", Toast.LENGTH_SHORT).show();
        }
    }

    // 刷新文件列表
    private void refreshFileList() {
        // 获取文件列表
        File directory = getFilesDir();
        File[] files = directory.listFiles();
        if (files != null) {
            fileNames.clear();
            for (File file : files) {
                fileNames.add(file.getName());
            }
            fileAdapter.notifyDataSetChanged();
        }
    }

    // 跳转到预览Activity
    private void previewMarkdownFile(String fileName) {
        // 创建Intent并指定目标Activity
        Intent intent = new Intent(MainActivity.this, PreviewActivity.class);

        // 将选中的md文件名作为参数传递到预览Activity
        intent.putExtra("file_name", fileName);

        // 启动预览Activity
        startActivity(intent);
    }

    // 跳转到设置Activity
    private void goToSettingsActivity() {
        // 创建Intent对象，指定要跳转的Activity
        Intent intent = new Intent(this, SettingsActivity.class);

        // 添加任何需要传递给SettingsActivity的额外数据
        // intent.putExtra("key", value);

        // 调用startActivity方法开始跳转
        startActivity(intent);
    }

    // 跳转到帮助Activity
    private void goToHelpActivity() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }
}
