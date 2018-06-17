package com.francescosilvetti.funreader;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Layout;
import android.text.TextPaint;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import android.text.StaticLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {

    Button btnEncrypt, btnClipboard, btnShare;
    EditText edtInput, edtOutput;
    SeekBar sdrDifficulty;
    Encriptor encriptor = new Encriptor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        setContentView(R.layout.activity_main);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        this.btnEncrypt = findViewById(R.id.btnEncrypt);
        this.btnClipboard = findViewById(R.id.btnClipboard);
        this.btnShare = findViewById(R.id.btnShare);

        this.edtInput = findViewById(R.id.edtTextInput);
        this.edtOutput = findViewById(R.id.edtTextOutput);

        this.sdrDifficulty = findViewById(R.id.sdrDifficulty);

        this.btnEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encrypt(v);
            }
        });
        this.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(v);
            }
        });
        this.btnClipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard(v);
            }
        });

    }

    private void encrypt(View v) {
        this.edtOutput.setText(this.encriptor.getEncripted(this.edtInput.getText().toString(), this.sdrDifficulty.getProgress(), getApplicationContext()));
    }

    private void share(View v) {
        this.createPdf(this.edtOutput.getText().toString());
    }

    public void createPdf(String text) {
        // create a new document
        PdfDocument document = new PdfDocument();

        // crate a page description
        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(210, 297, 1).create();

        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(3);
        paint.setTextAlign(Paint.Align.LEFT);
        TextPaint txtPaint = new TextPaint();
        txtPaint.set(paint);

        StaticLayout layout = new StaticLayout(text, txtPaint, 210, Layout.Alignment.ALIGN_CENTER, 1, 1, false);
        layout.getOffsetToLeftOf(10);
        layout.getOffsetToLeftOf(10);
        Canvas canvas = page.getCanvas();
        /*
        canvas.drawRect(new Rect(100,100,100,100), new Paint());
        canvas.drawText(text, 10, 10, 10,10, paint);
        */
        layout.draw(canvas);

        // finish the page
        document.finishPage(page);

        // write the document content
        File fol = new File(getApplicationContext().getExternalFilesDir(null), "Lengua.pdf");

        try {
            document.writeTo(new FileOutputStream(fol));

        } catch (IOException e) {
            e.printStackTrace();
        }

        // close the document
        document.close();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("document/pdf");

        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fol));
        startActivity(Intent.createChooser(intent, "Share PDF using:"));

        //Toast.makeText(getApplicationContext(), "This feature is not yet supported...", Toast.LENGTH_LONG).show();

    }

    private void copyToClipboard(View v) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Text", this.edtOutput.getText());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "Succesfully copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}
