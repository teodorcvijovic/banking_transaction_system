/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoints;

import entiteti.Komitent;
import helpers.RacunPoruka;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.OK;

/**
 *
 * @author Teodor
 */

@Path("racuni")
public class Racuni {
    
    @Resource(lookup = "connFactory")
    ConnectionFactory connectionFactory;
    
    @Resource(lookup = "serverTopic")
    Topic topic;
    
    @Resource(lookup = "molimteradi")
    Queue queue;
    
    @POST
    public Response kreirajRacun(@FormParam("idKom") Integer idKom, @FormParam("idMes") Integer idMes, @FormParam("dozvMinus") Integer dozvMinus){
        try {
            JMSContext context = connectionFactory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queue);
            
            // message
            TextMessage msg = context.createTextMessage("zahtev");
            msg.setIntProperty("zahtev", 5);
            msg.setIntProperty("podsistem", 2);
            
            msg.setIntProperty("idMes", idMes);
            msg.setIntProperty("dozvMinus", dozvMinus);
            msg.setIntProperty("idKom", idKom);
            
            producer.send(topic, msg);
            
            // response
            Message mess = consumer.receive();
            if (!(mess instanceof TextMessage)){
                return Response.status(Response.Status.BAD_REQUEST).entity("Greska: Neodgovarajuci tip poruke!").build();
            }
            TextMessage resMsg = (TextMessage) mess;
            String res = resMsg.getText();
            int ret = resMsg.getIntProperty("status");
            if (ret != 0) 
                return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
            
        } catch (JMSException ex) {
            Logger.getLogger(Racuni.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        
        return Response.status(OK).entity("Racun je uspesno otvoren!").build();
    }
    
    @POST
    @Path("zatvori")
    public Response zatvoriRacun(@FormParam("idRac") Integer idRac){
        try {
            JMSContext context = connectionFactory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queue);
            
            // message
            TextMessage msg = context.createTextMessage("zahtev");
            msg.setIntProperty("zahtev", 6);
            msg.setIntProperty("podsistem", 2);
            
            msg.setIntProperty("idRac", idRac);
            
            producer.send(topic, msg);
            
            // response
            Message mess = consumer.receive();
            if (!(mess instanceof TextMessage)){
                return Response.status(Response.Status.BAD_REQUEST).entity("Greska: Neodgovarajuci tip poruke!").build();
            }
            TextMessage resMsg = (TextMessage) mess;
            String res = resMsg.getText();
            int ret = resMsg.getIntProperty("status");
            if (ret != 0) 
                return Response.status(Response.Status.BAD_REQUEST).entity(res).build();
            
        } catch (JMSException ex) {
            Logger.getLogger(Racuni.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        
        return Response.status(OK).entity("Racun je uspesno zatvoren!").build();
    }
    
    @GET
    @Path("{idKom}")
    public Response dohvRacuneKomitenta(@PathParam("idKom") Integer idKom){
        ArrayList<RacunPoruka> racuni = null;
        
        try {
            JMSContext context = connectionFactory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queue);
            
            // message
            TextMessage msg = context.createTextMessage("zahtev");
            msg.setIntProperty("zahtev", 13);
            msg.setIntProperty("podsistem", 2);
            
            msg.setIntProperty("idKom", idKom);
            
            producer.send(topic, msg);
            
            // response
            Message mess = consumer.receive();
            if (!(mess instanceof ObjectMessage)){
                return Response.status(Response.Status.BAD_REQUEST).entity("Greska: Neodgovarajuci tip poruke!").build();
            }
            ObjectMessage objMsg = (ObjectMessage) mess;
            //String classname = objMsg.getObject().getClass().getName();
            racuni = (ArrayList<RacunPoruka>) objMsg.getObject();
            
        } catch (ClassCastException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Greska: Neodgovarajuci tip objekta!").build();
        } catch (JMSException ex) {
            Logger.getLogger(Komitenti.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return Response.status(OK).entity(new GenericEntity<List<RacunPoruka>>(racuni){}).build();
    }
    
    /*
    @GET
    @Path("{idKom}")
    public Response dohvRacuneKomitenta(@PathParam("idKom") Integer idKom){
        ArrayList<Racun> racuni = null;
        
        try {
            JMSContext context = connectionFactory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queue);
            
            // message
            TextMessage msg = context.createTextMessage("zahtev");
            msg.setIntProperty("zahtev", 13);
            msg.setIntProperty("podsistem", 2);
            
            msg.setIntProperty("idKom", idKom);
            
            producer.send(topic, msg);
            
            // response
            Message mess = consumer.receive();
            if (!(mess instanceof ObjectMessage)){
                return Response.status(Response.Status.BAD_REQUEST).entity("Greska: Neodgovarajuci tip poruke!").build();
            }
            ObjectMessage objMsg = (ObjectMessage) mess;
            //String classname = objMsg.getObject().getClass().getName();
            racuni = (ArrayList<Racun>) objMsg.getObject();
            
        } catch (ClassCastException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Greska: Neodgovarajuci tip objekta!").build();
        } catch (JMSException ex) {
            Logger.getLogger(Komitenti.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return Response.status(OK).entity(new GenericEntity<List<Racun>>(racuni){}).build();
    }
    */
}
