package com.myquotepro.myquotepro.payments

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.View
import android.widget.Toast
import com.braintreepayments.cardform.view.CardForm
import com.myquotepro.myquotepro.R
import kotlinx.android.synthetic.main.activity_payments.*


class PaymentsActivity : AppCompatActivity() {
    private var pd: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payments)
        pd = ProgressDialog(this@PaymentsActivity)

        val cardForm = findViewById<View>(R.id.card_form) as CardForm
        cardForm.cvvEditText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        cardForm.cardRequired(true)
            .expirationRequired(true)
            .cvvRequired(true)
            .cardholderName(CardForm.FIELD_REQUIRED)
            .postalCodeRequired(true)
            .mobileNumberRequired(true)
            .mobileNumberExplanation("SMS is required on this number")
            .actionLabel("Confirm")
            .setup(this@PaymentsActivity)

        paymentButton.setOnClickListener {
            if (cardForm.isValid) {
                val alertBuilder = AlertDialog.Builder(this@PaymentsActivity)
                alertBuilder.setTitle("Confirm Payment")
                alertBuilder.setMessage(
                    "Card number: " + cardForm.cardNumber + "\n" +
                            "Card expiry date: " + cardForm.expirationDateEditText.text.toString() + "\n" +
                            "Card CVV: " + cardForm.cvv + "\n" +
                            "Postal code: " + cardForm.postalCode + "\n" +
                            "Phone number: " + cardForm.mobileNumber
                )
                alertBuilder.setPositiveButton(
                    "Confirm"
                ) { dialogInterface, i ->
                    dialogInterface.dismiss()

                    pd!!.show()
                    //Toast.makeText(this@PaymentsActivity, "Thank you for purchase", Toast.LENGTH_LONG).show()
                }
                alertBuilder.setNegativeButton(
                    "Cancel"
                ) { dialogInterface, i -> dialogInterface.dismiss() }
                val alertDialog = alertBuilder.create()
                alertDialog.show()
            } else {
                Toast.makeText(this@PaymentsActivity, "Please complete the form", Toast.LENGTH_LONG).show()
            }
        }
    }
}
