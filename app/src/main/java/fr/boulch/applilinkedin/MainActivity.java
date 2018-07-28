package fr.boulch.applilinkedin;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String PACKAGE = "fr.boulch.applilinkedin";
    // Your Package Name<br />
    Button hask_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hask_key = (Button) findViewById(R.id.show_hash);
        //Compute application package and hash
        hask_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateHashkey();
            }
        });
        Button login_linkedin_btn = (Button) findViewById(R.id.login_button);
        login_linkedin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_linkedin();
            }
        });
    }
    // This Method is used to generate &quot;Android Package Name&quot; hash key</p>
    public void generateHashkey(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    PACKAGE,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                ((TextView) findViewById(R.id.package_name)).setText(info.packageName);
                ((TextView) findViewById(R.id.hash_key)).setText(Base64.encodeToString(md.digest(),
                        Base64.NO_WRAP));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, e.getMessage(), e);
        }
    }

    public void login_linkedin(){
        LISessionManager.getInstance(getApplicationContext()).init(this,
                buildScope(),new AuthListener() {
                    @Override
                    public void onAuthSuccess() {
                        Toast.makeText(getApplicationContext(), "succes : "+LISessionManager.getInstance((getApplicationContext())).getSession().getAccessToken(), Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onAuthError(LIAuthError error) {
                        Toast.makeText(getApplicationContext(), "failed"+error.toString(), Toast.LENGTH_LONG).show();
                    }
                }, true);
    }

    // This method is used to make permissions to retrieve data from linkedin</p>
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Add this line to your existing onActivityResult() method
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
    }
}
