package com.warnet.laptopku

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_hasil.*
import kotlin.math.absoluteValue

// Konstruktor primer dipanggil ketika ingin menampilkan semua laptop
class HasilFragment() : Fragment() {
    // Inisiasi variabel agar Fragment mengetahui laptop bagaimana yang diminta
    private var extraType = "none"
    private var extra = "all"
    private var min = 0
    private var max = 99999999
    private var gameBerat = true
    private var kalkulasiRumit = true
    private var grafis2D = true
    private var grafis3D = true
    private var editingVideo = true
    private var pekerjaanRingan = false
    private var isPerforma = false
    private var isAcer = true
    private var isAsus = true
    private var isHp = true
    private var isLenovo = true
    private var isMsi = true

    // Inisiasi variabel RecyclerView yang akan menampilkan grid laptop
    private lateinit var hasilRecyclerView: androidx.recyclerview.widget.RecyclerView

    // Inisiasi variabel ProgressBar
    private lateinit var progressBar: android.widget.ProgressBar

    // Inisiasi ArrayList untuk menampung data laptop yang diminta
    private var listLaptop: ArrayList<LaptopTerbaru> = arrayListOf()
    private var listLaptopFilter: ArrayList<LaptopTerbaru> = arrayListOf()
    private val rekomenLaptop: ArrayList<RekomenLaptop> = arrayListOf()

    // Boolean untuk mengetahui apakah overlay sedang tampil
    internal var isOverlayUrutkan = false
    internal var isOverlayFilter = false

    // String untuk mengetahui urutkan atau filter saat ini
    private var urutkanBerdasar = "Tidak"
    private var filterBerdasar = "Semua"

    // Konstruktor sekunder dipanggil ketika pengguna meminta brand, kategori tertentu, atau melalui pencarian
    constructor(extraType: String, extra: String, listLaptop: ArrayList<LaptopTerbaru>) : this(){
        this.extraType = extraType
        this.extra = extra
        this.listLaptop = listLaptop
    }

    // Konstruktor sekunder dipanggil ketika fragment dipanggil melalui Activity Rekomendasi
    constructor(min: Int, max: Int, gameBerat: Boolean, kalkulasiRumit: Boolean, grafis2D: Boolean,
        grafis3D: Boolean, editingVideo: Boolean, pekerjaanRingan: Boolean, isPerforma: Boolean,
        isAcer: Boolean, isAsus: Boolean, isHp: Boolean, isLenovo: Boolean, isMsi: Boolean, listLaptop: ArrayList<LaptopTerbaru>) : this(){
        this.extraType = "rekomendasi"
        this.min = min
        this.max = max
        this.gameBerat = gameBerat
        this.kalkulasiRumit = kalkulasiRumit
        this.grafis2D = grafis2D
        this.grafis3D = grafis3D
        this.editingVideo = editingVideo
        this.pekerjaanRingan = pekerjaanRingan
        this.isPerforma = isPerforma
        this.isAcer = isAcer
        this.isAsus = isAsus
        this.isHp = isHp
        this.isLenovo = isLenovo
        this.isMsi = isMsi
        this.listLaptop = listLaptop
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_hasil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Menampilkan icon urutkan
        urutkanTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_urutkan,0,0,0)
        urutkanTextView.compoundDrawablePadding = 32

