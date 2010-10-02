/*
 * Copyright 2010 Peter Vegh <peter.vegh@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.damazio.notifier.locale.notify;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Helper activity which shows instructions on how to get the plugin
 * working with locale and/or tasker.
 *
 * @author Peter Vegh
 */
public class MainHelperActivity extends Activity implements OnClickListener {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.helper);

    Button notifierButton = (Button) findViewById(R.id.button_get_notifier);
    notifierButton.setOnClickListener(this);

    Button taskerButton = (Button) findViewById(R.id.button_get_tasker);
    taskerButton.setOnClickListener(this);

    Button localeButton = (Button) findViewById(R.id.button_get_locale);
    localeButton.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    String url = "";

    switch (v.getId()) {
      case R.id.button_get_notifier:
        url = "market://details?id=org.damazio.notifier";
        break;
      case R.id.button_get_tasker:
        url = "market://details?id=net.dinglisch.android.taskerm";
        break;
      case R.id.button_get_locale:
        url = "market://details?id=com.twofortyfouram.locale";
        break;
      default:
        return;
    }

    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(url));
    startActivity(i);
  }
}