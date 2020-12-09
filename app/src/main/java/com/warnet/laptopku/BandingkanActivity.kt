package com.warnet.laptopku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_bandingkan.*

class BandingkanActivity : AppCompatActivity(), View.OnClickListener {
    // Variabel untuk menyimpan laptop kiri dan laptop kanan
    private var laptopKiri: LaptopTerbaru? = null // Bisa jadi berupa operan dari Acitivity Favorite atau Activity Deskripsi Laptop
    private var laptopKanan: LaptopTerbaru? = null

    // Variabel untuk menyimpan semua data laptop
    private var listLaptop: ArrayList<LaptopTerbaru>? = null
    private  var autoComplete: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bandingkan)

        // Menampilkan laptop yang ingin dibandingkan di kiri, jika Activity ini dipanggil dari Activity Favorite atau Activity Deskripsi Laptop
        laptopKiri = intent.getParcelableExtra("laptopKiri")
        if(laptopKiri != null){
            cariLaptopKiriAutoCompleteTextView.setText(laptopKiri!!.name)
            muatLaptopKiri()
        }

        listLaptop = intent.getSerializableExtra("listLaptop") as ArrayList<LaptopTerbaru>?
        autoComplete = intent.getSerializableExtra("autoComplete") as ArrayList<String>?
        if (listLaptop == null || autoComplete == null)
            muatSemuaLaptop()

        // Membuat adapter untuk AutoCompleteTextView
        val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_list_item_1, autoComplete?: arrayListOf())
        cariLaptopKiriAutoCompleteTextView.setAdapter(adapter)
        cariLaptopKananAutoCompleteTextView.setAdapter(adapter)

        // Mendaftarkan event cari
        cariLaptopKiriAutoCompleteTextView.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                laptopKiri = listLaptop?.find{ it.name.startsWith(cariLaptopKiriAutoCompleteTextView.text.toString(), true) }
                if (laptopKiri != null)
                    muatLaptopKiri()
                else
                    showToast()
                return@OnEditorActionListener true
            }
            false
        })
        cariLaptopKananAutoCompleteTextView.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                laptopKanan = listLaptop?.find{ it.name.startsWith(cariLaptopKananAutoCompleteTextView.text.toString(), true) }
                if (laptopKanan != null)
                    muatLaptopKanan()
                else
                    showToast()
                return@OnEditorActionListener true
            }
            false
        })

        // Mendaftarkan event klik untuk pindah ke Activity Telusuri
        val telusuriImageView: android.widget.ImageView = findViewById(R.id.bandingkanFooterTelusuriImageView)
        telusuriImageView.setOnClickListener(this)

        // Mendaftarkan event klik untuk pindah ke Activity Rekomendasi
        val rekomendasiImageView: android.widget.ImageView = findViewById(R.id.bandingkanFooterRekomendasiImageView)
        rekomendasiImageView.setOnClickListener(this)

        // Mendaftarkan event klik untuk kembali ke activity sebelumnya
        val kembaliImageView: android.widget.ImageView = findViewById(R.id.bandingkanKembaliImageView)
        kembaliImageView.setOnClickListener(this)
    }

    // Semua event klik
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.bandingkanFooterTelusuriImageView ->{
                val moveIntent = Intent(this@BandingkanActivity, MainActivity::class.java)
                startActivity(moveIntent)
            }
            R.id.bandingkanFooterRekomendasiImageView ->{
                val moveIntent = Intent(this@BandingkanActivity, RekomendasiActivity::class.java)
                moveIntent.putExtra("listLaptop", listLaptop)
                startActivity(moveIntent)
            }
            R.id.bandingkanKembaliImageView -> finish()
        }
    }

    fun muatLaptopKiri(){
        // Mengisi ImageView dengan foto laptop
        Glide.with(applicationContext)
            .load(laptopKiri?.photo)
            .apply(
                RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(
                    Target.SIZE_ORIGINAL))
            .into(laptopKiriImageView)

        // Menampilkan nama dan harga laptop
        namaLaptopKiriTextView.text = laptopKiri?.name
        hargaLaptopKiriTextView.text = laptopKiri?.price

        // Menampilkan spesifikasi-spesifikasi tunggal laptop
        acAdapterKiriTextView.text = laptopKiri?.acadapter
        audioKiriTextView.text = laptopKiri?.audio
        bateraiKiriTextView.text = laptopKiri?.baterai
        cpuKiriTextView.text = laptopKiri?.cpu
        osKiriTextView.text = laptopKiri?.os
        layarKiriTextView.text = laptopKiri?.layar
        chipsetKiriTextView.text = laptopKiri?.chipset
        memoriKiriTextView.text = laptopKiri?.memori
        penyimpananKiriTextView.text = laptopKiri?.penyimpanan
        webcamKiriTextView.text = laptopKiri?.webcam
        keyboardKiriTextView.text = laptopKiri?.keyboard
        dimensiKiriTextView.text = laptopKiri?.dimensi
        beratKiriTextView.text = laptopKiri?.berat

        // Menampilkan spesifikasi-spesifikasi ganda laptop
        for(i in 0 until laptopKiri?.grafis!!.size){
            if(i == 0){
                grafisKiriTextView.text = laptopKiri!!.grafis[i]
            }
            else{
                grafisKiriTextView.append("\n" + laptopKiri!!.grafis[i])
            }
        }
        for(i in 0 until laptopKiri?.io!!.size){
            if(i == 0){
                ioPortsKiriTextView.text = laptopKiri!!.io[i]
            }
            else{
                ioPortsKiriTextView.append("\n" + laptopKiri!!.io[i])
            }
        }
        for(i in 0 until laptopKiri?.komunikasi!!.size){
            if(i == 0){
                komunikasiKiriTextView.text = laptopKiri!!.komunikasi[i]
            }
            else{
                komunikasiKiriTextView.append("\n" + laptopKiri!!.komunikasi[i])
            }
        }
    }

    fun muatLaptopKanan(){
        // Mengisi ImageView dengan foto laptop
        Glide.with(applicationContext)
            .load(laptopKanan?.photo)
            .apply(
                RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(
                    Target.SIZE_ORIGINAL))
            .into(laptopKananImageView)

        // Menampilkan nama dan harga laptop
        namaLaptopKananTextView.text = laptopKanan?.name
        hargaLaptopKananTextView.text = laptopKanan?.price

        // Menampilkan spesifikasi-spesifikasi tunggal laptop
        acAdapterKananTextView.text = laptopKanan?.acadapter
        audioKananTextView.text = laptopKanan?.audio
        bateraiKananTextView.text = laptopKanan?.baterai
        cpuKananTextView.text = laptopKanan?.cpu
        osKananTextView.text = laptopKanan?.os
        layarKananTextView.text = laptopKanan?.layar
        chipsetKananTextView.text = laptopKanan?.chipset
        memoriKananTextView.text = laptopKanan?.memori
        penyimpananKananTextView.text = laptopKanan?.penyimpanan
        webcamKananTextView.text = laptopKanan?.webcam
        keyboardKananTextView.text = laptopKanan?.keyboard
        dimensiKananTextView.text = laptopKanan?.dimensi
        beratKananTextView.text = laptopKanan?.berat

        // Menampilkan spesifikasi-spesifikasi ganda laptop
        for(i in 0 until laptopKanan!!.grafis.size){
            if(i == 0){
                grafisKananTextView.text = laptopKanan!!.grafis[i]
            }
            else{
                grafisKananTextView.append("\n" + laptopKanan!!.grafis[i])
            }
        }
        for(i in 0 until laptopKanan!!.io.size){
            if(i == 0){
                ioPortsKananTextView.text = laptopKanan!!.io[i]
            }
            else{
                ioPortsKananTextView.append("\n" + laptopKanan!!.io[i])
            }
        }
        for(i in 0 until laptopKanan!!.komunikasi.size){
            if(i == 0){
                komunikasiKananTextView.text = laptopKanan!!.komunikasi[i]
            }
            else{
                komunikasiKananTextView.append("\n" + laptopKanan!!.komunikasi[i])
            }
        }
    }

    // Memanggil data semua laptop
    private fun muatSemuaLaptop(){
        listLaptop = arrayListOf()
        autoComplete = arrayListOf()
        val db = FirebaseFirestore.getInstance()
        db.collection("spekLaptop")
            .get()
            .addOnSuccessListener {result ->
                for (document in result){
                    listLaptop?.add(LaptopTerbaru(document.getString("namaLaptop")!!,
                        document.getString("hargaLaptop")!!,
                        document.getString("gambar")!!,
                        document.getString("acadapter")!!,
                        document.getString("audio")!!,
                        document.getString("baterai")!!,
                        document.getString("berat")!!,
                        document.getString("brand")!!,
                        document.getString("chipset")!!,
                        document.getString("cpu")!!,
                        document.getString("dimensi")!!,
                        document.get("grafis")!! as ArrayList<String>,
                        document.get("io")!! as ArrayList<String>,
                        document.get("kategori")!! as ArrayList<String>,
                        document.getString("keyboard")!!,
                        document.get("komunikasi")!! as ArrayList<String>,
                        document.getString("layar")!!,
                        document.getString("memori")!!,
                        document.getString("os")!!,
                        document.getString("penyimpanan")!!,
                        document.getString("tanggalRilis")!!,
                        document.getString("webcam")!!,
                        document.getLong("performa")!!.toInt(),
                        document.getLong("portabilitas")!!.toInt()))
                }
                if(listLaptop!!.isNotEmpty())
                    listLaptop!!.forEach{ autoComplete?.add(it.name) }
                else
                    muatSemuaLaptop()
            }
    }

    fun showToast(){
        val toast = android.widget.Toast.makeText(this,
            "Tidak ditemukan laptop tersebut pada basis data kami.",
            android.widget.Toast.LENGTH_LONG)
        //toast.setGravity(android.view.Gravity.BOTTOM,0,130)
        toast.show()
    }
}