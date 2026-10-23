package com.mycompany.sistemmanajemenperpustakaan;

/** Data mahasiswa yang berada di antrean peminjaman. Immutable agar aman. */
public class Mahasiswa {
    private final String nama;
    private final String nim;
    private final String idBuku;

    public Mahasiswa(String nama, String nim, String idBuku) {
        if (nama == null || nama.trim().isEmpty() || nim == null || nim.trim().isEmpty())
            throw new IllegalArgumentException("Nama dan NIM wajib diisi.");
        this.nama = nama.trim();
        this.nim = nim.trim();
        this.idBuku = idBuku.trim().toUpperCase();
    }

    public String getNama() { return nama; }
    public String getNim() { return nim; }
    public String getIdBuku() { return idBuku; }

    @Override public String toString() {
        return nama + " (" + nim + ") -> ingin meminjam " + idBuku;
    }
}
