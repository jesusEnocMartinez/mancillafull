/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojo;

import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author aldair
 */
public class Documento {

    private String id;
    private String direccionBlockchain;
    private String nomDocumento;
    private Date fechaCreacion;
    private Date fechaIncorporacion;
    private String descripcion;
    private String formato;
    private int tamano;
    private int numPaginas;
    private int nivelConfidencialidad;
    private String archivob64;
    private boolean activo;
    private Timestamp fechautc;

    public Timestamp getFechautc() {
        return fechautc;
    }

    public void setFechautc(Timestamp fechautc) {
        this.fechautc = fechautc;
    }
    

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getArchivob64() {
        return archivob64;
    }

    public void setArchivob64(String archivob64) {
        this.archivob64 = archivob64;
    }

    public int getNivelConfidencialidad() {
        return nivelConfidencialidad;
    }

    public void setNivelConfidencialidad(int nivelConfidencialidad) {
        this.nivelConfidencialidad = nivelConfidencialidad;
    }

    public String getIdDocumento() {
        return id;
    }

    public void setIdDocumento(String id) {
        this.id = id;
    }

    public String getNomDocumento() {
        return nomDocumento;
    }

    public void setNomDocumento(String nombreRecurso) {
        this.nomDocumento = nombreRecurso;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaIncorporacion() {
        return fechaIncorporacion;
    }

    public void setFechaIncorporacion(Date fechaIncorporacion) {
        this.fechaIncorporacion = fechaIncorporacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getDireccionBlockchain() {
        return direccionBlockchain;
    }

    public void setDireccionBlockchain(String direccionBlockchain) {
        this.direccionBlockchain = direccionBlockchain;
    }

    public int getTamano() {
        return tamano;
    }

    public void setTamano(int tamano) {
        this.tamano = tamano;
    }

    public int getNoPaginas() {
        return numPaginas;
    }

    public void setNoPaginas(int numPaginas) {
        this.numPaginas = numPaginas;
    }

    @Override
    public String toString() {
        return "nombre: " + nomDocumento + "\nid_documento" + id;
    }

}
