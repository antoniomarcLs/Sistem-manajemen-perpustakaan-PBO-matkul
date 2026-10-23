package com.mycompany.sistemmanajemenperpustakaan;

import java.util.Scanner;
/**
 *
 * @author kila
 */
public class Sistemmanajemenperpustakaan {
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        Perpustakaan p = new Perpustakaan();
        isiDataAwal(p);

        int pilih;
        do {
            tampilMenu(p);
            pilih = bacaInt("Pilih menu: ");
            System.out.println();
            switch (pilih) {
                case 1 -> {
                    String judul = bacaTeks("Judul      : ");
                    String pengarang = bacaTeks("Pengarang  : ");
                    p.tampilkanKategori();
                    int kat = bacaInt("Kategori (nomor): ") - 1;
                    int jenis = bacaInt("Jenis (1=Cetak, 2=E-Book): ");
                switch (jenis) {
                    case 1 -> {
                        int stok = bacaInt("Jumlah eksemplar: ");
                        System.out.println(p.tambahBukuCetak(judul, pengarang, kat, stok));
                    }
                    case 2 -> System.out.println(p.tambahEBook(judul, pengarang, kat));
                    default -> System.out.println("Jenis tidak valid.");
                }
                }
                case 2 -> p.tampilkanBuku();
                case 3 -> System.out.println(p.hapusBuku(bacaTeks("ID buku yang dihapus: ")));
                case 4 -> System.out.println(p.tambahAntrean(
                        bacaTeks("Nama : "), bacaTeks("NIM  : "), bacaTeks("ID buku: ")));
                case 5 -> p.tampilkanAntrean();
                case 6 -> System.out.println(p.layaniAntrean());
                case 7 -> System.out.println(p.kembalikanBuku(
                        bacaTeks("ID buku: "), bacaTeks("Nama peminjam: "), bacaInt("Terlambat berapa hari (0 jika tidak): ")));
                case 8 -> p.lihatTransaksiTerakhir();
                case 9 -> p.tampilkanRiwayat();
                case 10 -> System.out.println(p.batalkanTransaksiTerakhir());
                case 11 -> p.tampilkanStatistik();
                case 12 -> p.tampilkanPetaRak();
                case 13 -> {
                    for (int i = 0; i < Perpustakaan.HARI.length; i++)
                        System.out.println((i + 1) + ". " + Perpustakaan.HARI[i]);
                    p.setHariAktif(bacaInt("Pilih hari (1-7): ") - 1);
                    System.out.println("Hari aktif: " + Perpustakaan.HARI[p.getHariAktif()]);
                }
                case 0 -> System.out.println("Terima kasih.");
                default -> System.out.println("Menu tidak valid.");
            }
            System.out.println();
        } while (pilih != 0);
    }

    private static void tampilMenu(Perpustakaan p) {
        System.out.println("===== PERPUSTAKAAN KAMPUS (hari aktif: " + Perpustakaan.HARI[p.getHariAktif()] + ") =====");
        System.out.println(" 1. Tambah buku            8. Lihat transaksi terakhir");
        System.out.println(" 2. Tampilkan buku         9. Riwayat transaksi");
        System.out.println(" 3. Hapus buku            10. Batalkan transaksi terakhir");
        System.out.println(" 4. Masuk antrean         11. Statistik peminjaman");
        System.out.println(" 5. Lihat antrean         12. Peta lokasi rak");
        System.out.println(" 6. Layani antrean        13. Atur hari simulasi");
        System.out.println(" 7. Kembalikan buku        0. Keluar");
    }

    private static void isiDataAwal(Perpustakaan p) {
        p.tambahBukuCetak("Java: Dasar-Dasar OOP", "Budi Santoso", 0, 2);
        p.tambahBukuCetak("Kalkulus Dasar", "Siti Rahma", 1, 1);
        p.tambahBukuCetak("Jaringan Komputer", "Andi Wijaya", 2, 3);
        p.tambahEBook("Perancangan Basis Data", "Dewi Lestari", 3);
    }

    // Input yang tidak crash saat user mengetik huruf
    private static int bacaInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(in.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Masukkan angka yang valid."); }
        }
    }

    private static String bacaTeks(String prompt) {
        System.out.print(prompt);
        return in.nextLine();
    }
}