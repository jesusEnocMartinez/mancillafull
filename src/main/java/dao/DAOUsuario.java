/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import client.EthUsuario;
import config.Hasher;
import java.io.IOException;
import pojo.Usuario;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jettison.json.JSONException;
import pojo.ErrorClass;

/**
 *
 * @author aldair
 */
public class DAOUsuario implements Serializable {

    private static DAOUsuario daousuario;

    private DAOUsuario() {
    }

    public static DAOUsuario getDaoUsuario() {
        if (daousuario == null) {
            daousuario = new DAOUsuario();
        }
        return daousuario;
    }

    public Usuario login(String idUsuario, String password) throws SQLException, IOException, JSONException, ErrorClass, NoSuchAlgorithmException {
        Usuario u = null;
        ErrorClass err = new ErrorClass();
        Usuario user = getOneLogin(idUsuario);
        boolean exito = false;
        if (user != null) {
            if (user.isActivo()) {
                exito = Hasher.passwordsMatch(user.getSal(), password, user.getPasw(), "SHA-256");
            }
            if (exito) {
                EthUsuario eth = EthUsuario.getEthUsuario();
                String dir = BusquedasIdsBlockchain.getDireccionBlockchain(idUsuario, BusquedasIdsBlockchain.IdToBlock.USUARIO);
                u = eth.getUsuario(dir);
                if(u == null)
                {
                    err.setCode("404");
                    err.setMessage("El usuario no fue encontrado en blockchain");
                    throw err;
                }
                u.setDireccionBlockchain(dir);//Cambio
                u.setTelCasa(user.getTelCasa());
                u.setTelMovil(user.getTelMovil());
                return u;
            } else {
                err.setCode("404");
                err.setMessage("La contraseña no coincide con el login especificado");
                throw err;
            }
        } else {
            err.setCode("404");
            err.setMessage("No existe el usuario especificado");
            throw err;
        }

    }

