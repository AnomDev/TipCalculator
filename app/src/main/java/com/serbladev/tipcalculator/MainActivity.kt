package com.serbladev.tipcalculator

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.serbladev.tipcalculator.databinding.ActivityMainBinding
import java.text.NumberFormat
import kotlin.math.ceil

class MainActivity : AppCompatActivity() {

     private lateinit var binding: ActivityMainBinding

    // Para generar un binding y evitarnos los findViewById de cada vista se pone \ntodo este bloque
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.calculateBtn.setOnClickListener { calculateTip() }

        // Aquí llamamos al método creado al final de MainActivity para que el teclado se cierre al pulsar una keyCode
        // (en este caso la tecla ENTER)

        binding.costOfServiceTiet.setOnKeyListener { view, keyCode, _ -> handleKeyEvent(view, keyCode)
        }


    }

    private fun calculateTip() {
        val stringInTextField = binding.costOfServiceTiet.text.toString()

        // Para que toDouble() funcione, necesitamos convertir el texto 'editable' del EditText a String, por eso le metemos el toString()
        // ACTUALIZACIÓN:
        // Si dejabamos toDouble() la app crasheaba si el usuario no introducía ningun valor en el EditText, para evitar eso lo cambiamos por
        // toDoubleOrNull() y creamos una estructura de control mediante if...else para comprobar si el usuario ha introducido o no algun valor.
        val cost = stringInTextField.toDoubleOrNull()

        if (cost == null){
            binding.tipResultTv.text = ""
            Toast.makeText(this,  "Please, add the cost of service to continue", Toast.LENGTH_SHORT).show()
            return
        }

        //Aquí elegimos el radiobutton que esta seleccionado pero...
        val selectedId = binding.tipOptionsRg.checkedRadioButtonId

        //...para ello debemos decirle cuál de los tres es el que ha seleccionado el usuario mediante un 'when'
        val tipPercentage = when (selectedId) {
            R.id.option_20_rb -> 0.20
            R.id.option_18_rb -> 0.18
            else -> 0.15
        }
        // En este caso la variable es mutable porque, si el usuario elige la opción de redondear, nosotros deberemos cambiar el valor de tip
        var tip = tipPercentage * cost

        // Comprobamos si está marcado el switch
        val roundTip = binding.roundTipSw.isChecked

        //Si el switch está marcado, se desarrolla la siguiente condición para el redondeo
        //En nuestro caso sólo vamos a redondear a la alza siempre y para ello utilizamos la función matemática de "ceil" (literalmente 'hacer techo'),
        //la cual se importa automáticamente.
        if (roundTip) {
            tip = ceil(tip)
        }

        //Finalmente formatearemos el resultado para que aparezca en función a la divisa predeterminada en función al lenguaje y otros ajustes del móvil
        //de nuestro usuario. Por ejemplo, en Euros hará un formato como €1.234,56 mientras que en dólares lo hará usando $1,234.56.
        //Aquí le decimos que el numero a formatear es el que se genere en tip y que lo guarde en una variable.
        val formattedTip = NumberFormat.getCurrencyInstance().format(tip)
        binding.tipResultTv.text = getString(R.string.tip_amount, formattedTip)
    }

    //Éste método permite que el teclado se oculte cuando se presione la tecla Enter.
    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Hide the keyboard
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }

}



