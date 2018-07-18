package com.sktb.isyan;

import java.util.Random;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

@SuppressLint("InlinedApi") public class MainActivity extends AppCompatActivity {
	protected WebView webView;
	protected boolean doubleBackToExitPressedOnce = false;
	public int userId = 0;
	protected JSInterface jsInterface = null;

	protected final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 0x0001;
	public void UiChangeListener()
    {
        final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode != Activity.RESULT_OK || intent == null)
        {
        	return;
        }

        switch(requestCode) {
        case 1099:
        	Uri selectedImage = intent.getData();
        	final String fileName = FilePath.getPath(this,selectedImage);
        	//final String fileName = selectedImage.getPath();

        	ProgressDialog pd = ProgressDialog.show(this, "", "Lütfen bekleyin...", true);
        	UploadPicture uploader = new UploadPicture();
        	uploader.execute(fileName, userId);
        	
        	testIfUploaderIsFinished(uploader, pd);

        	break;
        }
    }

    protected void testIfUploaderIsFinished(final UploadPicture uploader, final ProgressDialog pd)
    {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(uploader.busy)
				{
					testIfUploaderIsFinished(uploader, pd);
				} else {
			        webView.loadUrl("javascript:stopUpload("+ (uploader.resultCode == 200 ? 1 : 0) +")");
		            pd.dismiss();
				}
			}
        }, 100);
    	
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Do something with granted permission
            //jsInterface.getScanningResults();
        }
    }

    @SuppressLint({ "JavascriptInterface", "InlinedApi" }) @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        final View decorView = this.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        
        UiChangeListener();

        setContentView(R.layout.activity_main);
        String url = "http://www.isyanyayinlari.com/app_index.php?v=" + new Random().nextInt();
        webView = (WebView) findViewById(R.id.webView);
        //webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        jsInterface = new JSInterface(this, webView);

        if(Build.VERSION.SDK_INT >= 23){
            int permission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if(permission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.CHANGE_NETWORK_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }
        }

        webView.addJavascriptInterface(jsInterface, "sktb");
        webView.setWebViewClient(new WebViewClient(){
        	   public void onPageFinished(WebView view, String url) {
        	   }
        });
        final Context context = this;
        webView.setWebChromeClient(
    		new WebChromeClient() {

    		    @Override
                public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                   AlertDialog.Builder b = new AlertDialog.Builder(context)
                   .setTitle("İsyan Kitaplığı")
                   .setMessage(message)
                   .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           result.confirm();
                       }
                   })
                   .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           result.cancel();
                       }
                   });

                   b.show();

                   // Indicate that we're handling this manually
                   return true;
                }
    		    @Override
    		    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
    		        callback.invoke(origin, true, false);
    		    }
                @Override
                public boolean onJsAlert(WebView view, String url, String message, final JsResult result)
                {
                    AlertDialog.Builder b = new AlertDialog.Builder(context)
                    .setTitle("İsyan Kitaplığı")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    });

                    b.show();
                    return true;
                }
                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);

                    WebView webView = (WebView) findViewById(R.id.webView);

                    boolean couldBeLoaded = webView.getTitle().indexOf("404") == -1;

                    CharSequence pnotfound = "The page cannot be found";
                    if (title.contains("The page cannot be found") || title.contains("Web sayfası mevcut değil")) {
                        ((RelativeLayout) findViewById(R.id.layout2)).setVisibility(View.VISIBLE);
                        webView.setVisibility(View.GONE);
                        ((RelativeLayout)findViewById(R.id.layout1)).setVisibility(View.GONE);
                        ((RelativeLayout)findViewById(R.id.layout2)).setVisibility(View.VISIBLE);
                    } else {
                        //if(!title.equals("") && !title.contains("index.")) {
                            webView.setVisibility(View.VISIBLE);

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    RelativeLayout img = (RelativeLayout) findViewById(R.id.layout1);
                                    img.setVisibility(View.INVISIBLE);
                                }
                            }, 100);
                        //}
                    }
                }
    		}
		);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setUserAgentString("SKTB");
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            //NOTE: this is required only for Android 4.2.2+
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }

        webView.loadUrl(url);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if(jsInterface.getStartOfASection())
        {
            webView.loadUrl("javascript:goBackFromBook()");
            jsInterface.setStartOfASection(false);
            return;
        }

        // Pop the browser back stack or exit the activity
	    if (doubleBackToExitPressedOnce) {
			this.finish();
			System.exit(0);
	    }

	    this.doubleBackToExitPressedOnce = true;
	    Toast.makeText(MainActivity.this, "Çıkmak için tekrar geri tuşuna basın", Toast.LENGTH_LONG).show();
	    new Handler().postDelayed(new Runnable() {
	        @Override
	        public void run() {
	            doubleBackToExitPressedOnce=false;                       
	        }
	    }, 2000);			
    }
}