    public boolean post(Usuario pojo) throws IOException, ErrorClass, JSONException, NoSuchAlgorithmException, SQLException {

        Hasher.HashedPair passw = Hasher.digestPassword(pojo.getPasw(), "SHA-256");
        Connection con = Conexion.getConnection();

        EthUsuario ethcli = EthUsuario.getEthUsuario();
        String direccionBlockchainUsuario = ethcli.createUsuario(pojo);

        String sql = "insert into usuarios(id_usuario,direccion_blockchain_usuario,nombre,ap_paterno,ap_materno,pasw ,tel_casa ,tel_movil ,"
                + "email,id_perfil,administrador,activo,sal) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pr = con.prepareStatement(sql)) {
            pr.setString(1, pojo.getIdUsuario());
            pr.setString(2, direccionBlockchainUsuario);
            pr.setString(3, pojo.getNombre());
            pr.setString(4, pojo.getApellidoPaterno());
            pr.setString(5, pojo.getApellidoMaterno());
            pr.setString(6, passw.getDigestedPassword());
            pr.setString(7, pojo.getTelCasa());
            pr.setString(8, pojo.getTelMovil());
            pr.setString(9, pojo.getEmail());
            pr.setString(10, pojo.getIdPerfil().getPerfil());
            if (pojo.getIdPerfil().getPerfil().equals("ADMINISTRADOR")) {
                pr.setInt(11, 1);
            } else {
                pr.setInt(11, 0);
            }
            pr.setBoolean(12, true);
            pr.setString(13, passw.getB64Salt());
            pr.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DAOUsuario.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }

    }

    public void upUser(String idUsuario) throws SQLException, ErrorClass, IOException {
        String sql = "update usuarios set activo = true where direccion_blockchain_usuario = ?";
        try (PreparedStatement pr = Conexion.getConnection().prepareStatement(sql)) {
            String direccion = BusquedasIdsBlockchain.getDireccionBlockchain(idUsuario, BusquedasIdsBlockchain.IdToBlock.USUARIO);
            EthUsuario.getEthUsuario().enableUsuario(direccion);
            pr.setString(1, direccion);
            pr.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DAOUsuario.class
                    .getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    public boolean delete(String idUsuario) throws SQLException, ErrorClass, IOException {
        String sql = "update usuarios set activo = false where direccion_blockchain_usuario = ?";
        try (PreparedStatement pr = Conexion.getConnection().prepareStatement(sql)) {
            String direccion = BusquedasIdsBlockchain.getDireccionBlockchain(idUsuario, BusquedasIdsBlockchain.IdToBlock.USUARIO);
            EthUsuario.getEthUsuario().disableUsuario(direccion);
            pr.setString(1, direccion);
            pr.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DAOUsuario.class
                    .getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    public boolean put(String idUsuario, Usuario pojo) throws SQLException {
        String sql = "update usuarios set "
                + "nombre = ?,"
                + "ap_paterno=?,"
                + "ap_materno=?, "
                + "pasw=?,"
                + "tel_casa = ?,"
                + "tel_movil = ?,"
                + "email=?,"
                + "id_perfil=?,"
                + "activo = ?"
                + " where id_usuario = ?";
        try (PreparedStatement pr = Conexion.getConnection().prepareStatement(sql)) {
            pr.setString(1, pojo.getNombre());
            pr.setString(2, pojo.getApellidoPaterno());
            pr.setString(3, pojo.getApellidoMaterno());
            pr.setString(4, pojo.getPasw());
            pr.setString(5, pojo.getTelCasa());
            pr.setString(6, pojo.getTelMovil());
            pr.setString(7, pojo.getEmail());
            pr.setString(8, pojo.getIdPerfil().getPerfil());
            pr.setString(10, idUsuario);
            pr.setBoolean(9, pojo.isActivo());
            pr.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DAOUsuario.class
                    .getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    public Usuario getOneEth(String idUsuario) throws JSONException, ErrorClass, SQLException {
        Usuario u;
        EthUsuario eth = EthUsuario.getEthUsuario();
        String dir = BusquedasIdsBlockchain.getDireccionBlockchain(idUsuario, BusquedasIdsBlockchain.IdToBlock.USUARIO);
        u = eth.getUsuario(dir);
        u.setDireccionBlockchain(dir);
        return u;
    }

    private static final String QUERY = "select * from usuarios where id_usuario=?";

    public Usuario getOne(String idUsuario) throws SQLException {
        Usuario u = null;
        try (PreparedStatement pr = Conexion.getConnection().prepareStatement(QUERY)) {
            pr.setString(1, idUsuario);
            try (ResultSet rs = pr.executeQuery()) {
                if (rs.next()) {
                    u = new Usuario();
                    u.setIdUsuario(rs.getString(1));
                    u.setNombre(rs.getString(3));
                    u.setApellidoPaterno(rs.getString(4));
                    u.setApellidoMaterno(rs.getString(5));
                    u.setTelCasa(rs.getString(8));
                    u.setTelMovil(rs.getString(9));
                    u.setEmail(rs.getString(10));
                    u.setIdPerfil(Usuario.toPerfil(rs.getString(11)));
                    u.setActivo(rs.getBoolean(13));
                }
            }
            return u;
        } catch (SQLException ex) {
            Logger.getLogger(DAOExpediente.class
                    .getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    private Usuario getOneLogin(String idUsuario) throws SQLException {
        Usuario u = null;
        try (PreparedStatement pr = Conexion.getConnection().prepareStatement(QUERY)) {
            pr.setString(1, idUsuario);
            try (ResultSet rs = pr.executeQuery()) {
                if (rs.next()) {
                    u = new Usuario();
                    u.setIdUsuario(rs.getString(1));
                    u.setDireccionBlockchain(rs.getString(2));
                    u.setNombre(rs.getString(3));
                    u.setApellidoPaterno(rs.getString(4));
                    u.setApellidoMaterno(rs.getString(5));
                    u.setPasw(rs.getString(6));
                    u.setTelCasa(rs.getString(8));
                    u.setTelMovil(rs.getString(9));
                    u.setEmail(rs.getString(10));
                    u.setIdPerfil(Usuario.toPerfil(rs.getString(11)));
                    u.setActivo(rs.getBoolean(13));
                    u.setSal(rs.getString(14));
                }
            }
            return u;
        } catch (SQLException ex) {
            Logger.getLogger(DAOUsuario.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    public Usuario get(String idUsuario) throws SQLException, IOException, ErrorClass, JSONException {
        Usuario u = null;
        try (PreparedStatement pr = Conexion.getConnection().prepareStatement(QUERY)) {
            pr.setString(1, idUsuario);
            try (ResultSet rs = pr.executeQuery()) {
                if (rs.next()) {
                    u = getOneEth(idUsuario);
                    u.setTelCasa(rs.getString(8));
                    u.setTelMovil(rs.getString(9));
                    u.setEmail(rs.getString(10));
                    u.setActivo(rs.getBoolean(13));
                }
            }
            return u;
        } catch (SQLException | ErrorClass | JSONException ex) {
            Logger.getLogger(DAOExpediente.class
                    .getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    public List<Usuario> getAll() throws SQLException {
        String sql = "select * from usuarios";
        ArrayList<Usuario> usuarios = new ArrayList<>();
        Connection c = Conexion.getConnection();

        try (PreparedStatement pr = c.prepareStatement(sql)) {
            resultSetToList(pr, usuarios);
        } catch (SQLException ex) {
            Logger.getLogger(DAOUsuario.class
                    .getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        return usuarios;

    }

    public enum Perfil {
        ADMINISTRADOR("ADMINISTRADOR"),
        USUARIO("USUARIO"),
        AUTOR("AUTOR");
        private final String tipo;

        Perfil(String ad) {
            this.tipo = ad;
        }

        public String getPerfil() {
            return tipo;
        }
    }

    private void resultSetToList(PreparedStatement pr, List<Usuario> usuarios) throws SQLException {
        try (ResultSet rs = pr.executeQuery()) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getString(1));
                u.setNombre(rs.getString(3));
                u.setApellidoPaterno(rs.getString(4));
                u.setApellidoMaterno(rs.getString(5));
                u.setTelCasa(rs.getString(8));
                u.setTelMovil(rs.getString(9));
                u.setEmail(rs.getString(10));
                u.setIdPerfil(Usuario.toPerfil(rs.getString(11)));
                u.setActivo(rs.getBoolean(13));
                usuarios.add(u);
            }
        }

    }

    public List<Usuario> getAllType(Perfil perfil) throws SQLException {
        String sql = "select * from usuarios where id_perfil = ?";
        List<Usuario> usuarios = new ArrayList<>();
        Connection c = Conexion.getConnection();
        try (PreparedStatement pr = c.prepareStatement(sql)) {
            pr.setString(1, perfil.getPerfil());
            resultSetToList(pr, usuarios);
        } catch (SQLException ex) {
            Logger.getLogger(DAOUsuario.class
                    .getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        return usuarios;
    }

    public List<Usuario> getAndFilter(List<Filtro> filtros) throws SQLException {
        int i = 1;
        List<Usuario> usuarios = new ArrayList<>();
        PreparedStatement ps = null;
        StringBuilder sql = new StringBuilder("select * from usuarios where ");

        for (Filtro f : filtros) {
            sql.append(f.toString());
            sql.append("and ");
        }
        sql.delete(sql.length() - 4, sql.length() - 1);
        try {
            ps = Conexion.getConnection().prepareStatement(sql.toString());
            for (Filtro f : filtros) {
                if (f.getCriterio() == Filtro.Criterio.LIKE) {
                    ps.setObject(i, "%" + f.getValue() + "%");
                } else {
                    ps.setObject(i, f.getValue());
                }
                i++;
            }
            resultSetToList(ps, usuarios);
            return usuarios;

        } catch (SQLException ex) {
            Logger.getLogger(DAOUsuario.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } finally {
            if (ps != null) {
                ps.close();
            }

        }

    }
}
