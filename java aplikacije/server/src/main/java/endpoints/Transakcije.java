/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoints;

import helpers.RacunPoruka;
import helpers.TransakcijaPoruka;
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
@Path("transakcije")
public class Transakcije {
    
    @Resource(lookup = "connFactory")
    ConnectionFactory connectionFactory;
    
    @Resource(lookup = "serverTopic")
    Topic topic;
    
    @Resource(lookup = "molimteradi")
    Queue queue;
    
    @POST
    @Path("prenos")
    public Response prenos(@FormParam("idRacSa") Integer idRacSa, @FormParam("idRacNa") Integer idRacNa, 
                           @FormParam("iznos") Integer iznos, @FormParam("svrha") String svrha){
        
        try {
            JMSContext context = connectionFactory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queue);
            
            // message
            TextMessage msg = context.createTextMessage("zahtev");
            msg.setIntProperty("zahtev", 7);
            msg.setIntProperty("podsistem", 2);
            
            msg.setIntProperty("idRacSa", idRacSa);
            msg.setIntProperty("idRacNa", idRacNa);
            msg.setIntProperty("iznos", iznos);
            msg.setStringProperty("svrha", svrha);
            
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
            Logger.getLogger(Transakcije.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return Response.status(OK).entity("Uspesan prenos!").build();
    }
    
    @POST
    public Response uplataIsplata(@FormParam("idRac") Integer idRac, @FormParam("iznos") Integer iznos,
                                  @FormParam("idFil") Integer idFil, @FormParam("svrha") String svrha){
        String responseMsg = (iznos<0 ? "Uspesna isplata!" : "Uspesna uplata!");
        int zahtev = (iznos<0 ? 9 : 8);
        
        try {
            JMSContext context = connectionFactory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queue);
            
            // message
            TextMessage msg = context.createTextMessage("zahtev");
            msg.setIntProperty("zahtev", zahtev);
            msg.setIntProperty("podsistem", 2);
            
            msg.setIntProperty("idRac", idRac);
            msg.setIntProperty("iznos", iznos);
            msg.setIntProperty("idFil", idFil);
            msg.setStringProperty("svrha", svrha);
            
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
            Logger.getLogger(Transakcije.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return Response.status(OK).entity(responseMsg).build();
    }
    
    @GET
    @Path("{idRac}")
    public Response dohvTransakcijeRacuna(@PathParam("idRac") Integer idRac){
        ArrayList<TransakcijaPoruka> transakcije = null;
        
        try {
            JMSContext context = connectionFactory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queue);
            
            // message
            TextMessage msg = context.createTextMessage("zahtev");
            msg.setIntProperty("zahtev", 14);
            msg.setIntProperty("podsistem", 2);
            
            msg.setIntProperty("idRac", idRac);
            
            producer.send(topic, msg);
            
            // response
            Message mess = consumer.receive();
            if (!(mess instanceof ObjectMessage)){
                return Response.status(Response.Status.BAD_REQUEST).entity("Greska: Neodgovarajuci tip poruke!").build();
            }
            ObjectMessage objMsg = (ObjectMessage) mess;
            //String classname = objMsg.getObject().getClass().getName();
            transakcije = (ArrayList<TransakcijaPoruka>) objMsg.getObject();
            
        } catch (ClassCastException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Greska: Neodgovarajuci tip objekta!").build();
        } catch (JMSException ex) {
            Logger.getLogger(Komitenti.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return Response.status(OK).entity(new GenericEntity<List<TransakcijaPoruka>>(transakcije){}).build();
    }
    
}
