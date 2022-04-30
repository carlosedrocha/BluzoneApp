package br.com.azzonaazul.modulofiscal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class TelaInicialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_inicial)

        val btnConsultar_Veiculo = findViewById<Button>(R.id.btnConsultarVeiculo)
        btnConsultar_Veiculo.setOnClickListener(){
            val intent = Intent(this, VerficarVeiculoActivity::class.java)
            startActivity(intent)
        }


    }
}