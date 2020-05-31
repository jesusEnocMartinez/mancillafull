/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import pojo.Expediente;
import pojo.Solicitud;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import pojo.ErrorClass;

/**
 *
 * @author aldair
 */
public class DAOSolicitud implements Serializable {

    private static DAOSolicitud daosolicitud = null;
    private String url;

    private DAOSolicitud(){
        url = config.ConfigAccess.getRecurso().getValue("app.web");
    }

    public static DAOSolicitud getDaoSolicitud() {
        if (daosolicitud == null) {
            daosolicitud = new DAOSolicitud();
        }

        return daosolicitud;
    }

    public Solicitud getOne(String idSolicitud, String password) throws SQLException, ErrorClass {
        Solicitud solicitud = null;
        String sql = "select * from solicitudes where id_solicitud = ?";
        Connection c = Conexion.getConnection();
        try (PreparedStatement pr = c.prepareStatement(sql)) {
            pr.setString(1, idSolicitud);
            try (ResultSet rs = pr.executeQuery()) {
                if (rs.next()) {
                    solicitud = new Solicitud();
                    solicitud.setPwd(rs.getString(5));
                    if (rs.getString(5).equals(password)) {
                        DAOExpediente daoexp = DAOExpediente.getDAOExpediente();
                        String direccionBlockchain = rs.getString(2);
                        String idExpediente = BusquedasIdsBlockchain.getId(direccionBlockchain, BusquedasIdsBlockchain.BlockToId.EXPEDIENTE);
                        Expediente expediente = daoexp.getOne(idExpediente, "");
                        if (!expediente.isActivo()) {
                            expediente = null;
                        }
                        solicitud.setExpediente(expediente);
                    }else{
                        return null;
                    }
                }
            }
        } 
        
        return solicitud;
    }

    public Expediente login(String idSolicitud, String password) throws SQLException, ErrorClass {
        Solicitud solicitud = getOne(idSolicitud, password);
        return solicitud.getExpediente();
    }

    public boolean post(Solicitud pojo, String id) throws SQLException {
        String sql = "INSERT INTO solicitudes values(?,?,?,?,?,?)";
        Connection c = Conexion.getConnection();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, UUID.randomUUID().toString().substring(0, 9));
            ps.setString(2, BusquedasIdsBlockchain.getDireccionBlockchain(id, BusquedasIdsBlockchain.IdToBlock.EXPEDIENTE));
            Calendar gc = GregorianCalendar.getInstance();
            gc.add(Calendar.DAY_OF_MONTH, 7);
            java.util.Date d = gc.getTime();
            java.sql.Date date = new java.sql.Date(d.getTime());
            ps.setDate(3, date);
            ps.setObject(4, null);
            ps.setString(5, UUID.randomUUID().toString().substring(0, 9));
            ps.setInt(6, pojo.getTipo());
            ps.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DAOSolicitud.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    public boolean delete(String idSolicitud) throws SQLException {
        String sql = "delete from solicitudes where id_solicitud=?";
        Connection c = Conexion.getConnection();
        try (PreparedStatement ps = c.prepareCall(sql)) {
            ps.setString(1, idSolicitud);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DAOSolicitud.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    public List<Solicitud> getAllByUsuario(String idUsuario) throws SQLException {

        String sql = "select * from\n"
                + "(select * from solicitudes\n"
                + ") as solicitudespendientes\n"
                + "inner join expedientes\n"
                + "on expedientes.direccion_blockchain_expediente = solicitudespendientes.direccion_blockchain\n"
                + "where direccion_blockchain_usuario=? "
                + "order by (case when solicitudespendientes.estatus then 1 when solicitudespendientes.estatus is null then 2 else 3 end) asc";

        List<Solicitud> solicitudes = new ArrayList<>();
        String idUsuarioBlockchain = BusquedasIdsBlockchain.getDireccionBlockchain(idUsuario, BusquedasIdsBlockchain.IdToBlock.USUARIO);
        try (PreparedStatement ps = Conexion.getConnection().prepareStatement(sql)) {
            ps.setString(1, idUsuarioBlockchain);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Solicitud sol = new Solicitud();
                    sol.setIdSolicitud(rs.getString(1));
                    sol.setFechaLimite(rs.getDate(3));
                    sol.setPwd(rs.getString(5));
                    if (rs.getObject(4) != null) {
                        sol.setEstatus(rs.getBoolean(4));
                    }
                    sol.setUrl(url);
                    Expediente e = DAOExpediente.getDAOExpediente().getOne(rs.getString(7));
                    sol.setIdExpediente(e.getTitulo());
                    sol.setExpediente(e);
                    solicitudes.add(sol);
                }
                return solicitudes;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DAOSolicitud.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    public void update(Solicitud solicitud) throws SQLException {
        String sql;
        sql = "update solicitudes set estatus = ?, fecha_limite=? where id_solicitud = ?";
        try (PreparedStatement pr = Conexion.getConnection().prepareStatement(sql)) {
            Calendar gc = GregorianCalendar.getInstance();
            gc.add(Calendar.DAY_OF_MONTH, 7);
            java.util.Date d = gc.getTime();
            java.sql.Date date = new java.sql.Date(d.getTime());
            pr.setBoolean(1, (boolean) solicitud.isEstatus());
            pr.setDate(2, date);
            pr.setString(3, solicitud.getIdSolicitud());
            pr.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DAOSolicitud.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }

    }

    //Retorna la lista de solicitudes que debe aprobar/rechazar un autor
    public List<Solicitud> check( String idAutor) throws SQLException {

        StringBuilder sql = new StringBuilder("select * from "
                + "(select * from solicitudes where estatus is null) as sols "
                + "inner join expedientes "
                + "on sols.direccion_blockchain=expedientes.direccion_blockchain_expediente "
                + "where expedientes.direccion_blockchain_autor = ? ");
        List<Solicitud> solicitudes = new ArrayList<>();
        try (PreparedStatement ps = Conexion.getConnection().prepareStatement(sql.toString())) {
            String direccion = BusquedasIdsBlockchain.getDireccionBlockchain(idAutor, BusquedasIdsBlockchain.IdToBlock.USUARIO);
            ps.setString(1, direccion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Solicitud sol = new Solicitud();
                    sol.setIdSolicitud(rs.getString(1));
                    sol.setFechaLimite(rs.getDate(3));
                    sol.setEstatus(rs.getBoolean(4));
                    sol.setPwd(rs.getString(5));
                    Expediente e = DAOExpediente.getDAOExpediente().getOne(rs.getString(7));
                    sol.setIdExpediente(e.getTitulo());
                    sol.setUrl(url);
                    sol.setExpediente(e);
                    solicitudes.add(sol);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DAOSolicitud.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        return solicitudes;
    }

}
