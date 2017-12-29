package hfad.com.unitech;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.util.JsonWriter;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;




import com.pdfjet.Cell;
import com.pdfjet.Color;
import com.pdfjet.CoreFont;
import com.pdfjet.Font;
import com.pdfjet.Letter;
import com.pdfjet.PDF;
import com.pdfjet.Page;
import com.pdfjet.Point;
import com.pdfjet.Table;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

   private Detail detail;
   private EditText name;
    private EditText password;
    private EditText email;
    private TextView latitude;
    private TextView longitude;
    JsonWriter writer;
    OutputStream out;
    File file;
    GetLocation getLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

       getLocation= new GetLocation(getApplicationContext());


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        name= (EditText) findViewById(R.id.name);
         password= (EditText) findViewById(R.id.pass);
         email= (EditText) findViewById(R.id.email);
         latitude= (TextView) findViewById(R.id.lat);
         longitude= (TextView) findViewById(R.id.longi);
        latitude.setText((int) getLocation.latitude);
        longitude.setText((int) getLocation.longitude);

        Button button= (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                detail= new Detail();



                detail.setName(name.getText().toString());
                detail.setPassword(password.getText().toString());
                detail.setEmail(email.getText().toString());
                detail.setLatitude(getLocation.latitude);
                detail.setLongitude(getLocation.longitude);
                try {
                    writeJson( detail);
                    createTextFile(detail);
                } catch (IOException e) {

                }






                // do something
            }
        });




    }

    public void createTextFile(Detail detail){

        File exportDir;
        String state = Environment.getExternalStorageState();
//check if the external directory is availabe for writing
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return;
        }
        else {
            exportDir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        }

//if the external storage directory does not exists, we create it
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file;
        file = new File(exportDir, "UniTechData.pdf");

//PDF is a class of the PDFJET library
        PDF pdf = null;
        try {
            pdf = new PDF(new FileOutputStream(file));

            Page page = new Page(pdf, Letter.PORTRAIT);

            Font f1 = new Font(pdf, CoreFont.HELVETICA_BOLD);
            f1.setSize(7.0f);


            Table table = new Table();


            List<List<Cell>> tableData= new ArrayList<List<Cell>>();
            List<Cell> row = new ArrayList<Cell>();
            row.add(new Cell(f1, detail.getName()));
            row.add(new Cell(f1, detail.getPassword()));
            row.add(new Cell(f1, detail.getEmail()));
            row.add(new Cell(f1, detail.getLatitude().toString()));
            row.add(new Cell(f1, detail.getLongitude().toString()));
            tableData.add(row);





            table.setData(tableData, Table.DATA_HAS_2_HEADER_ROWS);

            table.setPosition(70.0f, 30.0f);
            table.setTextColorInRow(6, Color.blue);
            table.setTextColorInRow(39, Color.red);
            table.setFontInRow(26, f1);
            table.removeLineBetweenRows(0, 1);
            table.autoAdjustColumnWidths();
            table.setColumnWidth(0, 120.0f);
            table.rightAlignNumbers();
            int numOfPages = table.getNumberOfPages(page);
            while (true) {
                Point point = table.drawOn(page);
                // TO DO: Draw "Page 1 of N" here
                if (!table.hasMoreData()) {
                    // Allow the table to be drawn again later:
                    table.resetRenderedPagesCount();
                    break;
                }
                page = new Page(pdf, Letter.PORTRAIT);
            }

            pdf.flush();

            Toast.makeText(getApplicationContext(), "PDF created",
                    Toast.LENGTH_LONG).show();

        }      catch (Exception e) {

        }

//instructions to create the pdf file content

        }


    public void writeJson(Detail detail) throws IOException {
        file = new File(getExternalCacheDir(),"unitechJson.json");
        try
        {
           out = new FileOutputStream(file);

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.setIndent("    ");
        jsonFinal(writer, detail);}

    public void jsonFinal(JsonWriter writer, Detail detail) throws IOException{
        writer.beginObject();
        writer.name("name").value(detail.getName());
        writer.name("email").value(detail.getEmail());
        writer.name("laitude").value(detail.getLatitude());
        writer.name("longitude").value(detail.getLongitude());

        writer.endObject();
        writer.close();
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
