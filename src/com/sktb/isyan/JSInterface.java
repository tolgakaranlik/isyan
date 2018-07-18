package com.sktb.isyan;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class JSInterface {
	protected Context context;
	protected WebView webView;
    protected boolean startOfASection = false;

	public JSInterface(Context context, WebView webView)
	{
		this.context = context;
		this.webView = webView;
    }

	@JavascriptInterface
	public void setStartOfASection()
	{
		startOfASection = true;
	}

    public void setStartOfASection(boolean value)
    {
        startOfASection = value;
    }

    public boolean getStartOfASection()
    {
        return startOfASection;
    }
}