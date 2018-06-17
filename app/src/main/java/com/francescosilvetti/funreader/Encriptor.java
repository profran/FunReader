package com.francescosilvetti.funreader;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;

public class Encriptor {

    private Scanner scanner = new Scanner(System.in);
    private String encripted = "";
    private JSONObject json;

    public String getEncripted(String texto, int difficulty, Context ctx) {
        try {
            this.json = (JSONObject) new JSONParser().parse(getJSONString(ctx));
            texto = texto.toLowerCase();
            this.encripted = "";
            for (String i : texto.split("")) {
                if (new Random().nextInt(10) + 1 <= difficulty) {
                    this.getJson(i, ctx);
                } else {
                    this.encripted += i;
                }
            }

            return this.encripted;
        } catch (ParseException ex) {
            Logger.getLogger(Encriptor.class.getName()).log(Level.SEVERE, null, ex);
            return ex.toString();
        }
    }

    private void getJson(String i, Context ctx) {

        if (this.json.get(i) != null) {

            this.encripted += this.json.get(i);

        } else {

            this.encripted += i;

        }
    }

    private String getJSONString(Context context) {
        String str = "";
        try {
            AssetManager assetManager = context.getAssets();
            InputStream in = assetManager.open("dictionary.json");
            InputStreamReader isr = new InputStreamReader(in);
            char[] inputBuffer = new char[150];

            int charRead;
            while ((charRead = isr.read(inputBuffer)) > 0) {
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                str += readString;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return str;
    }

    public void toPdf(String texto) {
        String extstoragedir = Environment.getExternalStorageDirectory().toString();
        File fol = new File(extstoragedir, "pdf");
        File folder = new File(fol, "pdf");
        if (!folder.exists()) {
            boolean bool = folder.mkdir();
        }

        PDDocument document = new PDDocument();
        PDPage blankPage = new PDPage();
        document.addPage(blankPage);

        PDFont font = PDType1Font.HELVETICA_BOLD;
        try {
            final File file = new File(folder, "Lengua.pdf");
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);

            PDPageContentStream contentStream = new PDPageContentStream(document, blankPage);

            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.moveTextPositionByAmount(100, 700);
            contentStream.drawString(texto);
            contentStream.endText();

            contentStream.close();

            document.save(fOut);
            document.close();
        } catch (FileNotFoundException ex) {
            System.out.print(ex);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}