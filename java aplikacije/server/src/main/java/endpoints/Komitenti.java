/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package endpoints;

import entiteti.Komitent;
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
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.OK;

/**
 *
 * @author Teodor
 */

@Path("komitenti")
public class Komitenti {
    
    @Resource(lookup = "connFactory")
    ConnectionFactory connectionFactory;
    
    @Resource(lookup = "serverTopic")
    Topic topic;
    
    @Resource(lookup = "molimteradi")
    Queue queue;
    
    @POST
    public Response kreirajKomitenta(@FormParam("naziv") String naziv, @FormParam("adresa") String adresa, @FormParam("sediste") String sediste){
                
        try {
            JMSContext context = connectionFactory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queue);
            
            // message
            TextMessage msg = context.createTextMessage("zahtev");
            msg.setIntProperty("zahtev", 3);
            msg.setIntProperty("podsistem", 1);
            
            msg.setStringProperty("naziv", naziv);
            msg.setStringProperty("adresa", adresa);
            msg.setStringProperty("mesto", sediste);
            
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
            Logger.getLogger(Komitenti.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.status(OK).entity("Komitent je uspesno kreiran!").build();
    }
    
    @GET
    public Response dohvKomitente(){
        ArrayList<Komitent> komitenti = null;
        
        try {
            JMSContext context = connectionFactory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queue);
            
            // message
            TextMessage msg = context.createTextMessage("zahtev");
            msg.setIntProperty("zahtev", 12);
            msg.setIntProperty("podsistem", 1);
            
            producer.send(topic, msg);
            
            // response
            Message mess = consumer.receive();
            if (!(mess instanceof ObjectMessage)){
                return Response.status(Response.Status.BAD_REQUEST).entity("Greska: Neodgovarajuci tip poruke!").build();
            }
            ObjectMessage objMsg = (ObjectMessage) mess;
            komitenti = (ArrayList<Komitent>) objMsg.getObject();
            
        } catch (JMSException ex) {
            Logger.getLogger(Komitenti.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassCastException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Greska: Neodgovarajuci tip objekta!").build();
        }
        
        return Response.status(OK).entity(new GenericEntity<List<Komitent>>(komitenti){}).build();
    }
    
    @POST
    @Path("sediste")
    public Response promeniSediste(@FormParam("IdKom") int idKom, @FormParam("mesto") String mesto){
        try {
            JMSContext context = connectionFactory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(queue);
            
            // message
            TextMessage msg = context.createTextMessage("zahtev");
            msg.setIntProperty("zahtev", 4);
            msg.setIntProperty("podsistem", 1);
           
            msg.setStringProperty("mesto", mesto);
            msg.setIntProperty("idkom", idKom);
            
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
            Logger.getLogger(Komitenti.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(OK).entity("Sediste je uspesno promenjeno!").build();
    }
    
    
}
