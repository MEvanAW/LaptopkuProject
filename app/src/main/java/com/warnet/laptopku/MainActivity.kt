package com.warnet.laptopku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    // Inisiasi variabel RecyclerView untuk menampilkan laptop rilis terbaru
    private lateinit var rvLaptop: RecyclerView

    // Inisiasi list untuk menampung data laptop
    private val listLaptop: ArrayList<LaptopTerbaru> = arrayListOf()
    private val autoComplete: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Menghubungkan variabel RecyclerView dengan RecyclerView sesungguhnya
        rvLaptop = findViewById(R.id.rv_laptop)
        rvLaptop.setHasFixedSize(true)

        // Membuat progress bar menjadi nampak
        rilisTerbaruProgressBar.visibility = View.VISIBLE

        // Memanggil data laptop rilis terbaru dari Firestore sekaligus ditampilkan
        muatLaptop()

        // Membuat adapter untuk AutoCompleteTextView
        val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_list_item_1, autoComplete)
        cariLaptopAutoCompleteTextView.setAdapter(adapter)

        // Membuat event-event EditText Cari Laptop
        cariLaptopAutoCompleteTextView.isCursorVisible = false
        cariLaptopAutoCompleteTextView.setOnClickListener{
            cariLaptopAutoCompleteTextView.isCursorVisible = true
        }
        cariLaptopAutoCompleteTextView.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                val moveIntent = Intent(this@MainActivity, HasilTelusuriActivity::class.java)
                moveIntent.putExtra("cari", cariLaptopAutoCompleteTextView.text.toString())
                moveIntent.putExtra("listLaptop", listLaptop)
                moveIntent.putExtra("autoComplete", autoComplete)
                startActivity(moveIntent)
                return@OnEditorActionListener true
            }
            false
        })

        // Mendaftarkan event klik untuk pindah ke Activity HasilTelusuri
        val gaming: ImageView = findViewById(R.id.gamingImageView)
        gaming.setOnClickListener(this)

        val profesional: ImageView = findViewById(R.id.profesionalImageView)
        profesional.setOnClickListener(this)

        val pelajar: ImageView = findViewById(R.id.pelajarImageView)
        pelajar.setOnClickListener(this)

        val workstation: ImageView = findViewById(R.id.workstationImageView)
        workstation.setOnClickListener(this)

        val acer: ImageView = findViewById(R.id.acerImageView)
        acer.setOnClickListener(this)

        val asus: ImageView = findViewById(R.id.asusImageView)
        asus.setOnClickListener(this)

        val hp: ImageView = findViewById(R.id.hpImageView)
        hp.setOnClickListener(this)

        val lenovo: ImageView = findViewById(R.id.lenovoImageView)
        lenovo.setOnClickListener(this)

        val msi: ImageView = findViewById(R.id.msiImageView)
        msi.setOnClickListener(this)

        // Mendaftarkan event klik untuk pindah ke Activity Rekomendasi
        val imgMenuRekomendasi: ImageView = findViewById(R.id.telusuriFooterRekomendasiImageView)
        imgMenuRekomendasi.setOnClickListener(this)

        // Mendaftarkan event klik untuk pindah ke Activity Bandingkan
        val imgMenuBandingkan: ImageView = findViewById(R.id.telusuriFooterBandingkanImageView)
        imgMenuBandingkan.setOnClickListener(this)
    }

    // Memanggil data laptop dari Firestore sekaligus ditampilkan
    private fun muatLaptop(){
        val db = FirebaseFirestore.getInstance()
        db.collection("spekLaptop")
            .orderBy("tanggalRilis", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {result ->
                for (document in result){
                    listLaptop.add(LaptopTerbaru(document.getString("namaLaptop")!!,
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
                if(listLaptop.isNotEmpty()){
                    showRecyclerList()
                    rilisTerbaruProgressBar.visibility = View.GONE
                    listLaptop.forEach{ autoComplete.add(it.name) }
                }
                else
                    muatLaptop()
            }
    }
    // Menampilkan laptop rilis terbaru pada RecyclerView
    private fun showRecyclerList(){
        rvLaptop.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val listLaptopTerbaruAdapter = ListLaptopTerbaruAdapter(applicationContext, listLaptop.take(5) as ArrayList<LaptopTerbaru>)
        rvLaptop.adapter = listLaptopTerbaruAdapter
    }

    // Semua event klik
    override fun onClick(v: View?){
        when(v?.id){
            R.id.gamingImageView ->{
                val moveIntent = Intent(this@MainActivity, HasilTelusuriActivity::class.java)
                moveIntent.putExtra("kategori", "Gaming")
                moveIntent.putExtra("listLaptop", listLaptop)
                moveIntent.putExtra("autoComplete", autoComplete)
                startActivity(moveIntent)
            }
            R.id.pelajarImageView ->{
                val moveIntent = Intent(this@MainActivity, HasilTelusuriActivity::class.java)
                moveIntent.putExtra("kategori", "Pelajar")
                moveIntent.putExtra("listLaptop", listLaptop)
                moveIntent.putExtra("autoComplete", autoComplete)
                startActivity(moveIntent)
            }
            R.id.profesionalImageView ->{
                val moveIntent = Intent(this@MainActivity, HasilTelusuriActivity::class.java)
                moveIntent.putExtra("kategori", "Profesional")
                moveIntent.putExtra("listLaptop", listLaptop)
                moveIntent.putExtra("autoComplete", autoComplete)
                startActivity(moveIntent)
            }
            R.id.workstationImageView ->{
                val moveIntent = Intent(this@MainActivity, HasilTelusuriActivity::class.java)
                moveIntent.putExtra("kategori", "Workstation")
                moveIntent.putExtra("listLaptop", listLaptop)
                moveIntent.putExtra("autoComplete", autoComplete)
                startActivity(moveIntent)
            }
            R.id.acerImageView ->{
                val moveIntent = Intent(this@MainActivity, HasilTelusuriActivity::class.java)
                moveIntent.putExtra("brand", "Acer")
                moveIntent.putExtra("listLaptop", listLaptop)
                moveIntent.putExtra("autoComplete", autoComplete)
                startActivity(moveIntent)
            }
            R.id.asusImageView ->{
                val moveIntent = Intent(this@MainActivity, HasilTelusuriActivity::class.java)
                moveIntent.putExtra("brand", "Asus")
                moveIntent.putExtra("listLaptop", listLaptop)
                moveIntent.putExtra("autoComplete", autoComplete)
                startActivity(moveIntent)
            }
            R.id.hpImageView ->{
                val moveIntent = Intent(this@MainActivity, HasilTelusuriActivity::class.java)
                moveIntent.putExtra("brand", "HP")
                moveIntent.putExtra("listLaptop", listLaptop)
                moveIntent.putExtra("autoComplete", autoComplete)
                startActivity(moveIntent)
            }
            R.id.lenovoImageView ->{
                val moveIntent = Intent(this@MainActivity, HasilTelusuriActivity::class.java)
                moveIntent.putExtra("brand", "Lenovo")
                moveIntent.putExtra("listLaptop", listLaptop)
                moveIntent.putExtra("autoComplete", autoComplete)
                startActivity(moveIntent)
            }
            R.id.msiImageView ->{
                val moveIntent = Intent(this@MainActivity, HasilTelusuriActivity::class.java)
                moveIntent.putExtra("brand", "MSI")
                moveIntent.putExtra("listLaptop", listLaptop)
                moveIntent.putExtra("autoComplete", autoComplete)
                startActivity(moveIntent)
            }
            R.id.telusuriFooterRekomendasiImageView ->{
                val moveIntent = Intent(this@MainActivity, RekomendasiActivity::class.java)
                moveIntent.putExtra("listLaptop", listLaptop)
                startActivity(moveIntent)
            }
            R.id.telusuriFooterBandingkanImageView ->{
                val moveIntent = Intent(this@MainActivity, BandingkanActivity::class.java)
                moveIntent.putExtra("listLaptop", listLaptop)
                moveIntent.putExtra("autoComplete", autoComplete)
                startActivity(moveIntent)
            }
        }
    }
}

