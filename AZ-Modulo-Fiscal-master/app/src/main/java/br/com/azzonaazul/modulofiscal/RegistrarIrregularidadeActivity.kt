package br.com.azzonaazul.modulofiscal

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import br.com.azzonaazul.modulofiscal.databinding.ActivityRegistrarIrregularidadeBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log


class  RegistrarIrregularidadeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarIrregularidadeBinding

    private var fotos = arrayOf("null", "null", "null", "null")
    private var whichBtn = 0
    private var placa = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val extras = intent.extras
        val value = extras?.getString("placa")
        placa = value.toString()

        binding = ActivityRegistrarIrregularidadeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnIMG1.setOnClickListener {
            whichBtn = 1
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
        }
        binding.btnIMG2.setOnClickListener {
            whichBtn = 2
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
        }
        binding.btnIMG3.setOnClickListener {
            whichBtn = 3
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
        }
        binding.btnIMG4.setOnClickListener {
            whichBtn = 4
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
        }
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, TelaInicialActivity::class.java)
            startActivity(intent)
        }
        binding.btnRegistrar.setOnClickListener {
            if (fotos.contains("null")) {
                Toast.makeText(
                    binding.root.context,
                    "Para efetuar o registro, tire 4 fotos de evidência",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                postRegistrarIrregularidade(placa, fotos)
            }
        }
    }

    private val cameraProviderResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                abrirTelaDePreview()
            } else {
                Snackbar.make(
                    binding.root,
                    "Você não concedeu permissões para usar a câmera.",
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            }
        }

    private fun abrirTelaDePreview() {
        val intent = Intent(this, CameraPreviewActivity::class.java)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val returnString = data!!.getStringExtra("photoUri")
                fotos.set(whichBtn - 1, returnString.orEmpty())
                if (whichBtn == 1) {
                    binding.btnIMG1.setImageURI(Uri.parse(returnString))
                } else if (whichBtn == 2) {
                    binding.btnIMG2.setImageURI(Uri.parse(returnString))
                } else if (whichBtn == 3) {
                    binding.btnIMG3.setImageURI(Uri.parse(returnString))
                } else if (whichBtn == 4) {
                    binding.btnIMG4.setImageURI(Uri.parse(returnString))
                }
            }
        }
    }


    fun postRegistrarIrregularidade(placa: String, fotos: Array<String>) {
        val irregularidade: Irregularidade = Irregularidade()

        val date = Calendar.getInstance().time

        var dateTimeFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        irregularidade.data = dateTimeFormat.format(date).toString()


        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        irregularidade.imei = "456123"

        var result = uploadImage(fotos[0].toUri())
        irregularidade.foto1 = result

        result = uploadImage(fotos[1].toUri())
        irregularidade.foto2 = result

        result = uploadImage(fotos[2].toUri())
        irregularidade.foto3 = result

        result = uploadImage(fotos[3].toUri())
        irregularidade.foto4 = result

        irregularidade.placa = placa

        val gson = Gson()
        val json = gson.toJson(irregularidade)


        var URL =
            "https://southamerica-east1-projeto-integrador-3-341623.cloudfunctions.net/addIrregularidade"
        val headerHttp = "application/json; charset=utf-8".toMediaTypeOrNull()
        val client = OkHttpClient()
        val body = RequestBody.create(headerHttp, json)
        val request = Request.Builder().url(URL).post(body).build()
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()

        if (responseBody.toString().contains("salvo")) {
            binding.tvMsgStatus.setText("Irregularidade registrada no sistema com sucesso.")
            binding.tvMsgStatus.setTextColor(Color.parseColor("#003383"));
            binding.btnIMG1.visibility = View.GONE
            binding.btnIMG2.visibility = View.GONE
            binding.btnIMG3.visibility = View.GONE
            binding.btnIMG4.visibility = View.GONE
            binding.btnRegistrar.visibility = View.GONE
            binding.btnHome.visibility = View.VISIBLE
            binding.tvMsgStatus.visibility = View.VISIBLE
        } else {
            binding.tvMsgStatus.visibility = View.VISIBLE
            binding.tvMsgStatus.setText("Houve um erro, irregularidade não registrada.")
            binding.tvMsgStatus.setTextColor(Color.RED);
            binding.btnIMG1.visibility = View.GONE
            binding.btnIMG2.visibility = View.GONE
            binding.btnIMG3.visibility = View.GONE
            binding.btnIMG4.visibility = View.GONE
            binding.btnRegistrar.visibility = View.VISIBLE
            binding.btnHome.visibility + View.VISIBLE
            binding.btnRegistrar.visibility = View.GONE
        }
    }

    private fun uploadImage(uri: Uri): String {
        val path = "images/" + UUID.randomUUID() + ".png"
        val storageReference = FirebaseStorage.getInstance().getReference(path);

        storageReference.putFile(uri)
        return storageReference.downloadUrl.result.toString()

    }
}






