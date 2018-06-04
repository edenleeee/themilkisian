package com.example.leeso.themilkisian;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users");

    @BindView(R.id.nameET)
    EditText nameET;
    @BindView(R.id.emailET)
    EditText emailET;
    @BindView(R.id.passwordET)
    EditText passwordET;
    @BindView(R.id.phoneNumberET)
    EditText phoneNumberET;
    @BindView(R.id.signUpBtn)
    Button signUpBtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameET.getText().toString().equals("") || emailET.getText().toString().equals("") || passwordET.getText().toString().equals("") || phoneNumberET.getText().toString().equals("") || phoneNumberET.getText().toString().equals(null)){
                    Toast.makeText(view.getContext(), "Please fill in the Information", Toast.LENGTH_SHORT).show();
                }else {
                    final ProgressDialog progressDialog = ProgressDialog.show(view.getContext(), "Please wait...", "Processing...", true);
                    firebaseAuth.createUserWithEmailAndPassword(emailET.getText().toString(),passwordET.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(nameET.getText().toString()).setPhotoUri(Uri.parse(phoneNumberET.getText().toString()))
                                                    .build();
                                        firebaseUser.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @RequiresApi(api = Build.VERSION_CODES.N)
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                                                    FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                                                    Map<String, Object> result = new HashMap<>();
                                                    result.put("uid", firebaseUser.getUid());
                                                    result.put("token", FirebaseInstanceId.getInstance().getToken());
                                                    result.put("name",nameET.getText().toString());
                                                    result.put("email",emailET.getText().toString());
                                                    result.put("phone",phoneNumberET.getText().toString());

                                                    myRef.child(mCurrentUser.getUid()).setValue(result);

                                                    Toast.makeText(view.getContext(), "Successfully", Toast.LENGTH_SHORT).show();
                                                    Intent i = new Intent(view.getContext(), LoginActivity.class);
                                                    i.putExtra("Name",nameET.getText().toString());
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(i);
                                                }
                                            }
                                        });



                                        }
                                    }
                                });
                            }
                            else {
                                Log.e("ERROR", task.getException().toString());
                                Toast.makeText(view.getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
    // TODO: Rename method, update argument and hook method into UI event

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
    @Override
    public void onDetach() {
        super.onDetach();

    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name

    }
}
