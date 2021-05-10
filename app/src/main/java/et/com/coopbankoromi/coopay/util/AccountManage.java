package et.com.coopbankoromi.coopay.util;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class AccountManage {
    private final Context mContext;
    private FirebaseAuth mAuth;

    public AccountManage(Context context) {
        mContext = context;
    }
// ...

    public void registerUserWithEmail(String email, String password) {

    }

    public boolean loginUserWithEmail(String email, String password) {

// Initialize Firebase Auth
        boolean[] userAuot = {false};
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        userAuot[0] = task.isSuccessful();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return userAuot[0];
    }
}
