/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojo;

/**
 *
 * @author aldair
 */
public class Usuario {

    private String idUsuario;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String biometria;
    private String pasw;
    private String email;
    private String telCasa;
    private String telMovil;
    private String direccionBlockchain;
    private Perfil idPerfil;
    private boolean activo;
    private String sal;

    public String getSal() {
        return sal;
    }

    public void setSal(String sal) {
        this.sal = sal;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    

    public enum Perfil {
        ADMINISTRADOR("ADMINISTRADOR"),
        USUARIO("USUARIO"),
        AUTOR("AUTOR");
        private final String typo;

        Perfil(String ad) {
            this.typo = ad;
        }

        public String getPerfil() {
            return typo;
        }

    }

    public static Perfil toPerfil(String perfil) {
        switch (perfil.trim()) {
            case "ADMINISTRADOR":
                return Perfil.ADMINISTRADOR;
            case "USUARIO":
                return Perfil.USUARIO;
            case "AUTOR":
                return Perfil.AUTOR;
            default:
                return null;
        }
    }

    public Perfil getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(Perfil idPerfil) {
        this.idPerfil = idPerfil;
    }

    public String getDireccionBlockchain() {
        return direccionBlockchain;
    }

    public void setDireccionBlockchain(String direccionBlockchain) {
        this.direccionBlockchain = direccionBlockchain;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getBiometria() {
        return biometria;
    }

    public void setBiometria(String biometria) {
        this.biometria = biometria;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelCasa() {
        return telCasa;
    }

    public void setTelCasa(String telCasa) {
        this.telCasa = telCasa;
    }

    public String getTelMovil() {
        return telMovil;
    }

    public void setTelMovil(String telMovil) {
        this.telMovil = telMovil;
    }

    public String getPasw() {
        return pasw;
    }

    public void setPasw(String password) {
        this.pasw = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

}
