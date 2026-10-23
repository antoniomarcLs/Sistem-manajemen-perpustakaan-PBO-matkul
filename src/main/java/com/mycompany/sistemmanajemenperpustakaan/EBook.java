package com.mycompany.sistemmanajemenperpustakaan;

/**
 * PEWARISAN + POLIMORFISME: perilaku berbeda dari BukuCetak,
 * tetapi dipakai lewat tipe yang sama (Buku).
 * E-book tidak punya stok fisik dan tidak menempati rak.
 */
public class EBook extends Buku {

    public EBook(String id, String judul, String pengarang, int kategori) {
        super(id, judul, pengarang, kategori);
    }

    @Override public String getJenis() { return "E-Book"; }
    @Override public int getDurasiPinjam() { return 3; }
    @Override public int hitungDenda(int hariTerlambat) { return Math.max(0, hariTerlambat) * 500; }
    @Override public boolean bisaDipinjam() { return true; }
    @Override public void pinjam() { /* lisensi tak terbatas: tidak ada stok yang berkurang */ }
    @Override public boolean kembalikan() { return true; }
    @Override public boolean sedangDipinjam() { return false; }
    @Override public String getStokText() { return "Digital"; }
    @Override public String getLokasiText() { return "Online"; }
}
