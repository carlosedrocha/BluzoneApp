package br.com.projetosmobile.appimc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Variaveis
        val btnCalc = findViewById<Button>(R.id.btnCalc)
        val vPeso = findViewById<TextView>(R.id.vPeso)
        val vAltura = findViewById<TextView>(R.id.vAltura)
        val Resp = findViewById<TextView>(R.id.Resp)

        btnCalc.setOnClickListener {
            if(vPeso.text.toString() != "" && vAltura.text.toString() != "" ) {
                val imc = calcIMC(vPeso.text.toString(), vAltura.text.toString())

                val imcResp = "IMC: " + "\n" + checkIMC(imc)
                Resp.text = imcResp
            }
            else{
                Resp.text = "Valores nulos."
            }
        }
    }
    // Cálculo do IMC
    private fun calcIMC(peso: String, altura: String): Double  {
        return peso.toDouble() / (altura.toDouble() * altura.toDouble())
    }

    // Retorna string de acordo com o cálculo
    private fun checkIMC(db: Double): String{
        when {
            0.0 < db < 17.0 -> {
                return "Muito abaixo do peso."
            }
            17.1 < db < 18.49 -> {
                return "Abaixo do peso."
            }
            18.5 < db < 24.99 -> {
                return "Abaixo do peso."
            }
            25.0 < db < 29.99 -> {
                return "Acima do peso."
            }
            30.0 < db < 34.99 -> {
                return "Obesidade I"
            }
            35.0 < db < 39.99 -> {
                return "Obesidade II(severa)"
            }
            else -> return "Obesidade III(mórbida)."
        }

    }
}







