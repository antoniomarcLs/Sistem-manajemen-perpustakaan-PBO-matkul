package com.mycompany.sistemmanajemenperpustakaan;

/**
 * ABSTRAKSI + ENKAPSULASI
 * Buku adalah kelas abstrak: tidak boleh di-instantiate langsung,
 * hanya mendefinisikan "kontrak" yang harus dipenuhi semua jenis buku.
 * Semua field private; perubahan hanya lewat setter yang divalidasi.
 */
public abstract class Buku {
    private final String id;
    private String judul;
    private String pengarang;
    private int kategori; // indeks ke array Perpustakaan.KATEGORI

    protected Buku(String id, String judul, String pengarang, int kategori) {
        this.id = id;
        setJudul(judul);
        setPengarang(pengarang);
        this.kategori = kategori;
    }

    // ---------- Getter & Setter (Enkapsulasi) ----------
    public String getId() { return id; }
    public String getJudul() { return judul; }
    public String getPengarang() { return pengarang; }
    public int getKategori() { return kategori; }

    public void setJudul(String judul) {
        if (judul == null || judul.trim().isEmpty())
            throw new IllegalArgumentException("Judul tidak boleh kosong.");
        this.judul = judul.trim();
    }

    public void setPengarang(String pengarang) {
        if (pengarang == null || pengarang.trim().isEmpty())
            throw new IllegalArgumentException("Pengarang tidak boleh kosong.");
        this.pengarang = pengarang.trim();
    }

    // ---------- Kontrak abstrak (Abstraksi) ----------
    public abstract String getJenis();
    public abstract int getDurasiPinjam();              // dalam hari
    public abstract int hitungDenda(int hariTerlambat);  // dalam rupiah
    public abstract boolean bisaDipinjam();
    public abstract void pinjam();
    public abstract boolean kembalikan();               // false jika tidak ada yang sedang dipinjam
    public abstract boolean sedangDipinjam();
    public abstract String getStokText();
    public abstract String getLokasiText();
}
