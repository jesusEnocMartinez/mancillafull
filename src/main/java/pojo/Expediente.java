/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojo;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 *
 * @author aldair
 */
public class Expediente {
    private String idExpediente;
    private String titulo;
    private Date fechaCreacion;
    private String temas;
    private String idUsuario;
    private String idAutor;
    private Usuario visor;
    private Usuario emisor;
    private List<Documento> documentos;
    private String direccionBlockchain;
    private boolean activo;
    private Timestamp fechaUtc;

    public Timestamp getFechaUtc() {
        return fechaUtc;
    }

    public void setFechaUtc(Timestamp fechaPrueba) {
        this.fechaUtc = fechaPrueba;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean estado) {
        this.activo = estado;
    }
    
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(String idAutor) {
        this.idAutor = idAutor;
    }

    public String getDireccionBlockchain() {
        return direccionBlockchain;
    }

    public void setDireccionBlockchain(String direccionBlockchain) {
        this.direccionBlockchain = direccionBlockchain;
    }

    public List<Documento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<Documento> documentos) {
        this.documentos = documentos;
    }

    public String getIdExpediente() {
        return idExpediente;
    }

    public void setIdExpediente(String id) {
        this.idExpediente = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getTemas() {
        return temas;
    }

    public void setTemas(String temas) {
        this.temas = temas;
    }

    public Usuario getVisor() {
        return visor;
    }

    public void setVisor(Usuario visor) {
        this.visor = visor;
    }

    public Usuario getEmisor() {
        return emisor;
    }

    public void setEmisor(Usuario emisor) {
        this.emisor = emisor;
    }

}
