package ch.hearc.viewlogo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;
import java.util.Vector;
import java.util.logging.LogRecord;

import ch.hearc.viewlogo.tools.FeatureLogo;
import ch.hearc.viewlogo.tools.FeatureLogoDAO;
import ch.hearc.viewlogo.tools.Logo;
import ch.hearc.viewlogo.tools.LogoDAO;
import mpi.cbg.fly.Feature;
import mpi.cbg.fly.Model;
import mpi.cbg.fly.PointMatch;
import mpi.cbg.fly.SIFT;
import mpi.cbg.fly.TModel2D;

public class CameraActivity extends AppCompatActivity
{
    private static final int PICTURE_RESULT = 20;
    private static final int LOGO_ADD_RESULT = 15;
    private static final int IMAGE_OK = 0;
    private static final int ERROR_MEMORY = 1;
    private static final int ERROR = 2;

    private Bitmap mPicture;
    private ImageView mView;
    private ProgressDialog mProgress;

    private List<Logo> logos;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            AlertDialog.Builder builder;

            switch (msg.what)
            {
                case IMAGE_OK:
                    mView.setImageBitmap(mPicture);
                    break;
                case  ERROR_MEMORY:
                    builder = new AlertDialog.Builder(CameraActivity.this);
                    builder.setMessage("Mémoire insuffisante. Image trop grande");
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                    break;
                case ERROR:
                    builder = new AlertDialog.Builder(CameraActivity.this);
                    builder.setMessage("Erreur durant le processus");
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                    break;
            }

            mProgress.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        this.mView = (ImageView) findViewById(R.id.imgViewLogo);

        LogoDAO logoDAO = new LogoDAO(this);
        this.logos = logoDAO.getAllLogos();

        for(Logo l : logos)
        {
            Log.i("Test", l.getTitle());
            Log.i("Test", l.getListFeatureLogo().size()+"");
        }
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

        if (id == R.id.camera)
        {
            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera, PICTURE_RESULT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICTURE_RESULT && resultCode == RESULT_OK)
        {
            if(mPicture != null)    mPicture.recycle();

            mPicture = (Bitmap) data.getExtras().get("data");

            Bitmap pic = mPicture.copy(mPicture.getConfig(), true);
            mPicture.recycle();
            mPicture = pic;

            mView.setImageBitmap(mPicture);

            processAlgoSift();
        }
    }

    public void drawFeature(Canvas c, float x, float y, double scale, double orientation)
    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        scale *= 3.;

        double sin = Math.sin(orientation);
        double cos = Math.cos(orientation);

        paint.setStrokeWidth(2f);
        paint.setColor(Color.GREEN);

        c.drawLine(x, y, (float) (x - (sin - cos) * scale), (float) (y + (sin + cos) * scale), paint);

        paint.setStrokeWidth(4f);
        paint.setColor(Color.YELLOW);
        c.drawPoint(x, y, paint);
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

    private void processAlgoSift()
    {
        mProgress = ProgressDialog.show(this, "Please wait",
                "Algorithme SIFT en progression...");

        LogoDAO logoDAO = new LogoDAO(this);
        this.logos = logoDAO.getAllLogos();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = null;

                try
                {
                    Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.volvic);
                    int[] pixels = bitmapToInt(mPicture);
                    Vector<Feature> features = SIFT.getFeatures(mPicture.getWidth(), mPicture.getHeight(), pixels);

                    Canvas c = new Canvas(mPicture);

                    int min = features.size();

                    for(Logo l : logos)
                    {
                        if(min > l.getListFeatureLogo().size())
                        {
                            min = l.getListFeatureLogo().size();
                        }
                    }

                    float distEuclid = 400000000.0f;
                    Logo logoSelect = null;
                    int pointMatch = 0;

                    for(Logo l : logos)
                    {
                        /*Vector<PointMatch> pointMatchVector = SIFT.createMatches(features, l.getListFeatureSift(), 10.0f, null, 10.5f);
                        Log.i("Test", pointMatchVector.size()+"");

                        if(pointMatch < pointMatchVector.size())
                        {
                            pointMatch = pointMatchVector.size();
                            logoSelect = l;
                        }*/
                        float distLogo = 0.0f;

                        for(int i = 0; i < min; i++)
                        {
                            List<FeatureLogo> listFl = l.getListFeatureLogo();

                            for(int j = 0; j < min; j++)
                            {
                                float ixiy = features.get(i).location[0] - listFl.get(j).getX();
                                float jxjy = features.get(i).location[1] - listFl.get(j).getY();
                                float newDistEuclid = (float) Math.sqrt(ixiy * ixiy + jxjy * jxjy);

                                distLogo += newDistEuclid;
                            }
                        }

                        if(distLogo < distEuclid)
                        {
                            distEuclid = distLogo;
                            logoSelect = l;
                        }
                    }

                    if(logoSelect != null) {
                        Log.i("Test", logoSelect.getImage()
                                + " : " + distEuclid + "");
                    }
                    else
                    {
                        Log.i("Test", "Aucun élément correspondant trouvé");
                    }

                    msg = mHandler.obtainMessage(IMAGE_OK);
                }
                catch (Exception e)
                {
                    Log.e("Erreur", e.toString());
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

    public void addNewLogoActivity(View view)
    {
        Intent i = new Intent(this, AddLogoActivity.class);
        startActivity(i);
    }
}
