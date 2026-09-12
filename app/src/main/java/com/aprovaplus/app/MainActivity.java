package com.aprovaplus.app;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
 private static final int FILE_CHOOSER_REQUEST = 1101;
 private static final long MIN_SPLASH_MS = 1500;
 private WebView webView;
 private View splashView;
 private ValueCallback<Uri[]> filePathCallback;
 private long splashStarted;
 private boolean splashClosed = false;

 @Override protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  splashStarted = System.currentTimeMillis();

  FrameLayout root = new FrameLayout(this);
  root.setBackgroundColor(Color.parseColor("#0B1220"));

  webView = new WebView(this);
  webView.setAlpha(0f);
  webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
  root.addView(webView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

  splashView = createSplash();
  root.addView(splashView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
  setContentView(root);

  WebSettings settings = webView.getSettings();
  settings.setJavaScriptEnabled(true);
  settings.setDomStorageEnabled(true);
  settings.setDatabaseEnabled(true);
  settings.setAllowFileAccess(true);
  settings.setAllowContentAccess(true);
  settings.setBuiltInZoomControls(false);
  settings.setDisplayZoomControls(false);
  settings.setSupportZoom(false);
  settings.setLoadWithOverviewMode(true);
  settings.setUseWideViewPort(true);
  settings.setMediaPlaybackRequiresUserGesture(false);

  webView.setWebViewClient(new WebViewClient(){
   @Override public void onPageFinished(WebView view, String url){
    super.onPageFinished(view,url);
    long elapsed = System.currentTimeMillis() - splashStarted;
    long wait = Math.max(0, MIN_SPLASH_MS - elapsed);
    new Handler(Looper.getMainLooper()).postDelayed(() -> closeSplash(), wait);
   }
  });

  webView.setWebChromeClient(new WebChromeClient(){
   @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params){
    if(filePathCallback!=null) filePathCallback.onReceiveValue(null);
    filePathCallback=callback;
    try { startActivityForResult(params.createIntent(), FILE_CHOOSER_REQUEST); }
    catch(ActivityNotFoundException e){
     filePathCallback=null;
     Toast.makeText(MainActivity.this,"Não foi possível abrir o seletor de arquivos.",Toast.LENGTH_SHORT).show();
     return false;
    }
    return true;
   }
  });

  if(savedInstanceState==null) webView.loadUrl("file:///android_asset/index.html");
  else { webView.restoreState(savedInstanceState); new Handler(Looper.getMainLooper()).postDelayed(() -> closeSplash(), MIN_SPLASH_MS); }
 }

 private View createSplash(){
  FrameLayout splash = new FrameLayout(this);
  splash.setBackgroundColor(Color.parseColor("#0B1220"));

  LinearLayout content = new LinearLayout(this);
  content.setOrientation(LinearLayout.VERTICAL);
  content.setGravity(Gravity.CENTER);
  content.setPadding(dp(28),dp(28),dp(28),dp(28));
  FrameLayout.LayoutParams contentParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
  splash.addView(content, contentParams);

  TextView mark = new TextView(this);
  mark.setText("A+");
  mark.setTextColor(Color.WHITE);
  mark.setTextSize(38);
  mark.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
  mark.setGravity(Gravity.CENTER);
  GradientDrawable markBg = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{Color.parseColor("#10B981"),Color.parseColor("#047857")});
  markBg.setCornerRadius(dp(24));
  mark.setBackground(markBg);
  LinearLayout.LayoutParams markParams = new LinearLayout.LayoutParams(dp(92),dp(92));
  markParams.bottomMargin=dp(18);
  content.addView(mark,markParams);

  TextView name = new TextView(this);
  name.setText("Aprova+");
  name.setTextColor(Color.WHITE);
  name.setTextSize(33);
  name.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
  name.setGravity(Gravity.CENTER);
  content.addView(name,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

  TextView sub = new TextView(this);
  sub.setText("SEU ESTUDO NO PILOTO AUTOMÁTICO");
  sub.setTextColor(Color.parseColor("#94A3B8"));
  sub.setTextSize(11);
  sub.setGravity(Gravity.CENTER);
  sub.setLetterSpacing(.08f);
  LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
  subParams.topMargin=dp(7);
  content.addView(sub,subParams);

  ProgressBar loader = new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
  loader.setIndeterminate(true);
  loader.getIndeterminateDrawable().setColorFilter(Color.parseColor("#10B981"), PorterDuff.Mode.SRC_IN);
  LinearLayout.LayoutParams loaderParams = new LinearLayout.LayoutParams(dp(155),dp(4));
  loaderParams.topMargin=dp(25);
  content.addView(loader,loaderParams);

  ObjectAnimator sx=ObjectAnimator.ofFloat(mark,View.SCALE_X,.78f,1.06f,1f);
  ObjectAnimator sy=ObjectAnimator.ofFloat(mark,View.SCALE_Y,.78f,1.06f,1f);
  ObjectAnimator a=ObjectAnimator.ofFloat(mark,View.ALPHA,0f,1f);
  ObjectAnimator rise=ObjectAnimator.ofFloat(content,View.TRANSLATION_Y,dp(18),0f);
  ObjectAnimator fade=ObjectAnimator.ofFloat(content,View.ALPHA,0f,1f);
  AnimatorSet intro=new AnimatorSet();
  intro.playTogether(sx,sy,a,rise,fade);
  intro.setDuration(650);
  intro.start();

  ObjectAnimator pulseX=ObjectAnimator.ofFloat(mark,View.SCALE_X,1f,1.035f,1f);
  ObjectAnimator pulseY=ObjectAnimator.ofFloat(mark,View.SCALE_Y,1f,1.035f,1f);
  pulseX.setDuration(1100); pulseY.setDuration(1100);
  pulseX.setRepeatCount(ObjectAnimator.INFINITE); pulseY.setRepeatCount(ObjectAnimator.INFINITE);
  pulseX.start(); pulseY.start();
  return splash;
 }

 private void closeSplash(){
  if(splashClosed||splashView==null||webView==null)return;
  splashClosed=true;
  webView.animate().alpha(1f).setDuration(350).start();
  splashView.animate().alpha(0f).setDuration(420).withEndAction(() -> {
   if(splashView!=null && splashView.getParent() instanceof ViewGroup) ((ViewGroup)splashView.getParent()).removeView(splashView);
   splashView=null;
  }).start();
 }

 private int dp(int v){ return Math.round(v*getResources().getDisplayMetrics().density); }

 @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){
  super.onActivityResult(requestCode,resultCode,data);
  if(requestCode==FILE_CHOOSER_REQUEST && filePathCallback!=null){
   Uri[] result=null;
   if(resultCode==RESULT_OK && data!=null && data.getData()!=null) result=new Uri[]{data.getData()};
   filePathCallback.onReceiveValue(result); filePathCallback=null;
  }
 }
 @Override protected void onSaveInstanceState(Bundle outState){ webView.saveState(outState); super.onSaveInstanceState(outState); }
 @Override public void onBackPressed(){ if(webView!=null && webView.canGoBack()) webView.goBack(); else super.onBackPressed(); }
 @Override protected void onDestroy(){ if(webView!=null){ webView.destroy(); webView=null; } super.onDestroy(); }
}