        // Menampilkan icon filter
        filterTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_filter,0,0,0)
        filterTextView.compoundDrawablePadding = 32

        // Menghubungkan variabel RecyclerView dengan RecyclerView sesungguhnya
        hasilRecyclerView = view.findViewById(R.id.hasilRecyclerView)
        hasilRecyclerView.setHasFixedSize(true)

        // Membuat progress bar menjadi nampak
        progressBar = view.findViewById(R.id.hasilProgressBar)
        progressBar.visibility = View.VISIBLE

        // Memanggil data yang diminta dari Firestore sekaligus ditampilkan
        when (extraType) {
            "cari" -> {
                if (listLaptop.isNotEmpty())
                    filterCari()
                else
                    loadLaptopCari()
            }
            "brand" -> {
                loadLaptopBrand()
                palingSesuaiButton.visibility = View.INVISIBLE
            }
            "kategori" -> {
                loadLaptopKategori()
                palingSesuaiButton.visibility = View.INVISIBLE
            }
            "rekomendasi" -> {
                loadLaptopRekomendasi()
                palingSesuaiButton.visibility = View.INVISIBLE
            }
        }

        // Menambahkan event urutkan
        urutkanTextView.setOnClickListener{
            if (progressBar.visibility == View.GONE){
                if (isOverlayFilter)
                    sembunyikanOverlayFilter()
                tampilkanOverlayUrutkan()
            }
        }
        palingSesuaiButton.setOnClickListener{
            if (urutkanBerdasar != "Paling Sesuai" && palingSesuaiButton.visibility == View.VISIBLE){
                pilihButtonUrutkan("Paling Sesuai")
                progressBar.visibility = View.VISIBLE
                if (listLaptopFilter.isEmpty()){
                    listLaptop.sortBy{ it.name.compareTo(extra, true).absoluteValue }
                    showRecyclerList()
                }
                else{
                    listLaptop.sortBy{ it.name.compareTo(extra, true).absoluteValue }
                    listLaptopFilter.sortBy{ it.name.compareTo(extra, true).absoluteValue }
                    showRecyclerList(filterBerdasar)
                }
                progressBar.visibility = View.GONE
            }
        }
        terbaruButton.setOnClickListener{
            if (urutkanBerdasar != "Terbaru"){
                pilihButtonUrutkan("Terbaru")
                progressBar.visibility = View.VISIBLE
                if (listLaptopFilter.isEmpty()){
                    listLaptop.sortByDescending{ it.tanggalRilis }
                    showRecyclerList()
                }
                else{
                    listLaptop.sortByDescending{ it.tanggalRilis }
                    listLaptopFilter.sortByDescending{ it.tanggalRilis }
                    showRecyclerList(filterBerdasar)
                }
                progressBar.visibility = View.GONE
            }
        }
        hargaTertinggiButton.setOnClickListener{
            if (urutkanBerdasar != "Harga Tertinggi"){
                pilihButtonUrutkan("Harga Tertinggi")
                progressBar.visibility = View.VISIBLE
                if (listLaptopFilter.isEmpty()){
                    listLaptop.sortByDescending{ l -> l.price.filter { it.isDigit() }.toInt() }
                    showRecyclerList()
                }
                else{
                    listLaptop.sortByDescending{ l -> l.price.filter { it.isDigit() }.toInt() }
                    listLaptopFilter.sortByDescending{ l -> l.price.filter { it.isDigit() }.toInt() }
                    showRecyclerList(filterBerdasar)
                }
                progressBar.visibility = View.GONE
            }
        }
        hargaTerendahButton.setOnClickListener{
            if (urutkanBerdasar != "Harga Terendah"){
                pilihButtonUrutkan("Harga Terendah")
                progressBar.visibility = View.VISIBLE
                if (listLaptopFilter.isEmpty()){
                    listLaptop.sortBy{ l -> l.price.filter { it.isDigit() }.toInt() }
                    showRecyclerList()
                }
                else{
                    listLaptop.sortBy{ l -> l.price.filter { it.isDigit() }.toInt() }
                    listLaptopFilter.sortBy{ l -> l.price.filter { it.isDigit() }.toInt() }
                    showRecyclerList(filterBerdasar)
                }
                progressBar.visibility = View.GONE
            }
        }
        performaButton.setOnClickListener{
            if (urutkanBerdasar != "Performa"){
                pilihButtonUrutkan("Performa")
                progressBar.visibility = View.VISIBLE
                if (listLaptopFilter.isEmpty()){
                    listLaptop.sortByDescending{ it.performa }
                    showRecyclerList()
                }
                else{
                    listLaptop.sortByDescending{ it.performa }
                    listLaptopFilter.sortByDescending{ it.performa }
                    showRecyclerList(filterBerdasar)
                }
                progressBar.visibility = View.GONE
            }
        }
        portabilitasButton.setOnClickListener{
            if (urutkanBerdasar != "Portabilitas"){
                pilihButtonUrutkan("Portabilitas")
                progressBar.visibility = View.VISIBLE
                if (listLaptopFilter.isEmpty()){
                    listLaptop.sortByDescending{ it.portabilitas }
                    showRecyclerList()
                }
                else{
                    listLaptop.sortByDescending{ it.portabilitas }
                    listLaptopFilter.sortByDescending{ it.portabilitas }
                    showRecyclerList(filterBerdasar)
                }
                progressBar.visibility = View.GONE
            }
        }

        // Menambahkan event filter
        filterTextView.setOnClickListener{
            if (progressBar.visibility == View.GONE){
                if (isOverlayUrutkan)
                    sembunyikanOverlayUrutkan()
                tampilkanOverlayFilter()
            }
        }
        gamingButton.setOnClickListener{
            if (filterBerdasar != "Gaming")
                filter("Gaming")
            else{
                gamingButton.setTextColor(-6908266) //abu
                gamingButton.setBackgroundResource(R.drawable.bg_button_putih)
                filter("Semua")
            }
        }
        profesionalButton.setOnClickListener{
            if (filterBerdasar != "Profesional")
                filter("Profesional")
            else{
                profesionalButton.setTextColor(-6908266) //abu
                profesionalButton.setBackgroundResource(R.drawable.bg_button_putih)
                filter("Semua")
            }
        }
        pelajarButton.setOnClickListener{
            if (filterBerdasar != "Pelajar")
                filter("Pelajar")
            else{
                pelajarButton.setTextColor(-6908266) //abu
                pelajarButton.setBackgroundResource(R.drawable.bg_button_putih)
                filter("Semua")
            }
        }
        workstationButton.setOnClickListener{
            if (filterBerdasar != "Workstation")
                filter("Workstation")
            else{
                workstationButton.setTextColor(-6908266) //abu
                workstationButton.setBackgroundResource(R.drawable.bg_button_putih)
                filter("Semua")
            }
        }

        // Menambahkan event hitamTransparanLinearLayout
        hitamTransparanLinearLayout.setOnClickListener{
            if (isOverlayUrutkan)
                sembunyikanOverlayUrutkan()
            else
                sembunyikanOverlayFilter()
        }
    }

    // Memanggil data semua laptop dari Firestore sekaligus ditampilkan
    private fun loadLaptopCari(){
        val db = FirebaseFirestore.getInstance()
        db.collection("spekLaptop")
            .orderBy("tanggalRilis", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {result ->
                for (document in result){
                    listLaptop.add(LaptopTerbaru(document.getString("namaLaptop")?: "",
                        document.getString("hargaLaptop")?: "",
                        document.getString("gambar")?: "",
                        document.getString("acadapter")?: "",
                        document.getString("audio")?: "",
                        document.getString("baterai")?: "",
                        document.getString("berat")?: "",
                        document.getString("brand")?: "",
                        document.getString("chipset")?: "",
                        document.getString("cpu")?: "",
                        document.getString("dimensi")?: "",
                        (document.get("grafis")?: arrayListOf("")) as ArrayList<String>,
                        (document.get("io")?: arrayListOf("")) as ArrayList<String>,
                        (document.get("kategori")?: arrayListOf("")) as ArrayList<String>,
                        document.getString("keyboard")?: "",
                        (document.get("komunikasi")?: arrayListOf("")) as ArrayList<String>,
                        document.getString("layar")?: "",
                        document.getString("memori")?: "",
                        document.getString("os")?: "",
                        document.getString("penyimpanan")?: "",
                        document.getString("tanggalRilis")?: "",
                        document.getString("webcam")?: "",
                        (document.getLong("performa")?: 1).toInt(),
                        (document.getLong("portabilitas")?: 1).toInt()))
                }
                if(listLaptop.isNotEmpty()){
                    filterCari()
                }
                else
                    loadLaptopCari()
            }
    }
    private fun filterCari(){
        listLaptop.filter {l: LaptopTerbaru -> !l.name.contains(extra, true) }
            .forEach { listLaptop.remove(it) }
        // Jika tidak ditemukan laptop sesuai masukan pengguna
        if (listLaptop.isEmpty()){
            progressBar.visibility = View.GONE
            val toast = android.widget.Toast.makeText(activity,
                "Tidak ditemukan laptop sesuai masukan Anda pada basis data kami.",
                android.widget.Toast.LENGTH_LONG)
            toast.show()
        }
        else{
            listLaptop.sortBy{ it.name.compareTo(extra, true).absoluteValue }
            showRecyclerList()
            progressBar.visibility = View.GONE
            urutkanBerdasar = "Paling Sesuai"
            palingSesuaiButton.setBackgroundResource(R.drawable.bg_button_ungu)
            palingSesuaiButton.setTextColor(-13434727) //biru
        }
    }

    // Memanggil data laptop dengan brand tertentu dari Firestore sekaligus ditampilkan
    private fun loadLaptopBrand(){
        listLaptop.filter {l: LaptopTerbaru -> !l.brand.equals(extra, true) }
            .forEach { listLaptop.remove(it) }
        showRecyclerList()
        progressBar.visibility = View.GONE
        urutkanBerdasar = "Terbaru"
        terbaruButton.setBackgroundResource(R.drawable.bg_button_ungu)
        terbaruButton.setTextColor(-13434727) //biru
    }

    // Memanggil data laptop dengan kategori tertentu dari Firestore sekaligus ditampilkan
    private fun loadLaptopKategori(){
        showRecyclerList(extra)
        progressBar.visibility = View.GONE
        urutkanBerdasar = "Terbaru"
        terbaruButton.setBackgroundResource(R.drawable.bg_button_ungu)
        terbaruButton.setTextColor(-13434727) //biru
        when (extra) {
            "Gaming" -> {
                filterBerdasar = "Gaming"
                gamingButton.setBackgroundResource(R.drawable.bg_button_ungu)
                gamingButton.setTextColor(-13434727) //biru
            }
            "Pelajar" -> {
                filterBerdasar = "Pelajar"
                pelajarButton.setBackgroundResource(R.drawable.bg_button_ungu)
                pelajarButton.setTextColor(-13434727) //biru
            }
            "Profesional" -> {
                filterBerdasar = "Profesional"
                profesionalButton.setBackgroundResource(R.drawable.bg_button_ungu)
                profesionalButton.setTextColor(-13434727) //biru
            }
            else -> {
                filterBerdasar = "Workstation"
                workstationButton.setBackgroundResource(R.drawable.bg_button_ungu)
                workstationButton.setTextColor(-13434727) //biru
            }
        }
    }

    // Memanggil data-laptop-sesuai-masukan-pengguna dari Firestore sekaligus ditampilkan
    private fun loadLaptopRekomendasi(){
        val db = FirebaseFirestore.getInstance()
        db.collection("rekomenLaptop")
            .get()
            .addOnSuccessListener {result ->
                for (document in result){
                    rekomenLaptop.add(RekomenLaptop(document.getBoolean("2d")?: false,
                        document.getBoolean("3d")?: false,
                        document.getString("brand")?: "",
                        document.getBoolean("gaming")?: false,
                        (document.getLong("harga")?: 0).toInt(),
                        document.getBoolean("kalkulasi")?: false,
                        document.getString("nama")?: "",
                        document.getBoolean("ringan")?: false,
                        document.getBoolean("video")?: false
                    ))
                }
                if (rekomenLaptop.isEmpty())
                    loadLaptopRekomendasi()
                else{
                    // Filter berdasar budget
                    rekomenLaptop.filter {r: RekomenLaptop -> r.harga < min || r.harga > max }
                        .forEach { rekomenLaptop.remove(it) }
                    // Filter berdasar keperluan
                    if (gameBerat || kalkulasiRumit || grafis2D || grafis3D || editingVideo){
                        if (gameBerat)
                            rekomenLaptop.filter { r: RekomenLaptop -> !r.gaming }
                                .forEach { rekomenLaptop.remove(it) }
                        if (kalkulasiRumit)
                            rekomenLaptop.filter { r: RekomenLaptop -> !r.kalkulasi }
                                .forEach { rekomenLaptop.remove(it) }
                        if (grafis2D)
                            rekomenLaptop.filter { r: RekomenLaptop -> !r.grafis2D }
                                .forEach { rekomenLaptop.remove(it) }
                        if (grafis3D)
                            rekomenLaptop.filter { r: RekomenLaptop -> !r.grafis3D }
                                .forEach { rekomenLaptop.remove(it) }
                        if (editingVideo)
                            rekomenLaptop.filter { r: RekomenLaptop -> !r.video }
                                .forEach { rekomenLaptop.remove(it) }
                    }
                    else
                        rekomenLaptop.filter { r: RekomenLaptop -> !r.ringan }
                            .forEach { rekomenLaptop.remove(it) }
                    // Filter berdasar brand
                    if (!isAcer)
                        rekomenLaptop.filter { r: RekomenLaptop -> r.brand == "Acer"}
                            .forEach { rekomenLaptop.remove(it) }
                    if (!isAsus)
                        rekomenLaptop.filter { r: RekomenLaptop -> r.brand == "Asus" }
                            .forEach { rekomenLaptop.remove(it) }
                    if (!isHp)
                        rekomenLaptop.filter { r: RekomenLaptop -> r.brand == "HP" }
                            .forEach { rekomenLaptop.remove(it) }
                    if (!isLenovo)
                        rekomenLaptop.filter {r: RekomenLaptop -> r.brand == "Lenovo"}
                            .forEach { rekomenLaptop.remove(it) }
                    if (!isMsi)
                        rekomenLaptop.filter {r: RekomenLaptop -> r.brand == "MSI"}
                            .forEach { rekomenLaptop.remove(it) }
                    rekomenLaptop.trimToSize()
                    // Jika tidak ditemukan laptop sesuai masukan pengguna
                    if (rekomenLaptop.isEmpty()){
                        progressBar.visibility = View.GONE
                        val toast = android.widget.Toast.makeText(activity,
                            "Tidak ditemukan laptop sesuai masukan Anda pada basis data kami.",
                            android.widget.Toast.LENGTH_LONG)
                        toast.setGravity(android.view.Gravity.BOTTOM,0,130)
                        toast.show()
                    }
                    // Jika ditemukan laptop sesuai masukan pengguna
                    else{
                        val temp: ArrayList<LaptopTerbaru> = arrayListOf()
                        rekomenLaptop.forEach{ getLaptop(temp, it.nama) }
                        listLaptop = temp
                        if (isPerforma){
                            listLaptop.sortByDescending{ it.performa }
                            urutkanBerdasar = "Performa"
                            performaButton.setBackgroundResource(R.drawable.bg_button_ungu)
                            performaButton.setTextColor(-13434727) //biru
                        }
                        else{
                            listLaptop.sortByDescending{ it.portabilitas }
                            urutkanBerdasar = "Portabilitas"
                            portabilitasButton.setBackgroundResource(R.drawable.bg_button_ungu)
                            portabilitasButton.setTextColor(-13434727) //biru
                        }
                        progressBar.visibility = View.GONE
                        showRecyclerList()
                    }
                }
            }
    }

    // Memanggil data satu laptop
    private fun getLaptop(temp: ArrayList<LaptopTerbaru>, namaLaptop: String){
        listLaptop.find{ it.name.equals(namaLaptop, true)}?.let { temp.add(it) }
    }

    // Melakukan filter laptop
    private fun filter(filterBerdasar: String){
        if (filterBerdasar != "Semua"){
            pilihButtonFilter(filterBerdasar)
            progressBar.visibility = View.VISIBLE
            showRecyclerList(filterBerdasar)
            progressBar.visibility = View.GONE
        }
        else{
            this.filterBerdasar = "Semua"
            progressBar.visibility = View.VISIBLE
            listLaptopFilter = arrayListOf()
            showRecyclerList()
            progressBar.visibility = View.GONE
        }
    }

    // Menampilkan laptop-laptop yang diminta pada RecyclerView
    private fun showRecyclerList(){
        hasilRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        val listLaptopTerbaruAdapter = ListLaptopTerbaruAdapter(activity, listLaptop)
        hasilRecyclerView.adapter = listLaptopTerbaruAdapter
    }

    // Menampilkan laptop-laptop sesuai filter pada RecyclerView
    private fun showRecyclerList(filter: String){
        hasilRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        listLaptopFilter = ArrayList(listLaptop.filter{ it.kategori.contains(filter) })
        val listLaptopTerbaruAdapter = ListLaptopTerbaruAdapter(activity, listLaptopFilter)
        hasilRecyclerView.adapter = listLaptopTerbaruAdapter
    }

    private fun tampilkanOverlayUrutkan(){
        urutkanTextView.setBackgroundColor(-6908266) //abu
        urutkanConstraintLayout.visibility = View.VISIBLE
        hitamTransparanLinearLayout.visibility = View.VISIBLE
        isOverlayUrutkan = true
    }

    private fun tampilkanOverlayFilter(){
        filterTextView.setBackgroundColor(-6908266) //abu
        filterConstraintLayout.visibility = View.VISIBLE
        hitamTransparanLinearLayout.visibility = View.VISIBLE
        isOverlayFilter = true
    }

    internal fun sembunyikanOverlayUrutkan(){
        urutkanTextView.setBackgroundColor(-1) //putih
        urutkanConstraintLayout.visibility = View.GONE
        hitamTransparanLinearLayout.visibility = View.GONE
        isOverlayUrutkan = false
    }

    internal fun sembunyikanOverlayFilter(){
        filterTextView.setBackgroundColor(-1) //putih
        filterConstraintLayout.visibility = View.GONE
        hitamTransparanLinearLayout.visibility = View.GONE
        isOverlayFilter = false
    }

    private fun pilihButtonUrutkan(button: String){
        urutkanBerdasar = button
        if (button == "Paling Sesuai"){
            palingSesuaiButton.setTextColor(-13434727) //biru
            palingSesuaiButton.setBackgroundResource(R.drawable.bg_button_ungu)
        }
        else if (palingSesuaiButton.visibility == View.VISIBLE){
            palingSesuaiButton.setTextColor(-6908266) //abu
            palingSesuaiButton.setBackgroundResource(R.drawable.bg_button_putih)
        }
        if (button == "Terbaru"){
            terbaruButton.setTextColor(-13434727) //biru
            terbaruButton.setBackgroundResource(R.drawable.bg_button_ungu)
        }
        else{
            terbaruButton.setTextColor(-6908266) //abu
            terbaruButton.setBackgroundResource(R.drawable.bg_button_putih)
        }
        if (button == "Harga Tertinggi"){
            hargaTertinggiButton.setTextColor(-13434727) //biru
            hargaTertinggiButton.setBackgroundResource(R.drawable.bg_button_ungu)
        }
        else{
            hargaTertinggiButton.setTextColor(-6908266) //abu
            hargaTertinggiButton.setBackgroundResource(R.drawable.bg_button_putih)
        }
        if (button == "Harga Terendah"){
            hargaTerendahButton.setTextColor(-13434727) //biru
            hargaTerendahButton.setBackgroundResource(R.drawable.bg_button_ungu)
        }
        else{
            hargaTerendahButton.setTextColor(-6908266) //abu
            hargaTerendahButton.setBackgroundResource(R.drawable.bg_button_putih)
        }
        if (button == "Performa"){
            performaButton.setTextColor(-13434727) //biru
            performaButton.setBackgroundResource(R.drawable.bg_button_ungu)
        }
        else{
            performaButton.setTextColor(-6908266) //abu
            performaButton.setBackgroundResource(R.drawable.bg_button_putih)
        }
        if (button == "Portabilitas"){
            portabilitasButton.setTextColor(-13434727)
            portabilitasButton.setBackgroundResource(R.drawable.bg_button_ungu)
        }
        else{
            portabilitasButton.setTextColor(-6908266) //abu
            portabilitasButton.setBackgroundResource(R.drawable.bg_button_putih)
        }
    }

    private fun pilihButtonFilter(button: String){
        filterBerdasar = button
        if (button == "Gaming"){
            gamingButton.setTextColor(-13434727) //biru
            gamingButton.setBackgroundResource(R.drawable.bg_button_ungu)
        }
        else{
            gamingButton.setTextColor(-6908266) //abu
            gamingButton.setBackgroundResource(R.drawable.bg_button_putih)
        }
        if (button == "Profesional"){
            profesionalButton.setTextColor(-13434727) //biru
            profesionalButton.setBackgroundResource(R.drawable.bg_button_ungu)
        }
        else{
            profesionalButton.setTextColor(-6908266) //abu
            profesionalButton.setBackgroundResource(R.drawable.bg_button_putih)
        }
        if (button == "Pelajar"){
            pelajarButton.setTextColor(-13434727) //biru
            pelajarButton.setBackgroundResource(R.drawable.bg_button_ungu)
        }
        else{
            pelajarButton.setTextColor(-6908266) //abu
            pelajarButton.setBackgroundResource(R.drawable.bg_button_putih)
        }
        if (button == "Workstation"){
            workstationButton.setTextColor(-13434727) //biru
            workstationButton.setBackgroundResource(R.drawable.bg_button_ungu)
        }
        else{
            workstationButton.setTextColor(-6908266) //abu
            workstationButton.setBackgroundResource(R.drawable.bg_button_putih)
        }
    }
}