package com.mycompany.sistemmanajemenperpustakaan;

/** Satu catatan transaksi; disimpan di Stack agar bisa dilihat/dibatalkan (LIFO). */
public class Transaksi {
    public enum Jenis { PINJAM, KEMBALI }

    private final Jenis jenis;
    private final Buku buku;
    private final String namaMahasiswa;
    private final int hari; // indeks hari (0 = Senin)

    public Transaksi(Jenis jenis, Buku buku, String namaMahasiswa, int hari) {
        this.jenis = jenis;
        this.buku = buku;
        this.namaMahasiswa = namaMahasiswa;
        this.hari = hari;
    }

    public Jenis getJenis() { return jenis; }
    public Buku getBuku() { return buku; }
    public String getNamaMahasiswa() { return namaMahasiswa; }
    public int getHari() { return hari; }

    @Override public String toString() {
        return String.format("[%-6s] %-12s | %s (%s) | hari: %s",
                jenis, namaMahasiswa, buku.getJudul(), buku.getId(), Perpustakaan.HARI[hari]);
    }
}
