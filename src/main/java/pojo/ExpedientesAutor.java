/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aldair
 */
public class ExpedientesAutor {
    private Usuario autor;
    private Usuario usuario;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    private List<Expediente> expedientesEmitidos;

    public ExpedientesAutor() {
        expedientesEmitidos = new ArrayList<>();
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public List<Expediente> getExpedientesEmitidos() {
        return expedientesEmitidos;
    }

    public void setExpedientesEmitidos(List<Expediente> expedientesEmitidos) {
        this.expedientesEmitidos = expedientesEmitidos;
    }
}
