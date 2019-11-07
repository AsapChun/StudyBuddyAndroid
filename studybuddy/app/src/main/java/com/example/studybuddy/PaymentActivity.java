package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.StripeIntent;
import com.stripe.android.model.StripePaymentSource;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;


import java.util.HashMap;
import java.util.Map;


public class PaymentActivity extends AppCompatActivity {

    private CardMultilineWidget cmw;
    private Card cardToSave;
    private Button btnSaveCard;
    private Button btnBack;
    private Button btnPayment;
    private EditText edtPay;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        cmw = (CardMultilineWidget) findViewById(R.id.card_multiline_widget);
        btnSaveCard = (Button) findViewById(R.id.btnSave);
        btnBack = (Button) findViewById(R.id.btnReturn);
        btnPayment = (Button) findViewById(R.id.btnPay);
        edtPay = (EditText) findViewById(R.id.edtPayment);


        btnSaveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCard();
            }
        });

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Card card =  cmw.getCard();
                if(card == null){
                    Toast.makeText(getApplicationContext(),"Save a Card First",Toast.LENGTH_SHORT).show();
                }
                else{
                    System.out.println("Card Not Null");
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

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(v);
            }
        });
    }

    private void saveCard() {

        Card card =  cmw.getCard();
        if(card == null){
            Toast.makeText(getApplicationContext(),"Invalid card",Toast.LENGTH_SHORT).show();
        }else {
            if (!card.validateCard()) {
                // Do not continue token creation.
                Toast.makeText(getApplicationContext(), "Invalid card", Toast.LENGTH_SHORT).show();
            } else {
                CreateToken(card);
                cardToSave = card;
            }
        }
    }
    private Token cc;

    private void CreateToken( Card card) {
        Stripe stripe = new Stripe(getApplicationContext(), "pk_test_zpzYaEYYjWapZrdYSBTbkG5x00ZQGlGiST");
        stripe.createToken(
                card,
                new ApiResultCallback<Token>() {
                    public void onSuccess(Token token) {

                        // Send token to your server
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


    public void goBack(View v) {
        this.finish();
    }

}
