package com.warnet.laptopku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class RekomendasiActivity : AppCompatActivity(), View.OnClickListener {
    // inisiasi variabel untuk menyimpan data semua laptop (operan dari Main Activity)
    private lateinit var listLaptop: ArrayList<LaptopTerbaru>

    // fragmentManager beserta transaction untuk mengatur pergantian fragment
    private lateinit var fragmentManager: FragmentManager
    private lateinit var transaction: FragmentTransaction

    // Inisiasi variabel untuk mengakses Button Selanjutnya dan Sebelumnya
    private lateinit var selanjutnyaButton: android.widget.LinearLayout
    private lateinit var sebelumnyaButton: android.widget.LinearLayout

    // Inisiasi variabel untuk menyimpan fragment
    private val budgetFragment = BudgetFragment()
    private lateinit var keperluanFragment: KeperluanFragment
    private lateinit var prioritasFragment: PrioritasFragment
    private lateinit var brandFragment: BrandFragment
    private var hasilFragment = HasilFragment()

    // Variabel agar Activity dapat mengenali sedang memuat fragment yang mana
    private var currentFragment = "budget"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rekomendasi)

        // Memuat fragment budget
        fragmentManager = supportFragmentManager
        transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.rekomendasiFrameLayout, budgetFragment)
        transaction.commit()

        // Menerima operan data laptop
        listLaptop = intent.getSerializableExtra("listLaptop") as ArrayList<LaptopTerbaru>

        // Digunakan untuk pindah ke tampilan telusuri
        val telusuriImageView: android.widget.ImageView = findViewById(R.id.rekomendasiFooterTelusuriImageView)
        telusuriImageView.setOnClickListener(this)

        // Digunakan untuk pindah ke tampilan bandingkan
        val bandingkanImageView: android.widget.ImageView = findViewById(R.id.rekomendasiFooterBandingkanImageView)
        bandingkanImageView.setOnClickListener(this)

        // Digunakan untuk pindah ke fragment selanjutnya
        selanjutnyaButton = findViewById(R.id.selanjutnyaButton)
        selanjutnyaButton.setOnClickListener(this)

        // Digunakan untuk kembali ke tampilan atau fragment sebelumnya
        sebelumnyaButton = findViewById(R.id.sebelumnyaButton)
        sebelumnyaButton.setOnClickListener(this)
        val kembaliImageView: android.widget.ImageView = findViewById(R.id.rekomendasiKembaliImageView)
        kembaliImageView.setOnClickListener(this)
    }

    // Isi semua event klik
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.rekomendasiFooterTelusuriImageView ->{
                val moveIntent = android.content.Intent(this@RekomendasiActivity, MainActivity::class.java)
                startActivity(moveIntent)
            }
            R.id.rekomendasiFooterBandingkanImageView ->{
                val moveIntent = android.content.Intent(this@RekomendasiActivity, BandingkanActivity::class.java)
                startActivity(moveIntent)
            }
            R.id.selanjutnyaButton ->{
                transaction = fragmentManager.beginTransaction()
                when (currentFragment) {
                    "budget" -> {
                        if(budgetFragment.minEditText.text.isNullOrBlank() || budgetFragment.maxEditText.text.isNullOrBlank()){
                            showToast("Budget minimal dan budget maximal tidak boleh kosong.")
                        }
                        else if(budgetFragment.min > budgetFragment.max){
                            showToast("Budget minimal harus lebih kecil dari budget maximal.")
                        }
                        else{
                            keperluanFragment = KeperluanFragment()
                            transaction.replace(R.id.rekomendasiFrameLayout, keperluanFragment)
                            transaction.addToBackStack(null)
                            transaction.commit()
                            sebelumnyaButton.setBackgroundResource(R.drawable.bg_button_biru)
                            currentFragment = "keperluan"
                        }
                    }
                    "keperluan" -> {
                        if (!keperluanFragment.gameBerat && !keperluanFragment.kalkulasiRumit && !keperluanFragment.grafis2D &&
                                !keperluanFragment.grafis3D && !keperluanFragment.editingVideo && !keperluanFragment.pekerjaanRingan)
                            showToast("Harus ada minimal satu keperluan yang dipilih.")
                        else{
                            prioritasFragment = PrioritasFragment()
                            transaction.replace(R.id.rekomendasiFrameLayout, prioritasFragment)
                            transaction.addToBackStack(null)
                            transaction.commit()
                            currentFragment = "prioritas"
                        }
                    }
                    "prioritas" -> {
                        if (!prioritasFragment.isPerforma && !prioritasFragment.isPortabilitas)
                            showToast("Mohon pilih jawaban terlebih dahulu.")
                        else{
                            brandFragment = BrandFragment()
                            transaction.replace(R.id.rekomendasiFrameLayout, brandFragment)
                            transaction.addToBackStack(null)
                            transaction.commit()
                            currentFragment = "brand"
                        }
                    }
                    "brand" -> {
                        if (!brandFragment.isAcer && !brandFragment.isAsus && !brandFragment.isHp &&
                            !brandFragment.isLenovo && !brandFragment.isMsi)
                            showToast("Harus ada minimal satu keperluan yang dipilih.")
                        else{
                            hasilFragment = HasilFragment(budgetFragment.min,
                                budgetFragment.max, keperluanFragment.gameBerat, keperluanFragment.kalkulasiRumit,
                                keperluanFragment.grafis2D, keperluanFragment.grafis3D, keperluanFragment.editingVideo,
                                keperluanFragment.pekerjaanRingan, prioritasFragment.isPerforma, brandFragment.isAcer,
                                brandFragment.isAsus, brandFragment.isHp, brandFragment.isLenovo, brandFragment.isMsi,
                                ArrayList(listLaptop))
                            transaction.replace(R.id.rekomendasiFrameLayout, hasilFragment)
                            transaction.addToBackStack(null)
                            transaction.commit()
                            sebelumnyaButton.visibility = View.GONE
                            selanjutnyaButton.visibility = View.GONE
                            currentFragment = "hasil"
                        }
                    }
                }
            }
            R.id.rekomendasiKembaliImageView, R.id.sebelumnyaButton -> {
                if (currentFragment == "budget")
                    finish()
                else {
                    when {
                        hasilFragment.isOverlayUrutkan -> hasilFragment.sembunyikanOverlayUrutkan()
                        hasilFragment.isOverlayFilter -> hasilFragment.sembunyikanOverlayFilter()
                        else -> {
                            fragmentManager.popBackStack()
                            sesuaikanCurrentFragmentKetikaKembali()
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed(){
        when {
            hasilFragment.isOverlayUrutkan -> hasilFragment.sembunyikanOverlayUrutkan()
            hasilFragment.isOverlayFilter -> hasilFragment.sembunyikanOverlayFilter()
            else -> {
                super.onBackPressed()
                sesuaikanCurrentFragmentKetikaKembali()
            }
        }
    }

    fun showToast(text: String){
        val toast = android.widget.Toast.makeText(applicationContext,
            text,
            android.widget.Toast.LENGTH_LONG)
        toast.setGravity(android.view.Gravity.BOTTOM,0,130)
        toast.show()
    }

    fun sesuaikanCurrentFragmentKetikaKembali(){
        when (currentFragment) {
            "keperluan" -> {
                currentFragment = "budget"
                sebelumnyaButton.setBackgroundResource(R.drawable.bg_button_abu)
            }
            "prioritas" -> currentFragment = "keperluan"
            "brand" -> currentFragment = "prioritas"
            "hasil" -> {
                sebelumnyaButton.visibility = View.VISIBLE
                selanjutnyaButton.visibility = View.VISIBLE
                currentFragment = "brand"
            }
        }
    }
}