package ch.hearc.viewlogo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.Vector;

import ch.hearc.viewlogo.tools.FeatureLogo;
import ch.hearc.viewlogo.tools.Logo;
import ch.hearc.viewlogo.tools.LogoDAO;
import mpi.cbg.fly.Feature;
import mpi.cbg.fly.PointMatch;
import mpi.cbg.fly.SIFT;
import mpi.cbg.fly.TModel2D;

public class AddLogoActivity extends AppCompatActivity {

    public static final String NEW_LOGO = "ch.hearc.viewlogo.newlogo";
    private static int RESULT_LOAD_IMAGE = 10;
    private static final int IMAGE_OK = 0;
    private static final int ERROR_MEMORY = 1;
    private static final int ERROR = 2;

    private EditText etTitle;
    private ImageView imgLogo;
    private Logo logo;

    private Bitmap mPicture;
    private ProgressDialog mProgress;

    private ArrayAdapter<Logo> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_logo);

        this.etTitle = (EditText) findViewById(R.id.etTitleLogo);
        this.imgLogo = (ImageView) findViewById(R.id.imgviewLogoPreload);

        this.logo = new Logo();

        // Show logo
        LogoDAO datasource = new LogoDAO(this);
        datasource.open();

        List<Logo> values = datasource.getAllLogos();
        adapter = new ArrayAdapter<Logo>(this, android.R.layout.simple_list_item_1, values);

        /*final ListView listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(adapter);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_logo, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK)
        {
            if(mPicture != null) mPicture.recycle();

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndex(filePathColumn[0]);
            cursor.moveToFirst();

            String filename = cursor.getString(column_index);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            options.inScaled = true;

            mPicture = BitmapFactory.decodeFile(filename, options);

            Bitmap pic = mPicture.copy(mPicture.getConfig(), true);
            mPicture.recycle();
            mPicture = pic;

            imgLogo.setImageBitmap(mPicture);

            this.logo.setImage(filename);

            cursor.close();
            processAlgoSift();
        }
    }

    public void addNewLogo(View view)
    {
        this.logo.setTitle(etTitle.getText().toString());
        if(!this.logo.getImage().isEmpty() && !this.logo.getTitle().isEmpty())
        {
            LogoDAO logoDAO = new LogoDAO(this);
            logoDAO.ajouter(logo);

            // Show logo
            adapter.add(logoDAO.selectionner(logo.getId()));
            adapter.notifyDataSetChanged();

            this.finish();
        }
        else
        {
            Toast.makeText(this, "Veuillez sélectionner un logo", Toast.LENGTH_SHORT).show();
        }
    }

    public void searchNewLogo(View view)
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    private void processAlgoSift()
    {
        mProgress = ProgressDialog.show(this, "Please wait",
                "Algorithme SIFT en progression...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = null;

                try
                {
                    int[] pixels = bitmapToInt(mPicture);
                    Vector<Feature> features = SIFT.getFeatures(mPicture.getWidth(), mPicture.getHeight(), pixels);
                    FeatureLogo fl;

                    for(Feature f : features)
                    {
                        fl = new FeatureLogo(-1, f.location[0], f.location[1], f.scale, f.orientation, -1);
                        logo.addFeatureLogo(fl);
                    }

                    msg = mHandler.obtainMessage(IMAGE_OK);
                }
                catch (Exception e)
                {
                    msg = mHandler.obtainMessage(ERROR);
                }
                catch (OutOfMemoryError e)
                {
                    msg = mHandler.obtainMessage(ERROR_MEMORY);
                }
                finally {
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    private int[] bitmapToInt(Bitmap src)
    {
        int w = src.getWidth();
        int h = src.getHeight();

        int[] pixels = new int[w * h];

        src.getPixels(pixels, 0, src.getWidth(), 0, 0, w, h);

        for (int i = 0; i < w * h; i++)
        {
            pixels[i] &= 0x00ffffff;
        }

        return pixels;
    }

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            AlertDialog.Builder builder;

            switch (msg.what)
            {
                case IMAGE_OK:
                    imgLogo.setImageBitmap(mPicture);
                    break;
                case  ERROR_MEMORY:
                    builder = new AlertDialog.Builder(AddLogoActivity.this);
                    builder.setMessage("Mémoire insuffisante. Image trop grande");
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                    break;
                case ERROR:
                    builder = new AlertDialog.Builder(AddLogoActivity.this);
                    builder.setMessage("Erreur durant le processus");
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                    break;
            }

            mProgress.dismiss();
        }
    };
}
