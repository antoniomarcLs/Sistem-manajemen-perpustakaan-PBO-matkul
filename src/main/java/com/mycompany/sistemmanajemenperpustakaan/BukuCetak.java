package com.mycompany.sistemmanajemenperpustakaan;

/**
 * PEWARISAN: BukuCetak mewarisi Buku.
 * POLIMORFISME: method abstrak di-override dengan perilaku khusus buku fisik.
 */
public class BukuCetak extends Buku {
    private final int totalStok;
    private int stok;
    private int lantai, rak, posisi; // lokasi di array 3D (indeks mulai 0)

    public BukuCetak(String id, String judul, String pengarang, int kategori, int totalStok) {
        super(id, judul, pengarang, kategori);
        if (totalStok <= 0) throw new IllegalArgumentException("Stok harus lebih dari 0.");
        this.totalStok = totalStok;
        this.stok = totalStok;
    }

    public void setLokasi(int lantai, int rak, int posisi) {
        this.lantai = lantai;
        this.rak = rak;
        this.posisi = posisi;
    }

    @Override public String getJenis() { return "Cetak"; }
    @Override public int getDurasiPinjam() { return 7; }
    @Override public int hitungDenda(int hariTerlambat) { return Math.max(0, hariTerlambat) * 1000; }
    @Override public boolean bisaDipinjam() { return stok > 0; }

    @Override public void pinjam() {
        if (stok <= 0) throw new IllegalStateException("Stok habis.");
        stok--;
    }

    @Override public boolean kembalikan() {
        if (stok >= totalStok) return false; // semua eksemplar sudah di perpustakaan
        stok++;
        return true;
    }

    @Override public boolean sedangDipinjam() { return stok < totalStok; }
    @Override public String getStokText() { return stok + "/" + totalStok; }

    @Override public String getLokasiText() {
        return "L" + (lantai + 1) + "-R" + (rak + 1) + "-P" + (posisi + 1);
    }
}
