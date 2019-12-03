package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studybuddy.Model.Appointment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "Payment";
    public static final String Appointment = "Appointment";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public static final String StudentId = "StudentId";
    public static final String ValidAppointment = "validAppointment";

    private CardMultilineWidget cmw;
    private Card cardToSave;
    private Button btnSaveCard;
    private Button btnBack;
    private Button btnPayment;
    private EditText edtPay;
    private Appointment app;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Intent intent = getIntent();
        app = (Appointment) intent.getSerializableExtra(Appointment);

        //firebase init
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //stripe widget
        cmw = (CardMultilineWidget) findViewById(R.id.card_multiline_widget);
        btnSaveCard = (Button) findViewById(R.id.btnSave);
        btnBack = (Button) findViewById(R.id.btnReturn);
        btnPayment = (Button) findViewById(R.id.btnPay);
        edtPay = (EditText) findViewById(R.id.edtPayment);

        //Save a card using the Stripe Api
        //Calls the saveCard function
        btnSaveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCard();
            }
        });

        //Payment button
        //shpuld only validate card if a card has been saved in the cmw widget
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Card card = cmw.getCard(); //stripe widget
                if (card == null) {
                    Toast.makeText(getApplicationContext(), "Save a Card First", Toast.LENGTH_SHORT).show();
                } else {

                    //Update Appointment
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();

                        Map<String, Object> result = new HashMap<>();
                        result.put(StudentId, userId);
                        result.put(ValidAppointment, true);

                        db.collection(Appointment)
                                .document(app.getAppId())
                                .update(result)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Payment Complete...", Toast.LENGTH_SHORT).show();
                                        goToHomePage();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Error Paying ...", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }

                    //Stripe payments will be implemented here once a server has been created to handle payments
                   /* Stripe.apiKey = ;

                    // Token is created using Checkout or Elements!
                    // Get the payment token ID submitted by the form:
                    String token = request.getParameter("stripeToken");

                    Map<String, Object> params = new HashMap<>();
                    params.put("amount", edtPay.getText().toString());
                    params.put("currency", "usd");
                    params.put("description", "Test charge");
                    params.put("source", token);

                    Charge charge = Charge.create(params);

                    */

                    //Toast.makeText(getApplicationContext(),"Payment Complete",Toast.LENGTH_SHORT).show();


                }

            }
        });

        //return to login activity
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(v);
            }
        });
    }

    //Called by clicking the save card button
    private void saveCard() {
        //save card to Customer

        Card card = cmw.getCard(); //gets the card from the stripe cmw widgfet
        if (card == null) {
            Toast.makeText(getApplicationContext(), "Invalid card", Toast.LENGTH_SHORT).show();
        } else {
            if (!card.validateCard()) { //Stripe function that checks whether if inputted card is valid
                // Do not continue token creation.
                Toast.makeText(getApplicationContext(), "Invalid card", Toast.LENGTH_SHORT).show();
            } else {
                CreateToken(card); //if valid, input the card to Stripe's CreateToken function
                cardToSave = card;
            }
        }
    }

    private Token cc;

    //Stripe function to create new token within the Stripe API
    //token allows us to use and access the newly created card
    private void CreateToken(Card card) {
        Stripe stripe = new Stripe(getApplicationContext(), "pk_test_zpzYaEYYjWapZrdYSBTbkG5x00ZQGlGiST");
        //Our publishable key
        stripe.createToken(
                card,
                new ApiResultCallback<Token>() {
                    public void onSuccess(Token token) {

                        // Send token to your server
                        //^Server has not been implemented yet
                        //But we take the necessary card details to create a card toekn
                        Log.e("Stripe Token", token.getId());
                        Intent intent = new Intent();
                        intent.putExtra("card", token.getCard().getLast4());
                        intent.putExtra("stripe_token", token.getId());
                        intent.putExtra("cardtype", token.getCard().getBrand());
                        setResult(0077, intent);
                        Toast.makeText(getApplicationContext(), "Valid Card", Toast.LENGTH_SHORT).show();


                    }

                    public void onError(Exception error) {

                        // Show localized error message
                        Toast.makeText(getApplicationContext(),
                                error.getLocalizedMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }
/*
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
       }
 */

    public void goBack(View v) {
        this.finish();
    }

    public void goToHomePage() {
        Intent newIntent = new Intent(this, HomePageActivity.class);
        startActivity(newIntent);
    }
}

