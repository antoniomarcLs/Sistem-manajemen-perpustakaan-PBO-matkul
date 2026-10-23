package com.mycompany.sistemmanajemenperpustakaan;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Kelas inti yang memegang ke-6 struktur data:
 *  1. Array 1D        -> KATEGORI, HARI
 *  2. Array 2D        -> peminjaman[kategori][hari]
 *  3. Array 3D        -> lokasi[lantai][rak][posisi]
 *  4. ArrayList       -> daftarBuku
 *  5. Stack (LIFO)    -> riwayat
 *  6. Queue (FIFO)    -> antrean
 */
public class Perpustakaan {
    // 1D
    public static final String[] KATEGORI = {"Pemrograman", "Matematika", "Jaringan Komputer", "Basis Data"};
    public static final String[] HARI = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};

    private static final int LANTAI = 3, RAK = 4, POSISI = 5;

    private final int[][] peminjaman = new int[KATEGORI.length][HARI.length];          // 2D
    private final String[][][] lokasi = new String[LANTAI][RAK][POSISI];               // 3D (isi: id buku / null)
    private final ArrayList<Buku> daftarBuku = new ArrayList<>();                      // ArrayList
    private final Stack<Transaksi> riwayat = new Stack<>();                            // Stack
    private final Queue<Mahasiswa> antrean = new LinkedList<>();                       // Queue

    private int hariAktif = java.time.LocalDate.now().getDayOfWeek().getValue() - 1;
    private int counterId = 1;

    // =====================================================================
    //  ArrayList: kelola buku
    // =====================================================================
    public String tambahBukuCetak(String judul, String pengarang, int kategori, int stok) {
        if (!kategoriValid(kategori)) return "Kategori tidak valid.";
        int[] slot = cariSlotKosong();
        if (slot == null) return "Gagal: semua rak penuh (" + (LANTAI * RAK * POSISI) + " posisi terisi).";
        try {
            String id = String.format("B%03d", counterId);
            BukuCetak b = new BukuCetak(id, judul, pengarang, kategori, stok);
            b.setLokasi(slot[0], slot[1], slot[2]);
            lokasi[slot[0]][slot[1]][slot[2]] = id;
            daftarBuku.add(b);
            counterId++;
            return "Buku cetak ditambahkan: " + id + " di lokasi " + b.getLokasiText();
        } catch (IllegalArgumentException e) {
            return "Gagal: " + e.getMessage();
        }
    }

    public String tambahEBook(String judul, String pengarang, int kategori) {
        if (!kategoriValid(kategori)) return "Kategori tidak valid.";
        try {
            String id = String.format("B%03d", counterId);
            daftarBuku.add(new EBook(id, judul, pengarang, kategori));
            counterId++;
            return "E-Book ditambahkan: " + id;
        } catch (IllegalArgumentException e) {
            return "Gagal: " + e.getMessage();
        }
    }

    public void tampilkanBuku() {
        if (daftarBuku.isEmpty()) { System.out.println("Belum ada buku."); return; }
        System.out.printf("%-6s %-8s %-26s %-18s %-18s %-8s %s%n",
                "ID", "Jenis", "Judul", "Pengarang", "Kategori", "Stok", "Lokasi");
        System.out.println("-".repeat(98));
        for (Buku b : daftarBuku) { // polimorfisme: tiap objek memakai versinya sendiri
            System.out.printf("%-6s %-8s %-26s %-18s %-18s %-8s %s%n",
                    b.getId(), b.getJenis(), potong(b.getJudul(), 26), potong(b.getPengarang(), 18),
                    KATEGORI[b.getKategori()], b.getStokText(), b.getLokasiText());
        }
    }

    public String hapusBuku(String id) {
        Buku b = cariBuku(id);
        if (b == null) return "Buku tidak ditemukan.";
        if (b.sedangDipinjam()) return "Gagal: buku masih ada yang meminjam.";
        daftarBuku.remove(b);
        for (int i = 0; i < LANTAI; i++)
            for (int j = 0; j < RAK; j++)
                for (int k = 0; k < POSISI; k++)
                    if (b.getId().equals(lokasi[i][j][k])) lokasi[i][j][k] = null;
        return "Buku " + b.getId() + " (" + b.getJudul() + ") dihapus.";
    }

    // =====================================================================
    //  Queue: antrean peminjaman
    // =====================================================================
    public String tambahAntrean(String nama, String nim, String idBuku) {
        try {
            Mahasiswa m = new Mahasiswa(nama, nim, idBuku);
            if (cariBuku(m.getIdBuku()) == null) return "Gagal: buku " + m.getIdBuku() + " tidak ada.";
            antrean.offer(m);
            return nama + " masuk antrean nomor " + antrean.size() + ".";
        } catch (IllegalArgumentException e) {
            return "Gagal: " + e.getMessage();
        }
    }

    public void tampilkanAntrean() {
        if (antrean.isEmpty()) { System.out.println("Antrean kosong."); return; }
        int no = 1;
        for (Mahasiswa m : antrean) System.out.println(no++ + ". " + m);
    }

    /** Melayani mahasiswa paling depan (FIFO). */
    public String layaniAntrean() {
        Mahasiswa m = antrean.poll();
        if (m == null) return "Antrean kosong.";
        Buku b = cariBuku(m.getIdBuku());
        if (b == null) return m.getNama() + ": buku sudah tidak tersedia di koleksi.";
        if (!b.bisaDipinjam())
            return m.getNama() + ": stok '" + b.getJudul() + "' habis. Dikeluarkan dari antrean, silakan daftar ulang.";

        b.pinjam();
        peminjaman[b.getKategori()][hariAktif]++;
        riwayat.push(new Transaksi(Transaksi.Jenis.PINJAM, b, m.getNama(), hariAktif));
        return m.getNama() + " meminjam '" + b.getJudul() + "'. Durasi " + b.getDurasiPinjam() + " hari.";
    }

    // =====================================================================
    //  Pengembalian
    // =====================================================================
    public String kembalikanBuku(String id, String nama, int hariTerlambat) {
        Buku b = cariBuku(id);
        if (b == null) return "Buku tidak ditemukan.";
        if (!b.kembalikan()) return "Gagal: tidak ada eksemplar '" + b.getJudul() + "' yang sedang dipinjam.";
        riwayat.push(new Transaksi(Transaksi.Jenis.KEMBALI, b, nama, hariAktif));
        int denda = b.hitungDenda(hariTerlambat); // polimorfisme
        return "'" + b.getJudul() + "' dikembalikan oleh " + nama
                + (denda > 0 ? ". Denda: Rp" + denda : ". Tidak ada denda.");
    }

    // =====================================================================
    //  Stack: riwayat transaksi
    // =====================================================================
    public void lihatTransaksiTerakhir() {
        if (riwayat.isEmpty()) System.out.println("Belum ada transaksi.");
        else System.out.println("Transaksi terakhir: " + riwayat.peek());
    }

    public void tampilkanRiwayat() {
        if (riwayat.isEmpty()) { System.out.println("Belum ada transaksi."); return; }
        for (int i = riwayat.size() - 1; i >= 0; i--) // dari yang terbaru
            System.out.println((riwayat.size() - i) + ". " + riwayat.get(i));
    }

    public String batalkanTransaksiTerakhir() {
        if (riwayat.isEmpty()) return "Tidak ada transaksi untuk dibatalkan.";
        Transaksi t = riwayat.peek();
        Buku b = t.getBuku();
        if (!daftarBuku.contains(b)) return "Gagal: buku pada transaksi ini sudah dihapus dari koleksi.";

        if (t.getJenis() == Transaksi.Jenis.PINJAM) {
            b.kembalikan();
            peminjaman[b.getKategori()][t.getHari()]--;
        } else {
            if (!b.bisaDipinjam()) return "Gagal membatalkan: stok buku sudah dipinjam orang lain.";
            b.pinjam();
        }
        riwayat.pop();
        return "Dibatalkan: " + t;
    }

    // =====================================================================
    //  Array 2D & 3D: laporan
    // =====================================================================
    public void tampilkanStatistik() {
        System.out.printf("%-20s", "Kategori");
        for (String h : HARI) System.out.printf("%-8s", h.substring(0, 3));
        System.out.println("Total");
        System.out.println("-".repeat(20 + 8 * HARI.length + 5));
        int[] totalHari = new int[HARI.length];
        int grand = 0;
        for (int i = 0; i < KATEGORI.length; i++) {
            System.out.printf("%-20s", KATEGORI[i]);
            int total = 0;
            for (int j = 0; j < HARI.length; j++) {
                System.out.printf("%-8d", peminjaman[i][j]);
                total += peminjaman[i][j];
                totalHari[j] += peminjaman[i][j];
            }
            System.out.println(total);
            grand += total;
        }
        System.out.println("-".repeat(20 + 8 * HARI.length + 5));
        System.out.printf("%-20s", "Total");
        for (int v : totalHari) System.out.printf("%-8d", v);
        System.out.println(grand);
    }

    public void tampilkanPetaRak() {
        for (int i = 0; i < LANTAI; i++) {
            System.out.println("Lantai " + (i + 1));
            for (int j = 0; j < RAK; j++) {
                System.out.print("  Rak " + (j + 1) + ": ");
                for (int k = 0; k < POSISI; k++)
                    System.out.printf("[%-4s]", lokasi[i][j][k] == null ? "--" : lokasi[i][j][k]);
                System.out.println();
            }
        }
    }

    public void tampilkanKategori() {
        for (int i = 0; i < KATEGORI.length; i++) System.out.println((i + 1) + ". " + KATEGORI[i]);
    }

    // =====================================================================
    //  Helper
    // =====================================================================
    public void setHariAktif(int hari) { if (hari >= 0 && hari < HARI.length) hariAktif = hari; }
    public int getHariAktif() { return hariAktif; }

    private Buku cariBuku(String id) {
        if (id == null) return null;
        for (Buku b : daftarBuku) if (b.getId().equalsIgnoreCase(id.trim())) return b;
        return null;
    }

    private boolean kategoriValid(int k) { return k >= 0 && k < KATEGORI.length; }

    private int[] cariSlotKosong() {
        for (int i = 0; i < LANTAI; i++)
            for (int j = 0; j < RAK; j++)
                for (int k = 0; k < POSISI; k++)
                    if (lokasi[i][j][k] == null) return new int[]{i, j, k};
        return null;
    }

    private String potong(String s, int max) { return s.length() <= max ? s : s.substring(0, max - 1) + "…"; }
}
