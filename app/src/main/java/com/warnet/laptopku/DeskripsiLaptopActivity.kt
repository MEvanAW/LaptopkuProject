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
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_deskripsi_laptop.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header_cari_laptop.*

class DeskripsiLaptopActivity : AppCompatActivity(), View.OnClickListener {
    // Variabel untuk menerima operan data spesifikasi laptop dari Activity sebelumnya
    private var laptopTerbaru : LaptopTerbaru? = null

    // Inisiasi list untuk Auto Complete
    private val autoComplete: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deskripsi_laptop)

        // Memuat nama-nama laptop untuk Auto Complete
        muatNamaLaptop()

        // Membuat adapter untuk AutoCompleteTextView
        val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_list_item_1, autoComplete)
        headerCariLaptopAutoCompleteTextView.setAdapter(adapter)

        // Menampilkan icon bandingkan
        val bandingkanTextView: TextView = findViewById(R.id.deskripsiLaptopBandingkanTextView)
        bandingkanTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bandingkan_biru,0,0,0)

        // Assignment variabel laptopTerbaru
        laptopTerbaru = intent.getParcelableExtra("laptopTerbaru")

        // Mengisi ImageView dengan foto laptop
        Glide.with(applicationContext)
            .load(laptopTerbaru?.photo)
            .apply(
                RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(
                    Target.SIZE_ORIGINAL))
            .into(img_laptop)

        // Menampilkan nama dan harga laptop
        val tvName: TextView = findViewById(R.id.tv_namaLaptop)
        val tvHarga: TextView = findViewById(R.id.tv_hargaLaptop)
        tvName.text = laptopTerbaru?.name
        tvHarga.text = laptopTerbaru?.price

        // Menampilkan spesifikasi-spesifikasi tunggal laptop
        tv_acAdapter.text = laptopTerbaru?.acadapter
        tv_audio.text = laptopTerbaru?.audio
        tv_baterai.text = laptopTerbaru?.baterai
        tv_cpu.text = laptopTerbaru?.cpu
        tv_os.text = laptopTerbaru?.os
        tv_layar.text = laptopTerbaru?.layar
        tv_chipset.text = laptopTerbaru?.chipset
        tv_memori.text = laptopTerbaru?.memori
        tv_penyimpanan.text = laptopTerbaru?.penyimpanan
        tv_webcam.text = laptopTerbaru?.webcam
        tv_keyboard.text = laptopTerbaru?.keyboard
        tv_dimensi.text = laptopTerbaru?.dimensi
        tv_berat.text = laptopTerbaru?.berat

        // Menampilkan spesifikasi-spesifikasi ganda laptop
        for(i in 0 until laptopTerbaru?.grafis!!.size){
            if(i == 0){
                tv_grafis.text = laptopTerbaru!!.grafis[i]
            }
            else{
                tv_grafis.append("\n" + laptopTerbaru!!.grafis[i])
            }
        }
        for(i in 0 until laptopTerbaru?.io!!.size){
            if(i == 0){
                tv_io.text = laptopTerbaru!!.io[i]
            }
            else{
                tv_io.append("\n" + laptopTerbaru!!.io[i])
            }
        }
        for(i in 0 until laptopTerbaru?.komunikasi!!.size){
            if(i == 0){
                tv_komunikasi.text = laptopTerbaru!!.komunikasi[i]
            }
            else{
                tv_komunikasi.append("\n" + laptopTerbaru!!.komunikasi[i])
            }
        }

        // Membuat event-event EditText Cari Laptop
        headerCariLaptopAutoCompleteTextView.isCursorVisible = false
        headerCariLaptopAutoCompleteTextView.setOnClickListener{
            headerCariLaptopAutoCompleteTextView.isCursorVisible = true
        }
        headerCariLaptopAutoCompleteTextView.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                val moveIntent = Intent(this@DeskripsiLaptopActivity, HasilTelusuriActivity::class.java)
                moveIntent.putExtra("cari", headerCariLaptopAutoCompleteTextView.text.toString())
                moveIntent.putExtra("autoComplete", autoComplete)
                startActivity(moveIntent)
                return@OnEditorActionListener true
            }
            false
        })

        // Mendaftarkan event klik untuk pindah Main Activity
        val telusuriImageView: android.widget.ImageView = findViewById(R.id.telusuriFooterTelusuriImageView)
        telusuriImageView.setOnClickListener(this)

        // Mendaftarkan event klik untuk pindah ke Activity Rekomendasi
        val rekomendasiImageView: android.widget.ImageView = findViewById(R.id.telusuriFooterRekomendasiImageView)
        rekomendasiImageView.setOnClickListener(this)

        //  Mendaftarkan event klik untuk pindah ke Activity Bandingkan
        bandingkanTextView.setOnClickListener(this)
        val bandingkanImageView: android.widget.ImageView = findViewById(R.id.telusuriFooterBandingkanImageView)
        bandingkanImageView.setOnClickListener(this)

        //  Mendaftarkan event klik untuk kembali ke Activity sebelumnya
        val kembaliImageView: android.widget.ImageView = findViewById(R.id.headerKembaliImageView)
        kembaliImageView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.telusuriFooterTelusuriImageView ->{
                val moveIntent = android.content.Intent(this@DeskripsiLaptopActivity, MainActivity::class.java)
                startActivity(moveIntent)
            }
            R.id.telusuriFooterRekomendasiImageView ->{
                val moveIntent = android.content.Intent(this@DeskripsiLaptopActivity, RekomendasiActivity::class.java)
                startActivity(moveIntent)
            }
            R.id.telusuriFooterBandingkanImageView ->{
                val moveIntent = android.content.Intent(this@DeskripsiLaptopActivity, BandingkanActivity::class.java)
                startActivity(moveIntent)
            }
            R.id.deskripsiLaptopBandingkanTextView ->{
                val moveIntent = android.content.Intent(this@DeskripsiLaptopActivity, BandingkanActivity::class.java)
                moveIntent.putExtra("laptopKiri", laptopTerbaru)
                startActivity(moveIntent)
            }
            R.id.headerKembaliImageView -> finish()
        }
    }

    // Memuat nama-nama laptop untuk Auto Complete
    private fun muatNamaLaptop(){
        val db = FirebaseFirestore.getInstance()
        db.collection("spekLaptop")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    autoComplete.add(document.getString("namaLaptop")!!)
                }
                if (autoComplete.isEmpty()) {
                    muatNamaLaptop()
                }
            }
    }
}