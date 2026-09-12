package com.aprovaplus.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {
 private static final int FILE_CHOOSER_REQUEST = 1101;
 private WebView webView;
 private ValueCallback<Uri[]> filePathCallback;
 @Override protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  webView = new WebView(this); webView.setOverScrollMode(View.OVER_SCROLL_NEVER); setContentView(webView);
  WebSettings settings = webView.getSettings(); settings.setJavaScriptEnabled(true); settings.setDomStorageEnabled(true); settings.setDatabaseEnabled(true); settings.setAllowFileAccess(true); settings.setAllowContentAccess(true); settings.setBuiltInZoomControls(false); settings.setDisplayZoomControls(false); settings.setSupportZoom(false); settings.setLoadWithOverviewMode(true); settings.setUseWideViewPort(true); settings.setMediaPlaybackRequiresUserGesture(false);
  webView.setWebViewClient(new WebViewClient());
  webView.setWebChromeClient(new WebChromeClient(){ @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params){ if(filePathCallback!=null) filePathCallback.onReceiveValue(null); filePathCallback=callback; try { startActivityForResult(params.createIntent(), FILE_CHOOSER_REQUEST); } catch(ActivityNotFoundException e){ filePathCallback=null; Toast.makeText(MainActivity.this,"Não foi possível abrir o seletor de arquivos.",Toast.LENGTH_SHORT).show(); return false; } return true; }});
  if(savedInstanceState==null) webView.loadUrl("file:///android_asset/index.html"); else webView.restoreState(savedInstanceState);
 }
 @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){ super.onActivityResult(requestCode,resultCode,data); if(requestCode==FILE_CHOOSER_REQUEST && filePathCallback!=null){ Uri[] result=null; if(resultCode==RESULT_OK && data!=null && data.getData()!=null) result=new Uri[]{data.getData()}; filePathCallback.onReceiveValue(result); filePathCallback=null; }}
 @Override protected void onSaveInstanceState(Bundle outState){ webView.saveState(outState); super.onSaveInstanceState(outState); }
 @Override public void onBackPressed(){ if(webView!=null && webView.canGoBack()) webView.goBack(); else super.onBackPressed(); }
 @Override protected void onDestroy(){ if(webView!=null){ webView.destroy(); webView=null; } super.onDestroy(); }
}
