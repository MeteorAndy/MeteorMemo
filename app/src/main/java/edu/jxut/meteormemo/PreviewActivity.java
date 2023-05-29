package edu.jxut.meteormemo;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class PreviewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        // 初始化Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // 获取传递过来的文件名
        String fileName = getIntent().getStringExtra("file_name");

        // 设置Toolbar标题为文件名
        getSupportActionBar().setTitle(fileName);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));

        // 初始化WebView
        webView = findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient());

        // 设置WebView背景颜色和文字颜色
        setWebViewTheme();

        // 解析并渲染Markdown文件
        renderMarkdownFile(fileName);
    }

    // 设置WebView的背景颜色和文字颜色
    private void setWebViewTheme() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            String nightBackgroundColor = "#1c1b1f";
            webView.setBackgroundColor(parseColor(nightBackgroundColor));
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            String normalBackgroundColor = "#fffbfe";
            webView.setBackgroundColor(parseColor(normalBackgroundColor));
            webView.setLayerType(View.LAYER_TYPE_NONE, null);
        }
    }

    // 解析并渲染Markdown文件
    private void renderMarkdownFile(String fileName) {
        if (!TextUtils.isEmpty(fileName)) {
            File file = new File(getFilesDir(), fileName);
            if (file.exists() && file.isFile()) {
                try {
                    // 读取Markdown文件内容
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        sb.append("\n");
                    }
                    reader.close();
                    String markdownContent = sb.toString();

                    // 解析Markdown内容
                    Parser parser = Parser.builder().build();
                    Node document = parser.parse(markdownContent);

                    // 渲染Markdown内容为HTML
                    HtmlRenderer renderer = HtmlRenderer.builder().build();
                    String htmlContent = renderer.render(document);

                    // 使用CSS样式覆盖WebView的默认样式
                    String cssStyle = "<style>* { color: " + getTextColor() + "; }</style>";
                    htmlContent = cssStyle + htmlContent;

                    // 加载渲染后的HTML内容到WebView
                    webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 获取文字颜色
    private String getTextColor() {
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            return "#FFFFFF"; // 白色
        } else {
            return "#000000"; // 黑色
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setWebViewTheme();
    }

    private int parseColor(String colorString) {
        return Color.parseColor(colorString);
    }
}
