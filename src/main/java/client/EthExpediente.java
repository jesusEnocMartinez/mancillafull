/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import config.ConfigAccess;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import pojo.Documento;
import pojo.ErrorClass;
import pojo.Expediente;

/**
 *
 * @author aldair
 */
public class EthExpediente {

    private static EthExpediente ethExp = null;
    private final HttpAuthenticationFeature feature;
    private final javax.ws.rs.client.Client client;
    private final String basepath;
    private final String password = ConfigAccess.getRecurso().getValue("eth.password");
    private final String user = ConfigAccess.getRecurso().getValue("eth.user");
    private static final String PATH = "expedientes";

    public EthExpediente() throws IOException {
        this.basepath = ConfigAccess.getRecurso().getValue("eth.basepath");
        feature = HttpAuthenticationFeature.basic(user, password);
        client = ClientBuilder.newClient();
        client.register(feature);
    }

    public static EthExpediente getEthExpediente() throws IOException {
        if (ethExp == null) {
            ethExp = new EthExpediente();
        }
        return ethExp;
    }

    public void enableExpediente(String direccionBlockchain) throws ErrorClass {
        WebTarget target = client.target(basepath).path(PATH).path(direccionBlockchain).path("enable");
        Response r = target.request().put(Entity.entity("{}", MediaType.APPLICATION_JSON));
        String res = r.readEntity(String.class);
        try {
            validator(r, res);
        } catch (JSONException ex) {
            Logger.getLogger(EthUsuario.class.getName()).log(Level.SEVERE, null, ex);
            ErrorClass error = new ErrorClass();
            error.setMessage("No se ha podido deshabilitar usuario");
            error.setCode("404");
        }
    }

    public void disableExpediente(String direccionBlockchain) throws ErrorClass {
        JSONObject json = new JSONObject();
        WebTarget target = client.target(basepath).path(PATH).path(direccionBlockchain).path("disable");
        try {
            json.put("key", "value");
        } catch (JSONException ex) {
            Logger.getLogger(EthDocumento.class.getName()).log(Level.SEVERE, null, ex);
        }
        Response r = target.request().put(Entity.entity(json.toString(), MediaType.APPLICATION_JSON));
        String res = r.readEntity(String.class);
        try {
            validator(r, res);
        } catch (JSONException ex) {
            ErrorClass err = new ErrorClass();
            err.setCode("404");
            err.setMessage("No se ha podido deshabilitar usuario");
            Logger.getLogger(EthUsuario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Expediente getExp(String direccionExp) throws JSONException, ErrorClass, ParseException {
        WebTarget target = client.target(basepath).path(PATH).path(direccionExp);
        Response response = target.request().get();
        String body = response.readEntity(String.class);
        validator(response, body);
        return objectify(body);
    }

    public String postExp(Expediente ex) throws JSONException, ErrorClass {
        WebTarget target = client.target(basepath).path(PATH);
        Response response = target.request().post(Entity.entity(jsontify(ex), MediaType.APPLICATION_JSON));
        String body = response.readEntity(String.class);
        validator(response, body);
        JSONObject json = new JSONObject(body);
        return json.getString("direccion");
    }

    public void addDoc(String direccionExp, String direccionDoc) throws JSONException, ErrorClass {
        WebTarget target = client.target(basepath).path(PATH)
                .path(direccionExp)
                .path(direccionDoc);
        Response response = target.request().post(Entity.entity(null, MediaType.APPLICATION_JSON));
        String body = response.readEntity(String.class);
        validator(response, body);
    }

    private String jsontify(Expediente e) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", e.getIdExpediente());
        json.put("titulo", e.getTitulo());
        json.put("fechaCreacion", Long.toString(e.getFechaCreacion().toInstant().toEpochMilli()));
        json.put("tema", e.getTemas());
        json.put("activo", "1");
        json.put("direccionUsuario", e.getIdUsuario());
        json.put("direccionAutor", e.getIdAutor());
        return json.toString();
    }

    private Expediente objectify(String body) throws JSONException, ErrorClass, ParseException {
        JSONObject json = new JSONObject(body);
        Expediente e = new Expediente();
        e.setIdExpediente(json.getString("id"));
        e.setTitulo(json.getString("titulo"));
        e.setTemas(json.getString("tema"));
        EthUsuario et = EthUsuario.getEthUsuario();
        e.setEmisor(et.getUsuario(json.getString("direccionAutor")));
        e.setVisor(et.getUsuario(json.getString("direccionUsuario")));
        //new SimpleDateFormat("dd/MM/yyyy").parse(json2.getString("fechaCreacion"))
        e.setFechaCreacion(new SimpleDateFormat("dd/MM/yyyy").parse(json.getString("fechaCreacion")));
        JSONArray jsona = json.getJSONArray("documentos");
        ArrayList<Documento> documentos = new ArrayList<>();
        EthDocumento ethdoc = EthDocumento.getEthDocumento();
        for (int i = 0; i < jsona.length(); i++) {
            Documento doc = ethdoc.get(jsona.get(i).toString());
            documentos.add(doc);
        }
        e.setDocumentos(documentos);
        return e;
    }
    private final ErrorClass erroreth = new ErrorClass();

    private void validator(Response response, String responsestr) throws JSONException, ErrorClass {

        JSONObject jsonObj = null;
        switch (response.getStatus()) {
            case 200:
                if (responsestr.trim().isEmpty()) {
                    return;
                }
                jsonObj = new JSONObject(responsestr);
                if (jsonObj.has("errors")) {
                    JSONArray jsonarray = jsonObj.getJSONArray("errors");
                    if (jsonarray.length() > 0) {
                        JSONObject json = jsonarray.getJSONObject(0);
                        erroreth.setCode(json.getString("code"));
                        erroreth.setMessage(json.getString("description"));
                        throw erroreth;
                    }
                }

                break;

            case 400:
                jsonObj = new JSONObject(responsestr);
                erroreth.setCode(jsonObj.getString("code"));
                erroreth.setMessage(jsonObj.getString("description"));
                throw erroreth;
            case 401:
                erroreth.setCode("unknown");
                erroreth.setMessage("Requiere autenticación");
                throw erroreth;
            case 404:
                erroreth.setCode("404");
                erroreth.setMessage("Expediente no encontrado");
                throw erroreth;
            case 405:
                erroreth.setCode("405");
                erroreth.setMessage("Bad Request");
                throw erroreth;
            default:
                erroreth.setCode("unknown");
                erroreth.setMessage("Error no esperado");
                throw erroreth;
        }
    }
}
