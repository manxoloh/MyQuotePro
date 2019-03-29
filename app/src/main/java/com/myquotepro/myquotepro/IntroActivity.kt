package com.myquotepro.myquotepro

import agency.tango.materialintroscreen.MaterialIntroActivity
import agency.tango.materialintroscreen.MessageButtonBehaviour
import agency.tango.materialintroscreen.SlideFragmentBuilder
import android.os.Bundle
import android.view.View

class IntroActivity : MaterialIntroActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlide(SlideFragmentBuilder()
            .backgroundColor(R.color.colorPrimary)
            .buttonsColor(R.color.colorAccent)
            .image(agency.tango.materialintroscreen.R.drawable.ic_next)
            .title("title 3")
            .description("Description 3")
            .build(),
            MessageButtonBehaviour(
                View.OnClickListener { showMessage("We provide solutions to make you love your work") },
                "Work with love"
            )
        )
    }
}