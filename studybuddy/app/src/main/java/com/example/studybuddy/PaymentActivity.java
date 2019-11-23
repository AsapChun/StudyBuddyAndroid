package com.example.studybuddy;


import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.StripeIntent;
import com.stripe.android.model.StripePaymentSource;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;



import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "Payment";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CardMultilineWidget cmw;
    private Card cardToSave;
    private Button btnSaveCard;
    private Button btnBack;
    private Button btnPayment;
    private EditText edtPay;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        //stripe widget
        cmw = (CardMultilineWidget) findViewById(R.id.card_multiline_widget);
        btnSaveCard = (Button) findViewById(R.id.btnSave);
        btnBack = (Button) findViewById(R.id.btnReturn);
        btnPayment = (Button) findViewById(R.id.btnPay);
        edtPay = (EditText) findViewById(R.id.edtPayment);
        mAuth = FirebaseAuth.getInstance();

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
                Card card =  cmw.getCard(); //stripe widget
                if(card == null){
                    Toast.makeText(getApplicationContext(),"Save a Card First",Toast.LENGTH_SHORT).show();
                }
                else{
                    String userUid = mAuth.getCurrentUser().getUid();


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

                    Toast.makeText(getApplicationContext(),"Payment Complete",Toast.LENGTH_SHORT).show();


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

        Card card =  cmw.getCard(); //gets the card from the stripe cmw widgfet
        if(card == null){
            Toast.makeText(getApplicationContext(),"Invalid card",Toast.LENGTH_SHORT).show();
        }else {
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
    private void CreateToken( Card card) {
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
                        intent.putExtra("card",token.getCard().getLast4());
                        intent.putExtra("stripe_token",token.getId());
                        intent.putExtra("cardtype",token.getCard().getBrand());
                        setResult(0077,intent);
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

}

