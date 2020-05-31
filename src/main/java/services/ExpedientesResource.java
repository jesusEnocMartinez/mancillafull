package services;

import dao.DAOExpediente;
import pojo.ErrorObjectConv;
import pojo.Expediente;
import pojo.ExpedientesAutor;
import com.google.common.collect.Lists;
import dao.Filtro;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONException;
import pojo.ErrorClass;

/**
 * REST Web Service
 *
 * @author aldair
 *
 */
@Path("expedientes")
public class ExpedientesResource {

    @Context
    private UriInfo context;
    
    private static final String MESSAGE = "Validación";
    /*
    *
     * Creates a new instance of GenericResource
     */
    /**
     * Retrieves representation of an instance of Services.GenericResource
     *
     * @param id_usuario
     * @return
     */
    
    @PUT
    @Path("enable")
    @Produces(MediaType.APPLICATION_JSON)
    public Response upExpediente(@QueryParam("id_expediente") String idExpediente){
        try {           
            DAOExpediente dao = DAOExpediente.getDAOExpediente();
            dao.upExp(idExpediente);
            return Response.ok().build();
        } catch (SQLException ex) {
            Logger.getLogger(UsuariosResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(409).entity(ErrorObjectConv.getErrorObject(ex)).build();
        } catch (ErrorClass ex) {
            Logger.getLogger(ExpedientesResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(409).entity(ex).build();
        } 
    }
    @GET
    @Path("usuario")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExpedientesByUsuario(@QueryParam("id_usuario") String idUsuario) {
        
        DAOExpediente dao = DAOExpediente.getDAOExpediente();
        GenericEntity<List<Expediente>> expedientes;
        try {
            expedientes = new GenericEntity<List<Expediente>>(Lists.newArrayList(dao.getAllByUsuario(idUsuario))) {
            };
            return Response.ok(expedientes).build();
        } catch (SQLException ex) {
            Logger.getLogger(ExpedientesResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(409).entity(ErrorObjectConv.getErrorObject(ex)).build();
        }

    }

    @GET
    @Path("autor_usuario")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllByAuthUser(@QueryParam("id_autor") String idAutor, @QueryParam("id_usuario") String idUsuario) {
        DAOExpediente dao = DAOExpediente.getDAOExpediente();
        try {
            ExpedientesAutor expsAuth = dao.getAllByAutorUsuario(idAutor, idUsuario);
            return Response.ok(expsAuth).build();
        } catch (SQLException ex) {
            Logger.getLogger(ExpedientesResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(409).entity(ErrorObjectConv.getErrorObject(ex)).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(Expediente expediente) {
        if(expediente.getTitulo().isEmpty()){
            ErrorClass ex = new ErrorClass();
            ex.setCode(MESSAGE);
            ex.setMessage("El titulo no puede ir vacío");
            return Response.status(409).entity(ex).build();
        }
        if(expediente.getTitulo().length()>50){
           ErrorClass ex = new ErrorClass();
            ex.setCode(MESSAGE);
            ex.setMessage("El titulo no puede exceder de 50 caracteres");
            return Response.status(409).entity(ex).build(); 
        }
        if(expediente.getTemas().isEmpty()){
            ErrorClass ex = new ErrorClass();
            ex.setCode(MESSAGE);
            ex.setMessage("Agregue al menos un tema");
            return Response.status(409).entity(ex).build();
        }
        
        if(expediente.getTemas().length()>32){
            ErrorClass ex = new ErrorClass();
            ex.setCode(MESSAGE);
            ex.setMessage("Agregue al menos un tema");
            return Response.status(409).entity(ex).build();
        }
        
        DAOExpediente dao = DAOExpediente.getDAOExpediente();
        try {
            dao.post(expediente);
            return Response.status(200).build();
        } catch (IOException | JSONException|SQLException ex) {
            Logger.getLogger(ExpedientesResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(409).entity(ErrorObjectConv.getErrorObject(ex)).build();
        } catch ( ErrorClass ex) {
            Logger.getLogger(ExpedientesResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(409).entity(ex).build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@QueryParam("id_expediente") String idExpediente) {
        try {
            DAOExpediente dao = DAOExpediente.getDAOExpediente();
            dao.delete(idExpediente);
            return Response.status(200).build();
        } catch (SQLException ex) {
            Logger.getLogger(ExpedientesResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(409).entity(ErrorObjectConv.getErrorObject(ex)).build();
        } catch (ErrorClass ex) {
            Logger.getLogger(ExpedientesResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(409).entity(ex).build();
        }
    }

    //Retorna un sólo expediente
    @GET
    @Path("/id")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExpediente(@QueryParam("id_expediente") String idExpediente,
            @QueryParam("id_usuario") String idUsuario){
        try {
            DAOExpediente dao = DAOExpediente.getDAOExpediente();
            return Response.ok(dao.getOne(idExpediente, idUsuario)).build();
        } catch (SQLException ex) {
            Logger.getLogger(ExpedientesResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(409).entity(ErrorObjectConv.getErrorObject(ex)).build();
        } catch (ErrorClass ex) {
            Logger.getLogger(ExpedientesResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(409).entity(ex).build();
        } 
    }
    
    @PUT
    @Path("autor")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    //Expedientes de creador
    public Response getExpedientesByAutor(@QueryParam("id_autor") String idAutor, String filtros) {
        GenericEntity<List<Expediente>> expedientes;
        DAOExpediente dao = DAOExpediente.getDAOExpediente();
        try {
            List<Filtro> filtrosa = Filtro.getFiltrosList(filtros);
            expedientes = new GenericEntity<List<Expediente>>(Lists.newArrayList(dao.getAllByAutor(idAutor, filtrosa))) {
            };
            return Response.ok(expedientes).build();
        } catch (SQLException ex) {
            Logger.getLogger(ExpedientesResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(409).entity(ErrorObjectConv.getErrorObject(ex)).build();
        } catch (ErrorClass ex) {
            Logger.getLogger(ExpedientesResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(409).entity(ex).build();
        }
    }
    
    

    /**
     * PUT method for updating or creating an instance of GenericResource
     *
     * @param id_expediente
     * @param expediente
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(@QueryParam("id_expediente") String idExpediente, Expediente expediente) {
        DAOExpediente dao = DAOExpediente.getDAOExpediente();
        try {
            dao.put(idExpediente, expediente);
            return Response.ok().build();
        } catch (SQLException ex) {
            Logger.getLogger(ExpedientesResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(409).build();
        }
    }

}
