package com.viictrp.financeapp

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.viictrp.financeapp.utils.Constantes

class MainActivity : AppCompatActivity() {

    private var actionBarButton: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.navegacao)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navegacao_home,
                R.id.navegacao_carteira,
                R.id.navegacao_cartao
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        supportActionBar?.elevation = Constantes.ZERO_FLOAT
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setCustomView(R.layout.custom_action_bar_layout)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.white)))
        actionBarButton = supportActionBar?.customView?.findViewById(R.id.action_bar_button)
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.nav_host_fragment)
            .navigateUp() || super.onSupportNavigateUp()
    }

    fun displayActionBarButton(@DrawableRes icon: Int, onClickListener: View.OnClickListener) {
        this.actionBarButton?.setOnClickListener(onClickListener)
        this.actionBarButton?.setImageResource(icon)
        this.actionBarButton?.visibility = View.VISIBLE
    }

    fun disableActionBarButton() {
        this.actionBarButton?.setOnClickListener(null)
        this.actionBarButton?.visibility = View.GONE
    }
}
