package com.warnet.laptopku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.footer_telusuri.*
import kotlinx.android.synthetic.main.header_cari_laptop.*

class HasilTelusuriActivity : AppCompatActivity(), View.OnClickListener {
    // Inisiasi variabel untuk menerima operan dari activity lain
    private var listLaptop: ArrayList<LaptopTerbaru>? = null
    private var autoComplete: ArrayList<String>? = null
    private var brand: String? = null
    private var kategori: String? = null
    private var cari: String? = null

    // Variabel transaksi fragment
    private lateinit var hasilFragment: HasilFragment
    private lateinit var transaction: FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_telusuri)

        // Assignment variabel operan
        listLaptop = intent.getSerializableExtra("listLaptop") as ArrayList<LaptopTerbaru>?
        autoComplete = intent.getSerializableExtra("autoComplete") as ArrayList<String>?
        brand = intent.getStringExtra("brand")
        kategori = intent.getStringExtra("kategori")
        cari = intent.getStringExtra("cari")

        // Membuat adapter untuk AutoCompleteTextView
        if (autoComplete != null){
            val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_list_item_1, autoComplete!!)
            headerCariLaptopAutoCompleteTextView.setAdapter(adapter)
        }

        // Memanggil FragmentHasil
        transaction = supportFragmentManager.beginTransaction()
        when {
            // Jika Activity dipanggil dari ImageView Kategori: menampilkan kategori tertentu
            kategori != null -> {
                hasilFragment = HasilFragment("kategori", kategori!!, ArrayList(listLaptop!!))
                transaction.add(R.id.hasilTelusuriFrameLayout, hasilFragment)
            }
            // Jika Activity dipanggil dari ImageView Brand: menampilkan brand tertentu
            brand != null -> {
                hasilFragment = HasilFragment("brand", brand!!, ArrayList(listLaptop!!))
                transaction.add(R.id.hasilTelusuriFrameLayout, hasilFragment)
            }
            // Jika Activity dipanggil dari EditText Cari Laptop: menampilkan laptop yang dicari pengguna
            cari != null -> {
                headerCariLaptopAutoCompleteTextView.setText(cari, TextView.BufferType.EDITABLE)
                hasilFragment = HasilFragment("cari", cari!!, ArrayList(listLaptop?: arrayListOf()))
                transaction.add(R.id.hasilTelusuriFrameLayout, hasilFragment)
            }
        }
        transaction.commit()

        // Membuat event-event pada EditText Cari Laptop
        headerCariLaptopAutoCompleteTextView.isCursorVisible = false
        headerCariLaptopAutoCompleteTextView.setOnClickListener{
        headerCariLaptopAutoCompleteTextView.isCursorVisible = true
        }
        headerCariLaptopAutoCompleteTextView.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                hasilFragment = HasilFragment("cari", headerCariLaptopAutoCompleteTextView.text.toString(), ArrayList(listLaptop?: arrayListOf()))
                transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.hasilTelusuriFrameLayout, hasilFragment)
                transaction.commit()
                return@OnEditorActionListener true
            }
            false
        })

        // Mendaftarkan event klik untuk pindah Main Activity
        val telusuriImageView: android.widget.ImageView = findViewById(R.id.telusuriFooterTelusuriImageView)
        telusuriImageView.setOnClickListener(this)
        telusuriFooterTelusuriTextView.setOnClickListener(this)

        // Mendaftarkan event klik untuk pindah ke Activity Rekomendasi
        val rekomendasiImageView: android.widget.ImageView = findViewById(R.id.telusuriFooterRekomendasiImageView)
        rekomendasiImageView.setOnClickListener(this)
        telusuriFooterRekomendasiTextView.setOnClickListener(this)

        //  Mendaftarkan event klik untuk pindah ke Activity Bandingkan
        val bandingkanImageView: android.widget.ImageView = findViewById(R.id.telusuriFooterBandingkanImageView)
        bandingkanImageView.setOnClickListener(this)
        telusuriFooterBandingkanTextView.setOnClickListener(this)

        //  Mendaftarkan event klik untuk kembali ke Activity sebelumnya
        val kembaliImageView: android.widget.ImageView = findViewById(R.id.headerKembaliImageView)
        kembaliImageView.setOnClickListener(this)
    }

    // Isi semua event klik
    override fun onClick(v: View?){
        when(v?.id){
            R.id.telusuriFooterTelusuriImageView, R.id.telusuriFooterTelusuriTextView ->{
                val moveIntent = Intent(this@HasilTelusuriActivity, MainActivity::class.java)
                startActivity(moveIntent)
            }
            R.id.telusuriFooterRekomendasiImageView, R.id.telusuriFooterRekomendasiTextView ->{
                val moveIntent = Intent(this@HasilTelusuriActivity, RekomendasiActivity::class.java)
                moveIntent.putExtra("listLaptop", listLaptop)
                startActivity(moveIntent)
            }
            R.id.telusuriFooterBandingkanImageView, R.id.telusuriFooterBandingkanTextView ->{
                val moveIntent = Intent(this@HasilTelusuriActivity, BandingkanActivity::class.java)
                moveIntent.putExtra("listLaptop", listLaptop)
                startActivity(moveIntent)
            }
            R.id.headerKembaliImageView -> {
                when {
                    hasilFragment.isOverlayUrutkan -> hasilFragment.sembunyikanOverlayUrutkan()
                    hasilFragment.isOverlayFilter -> hasilFragment.sembunyikanOverlayFilter()
                    else -> finish()
                }
            }
        }
    }

    // Override event tombol back
    override fun onBackPressed(){
        when {
            hasilFragment.isOverlayUrutkan -> hasilFragment.sembunyikanOverlayUrutkan()
            hasilFragment.isOverlayFilter -> hasilFragment.sembunyikanOverlayFilter()
            else -> super.onBackPressed()
        }
    }
}