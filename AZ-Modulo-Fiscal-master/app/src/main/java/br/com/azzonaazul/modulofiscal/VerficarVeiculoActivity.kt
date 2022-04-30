package br.com.azzonaazul.modulofiscal

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request


class VerficarVeiculoActivity : AppCompatActivity() {

    private lateinit var etPlaca: AppCompatEditText
    private lateinit var btnConsultar: AppCompatButton
    private lateinit var btnRegistrar: AppCompatButton
    private lateinit var tvMsgStatus: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verficar_veiculo)

        etPlaca = findViewById(R.id.etPlaca)
        btnConsultar = findViewById(R.id.btnConsultar)

        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        btnConsultar.setOnClickListener {
            it.hideKeyboard()
            consultarPlaca()
        }
        btnRegistrar = findViewById(R.id.btnRegistrar)
        tvMsgStatus = findViewById(R.id.tvMsgStatus)

        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        btnRegistrar.setOnClickListener(){
            val intent = Intent(this, RegistrarIrregularidadeActivity::class.java)
            intent.putExtra("placa", etPlaca.text.toString())
            startActivity(intent)
        }
    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun consultarPlaca() {
        if (etPlaca.text.isNullOrBlank()) {
            Snackbar.make(
                tvMsgStatus,
                "Informe a placa do veículo que deseja consultar",
                Snackbar.LENGTH_LONG
            ).show()
            tvMsgStatus.visibility = View.GONE;
            btnRegistrar.visibility = View.GONE;
        }else if(etPlaca.text.toString().length != 7){
            Snackbar.make(
                tvMsgStatus,
                "Placas válidas possuem 7 digitos",
                Snackbar.LENGTH_LONG
            ).show()
            tvMsgStatus.visibility = View.GONE;
            btnRegistrar.visibility = View.GONE;
        }
        else {
            val resultadoString = getConsultaPlaca(etPlaca.text.toString());

            if (resultadoString.toString().isEmpty()) {
                tvMsgStatus.setText("Veiculo não encontrado")
                tvMsgStatus.visibility = View.VISIBLE;
                btnRegistrar.visibility = View.GONE;


            } else {
                val gson = Gson()
                var consulta = ConsultaVeiculo()
                consulta = gson.fromJson(resultadoString, ConsultaVeiculo::class.java)

                if (consulta.situacaoPagamento.equals("Efetuado")) {
                    tvMsgStatus.setText(getString(R.string.consultar_placa_retorno_regular))
                    tvMsgStatus.setTextColor(Color.parseColor("#003383"));
                    tvMsgStatus.visibility = View.VISIBLE;
                    btnRegistrar.visibility = View.GONE;
                } else if(consulta.situacaoPagamento.equals("Não efetuado")){
                    tvMsgStatus.setText("Veiculo irregular")
                    tvMsgStatus.setTextColor(Color.RED);
                    tvMsgStatus.visibility = View.VISIBLE;
                    btnRegistrar.visibility = View.VISIBLE;
                }
            }
        }
    }

    fun getConsultaPlaca(placa: String): String? {

        var URL =
            "https://southamerica-east1-projeto-integrador-3-341623.cloudfunctions.net/verificarPlaca/?placa=" + placa.uppercase()
        val client = OkHttpClient()
        val request = Request.Builder().url(URL).get().build()
        val response = client.newCall(request).execute()
        val responseBody = response.body
        val json = responseBody?.string()
        println("Resposta" + json)

        return json
    }
}


