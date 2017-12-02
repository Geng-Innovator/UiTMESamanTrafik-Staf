package com.seladanghijau.uitme_reportstaf.Entiti;

public class Pekerja {

    private long id;
    private String nama, password, no_ic, emel, no_tel_hp, no_tel, pej, no_pekerja;
    private int jawatan, jenis_pekerja;

    public Pekerja() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNo_ic() {
        return no_ic;
    }

    public void setNo_ic(String no_ic) {
        this.no_ic = no_ic;
    }

    public String getEmel() {
        return emel;
    }

    public void setEmel(String emel) {
        this.emel = emel;
    }

    public String getNo_tel_hp() {
        return no_tel_hp;
    }

    public void setNo_tel_hp(String no_tel_hp) {
        this.no_tel_hp = no_tel_hp;
    }

    public String getNo_tel() {
        return no_tel;
    }

    public void setNo_tel(String no_tel) {
        this.no_tel = no_tel;
    }

    public String getPej() {
        return pej;
    }

    public void setPej(String pej) {
        this.pej = pej;
    }

    public String getNo_pekerja() {
        return no_pekerja;
    }

    public void setNo_pekerja(String no_pekerja) {
        this.no_pekerja = no_pekerja;
    }

    public int getJawatan() {
        return jawatan;
    }

    public void setJawatan(int jawatan) {
        this.jawatan = jawatan;
    }

    public int getJenis_pekerja() {
        return jenis_pekerja;
    }

    public void setJenis_pekerja(int jenis_pekerja) {
        this.jenis_pekerja = jenis_pekerja;
    }
}
