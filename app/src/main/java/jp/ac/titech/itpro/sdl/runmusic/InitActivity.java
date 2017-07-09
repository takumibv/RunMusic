package jp.ac.titech.itpro.sdl.runmusic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class InitActivity extends AppCompatActivity {
    private final static String TAG = "InitActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
    }

    public void onClickFeelingButton(View view){
        Log.d(TAG, "a::"+view.getId());
        Intent data = new Intent();
        if(view.getId() == R.id.imageButton1){
            Log.d(TAG, "熱い");
            data.putExtra("feel", "hot");
            setResult(RESULT_OK, data);
            finish();
        }else if(view.getId() == R.id.imageButton2){
            Log.d(TAG, "爽やか");
            data.putExtra("feel", "refresh");
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
