/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package podsistem1;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import entiteti.*;
import java.util.ArrayList;
import java.util.List;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Teodor
 */
public class Podsistem1 {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("Podsistem1PU");
    static EntityManager em = emf.createEntityManager();
    
    @Resource(lookup = "connFactory")
    static ConnectionFactory connFactory;
    
    @Resource(lookup="serverTopic")
    static Topic topic;
    
    @Resource(lookup="molimteradi")
    static Queue serverQueue;
    
    @Resource(lookup="p1p2queue")
    static Queue p1p2queue; // producer
    JMSProducer p2producer;
    
    @Resource(lookup="p2p1queue")
    static Queue p2p1queue; // consumer
    JMSConsumer p2consumer;
    
    @Resource(lookup="p1p3queue")
    static Queue p1p3queue; // producer
    JMSProducer p3producer;
    
    @Resource(lookup="p3p1queue")
    static Queue p3p1queue; // consumer
    JMSConsumer p3consumer;
    
    JMSContext context;
    JMSConsumer consumer;
    JMSProducer producer;
    
    //////////////////////
    
    private TextMessage kreirajKomitenta(String naziv, String adresa, String mesto) {
        TextMessage msg = null;
        try {
            Komitent komitent = new Komitent();
            komitent.setNaziv(naziv);
            komitent.setAdresa(adresa);
            
            List<Mesto> mesta = em.createNamedQuery("Mesto.findByNaziv", Mesto.class).setParameter("naziv", mesto).getResultList();
            Mesto m = (mesta.isEmpty()? null : mesta.get(0));
            
            String txt = "Komitent uspesno kreiran!";
            int status = 0;
            if (m == null){
                txt = "Zadato mesto ne postoji!";
                status = -1;
            }
            else {
                komitent.setIdMes(m);
                
                try
                {
                    em.getTransaction().begin();
                    em.persist(komitent);
                    em.getTransaction().commit();
                }
                catch(EntityExistsException e)
                {
                    txt = "Komitent vec postoji!";
                    status = -1;
                }
                finally
                {
                     if (em.getTransaction().isActive())
                        em.getTransaction().rollback();
                }
                
                // dodajemo komitenta u podsistem2
                TextMessage txtmsg = context.createTextMessage("sinhronizacija");
                txtmsg.setIntProperty("zahtev", 3);
                txtmsg.setIntProperty("IdKom", komitent.getIdKom());
                txtmsg.setStringProperty("Naziv", naziv);
                txtmsg.setStringProperty("Adresa", adresa);
                txtmsg.setIntProperty("IdMes", m.getIdMes());
                
                p2producer.send(p1p2queue, txtmsg);
            }
            msg = context.createTextMessage(txt);
            msg.setIntProperty("status", status);
        } catch (JMSException ex) {
            Logger.getLogger(Podsistem1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msg;
    }
    
    private ObjectMessage dohvKomitente(){
        List<Komitent> komitenti = em.createNamedQuery("Komitent.findAll", Komitent.class).getResultList();
        
        ArrayList<Komitent> k = new ArrayList<>();
        
        for (Komitent komitent : komitenti) 
            k.add(komitent);
        
        return context.createObjectMessage(k);
    }
    
    private TextMessage promeniSedisteKomitentu(int idKom, String novo_sediste){
        TextMessage msg = null;
        try {  
            List<Komitent> kom = em.createNamedQuery("Komitent.findByIdKom", Komitent.class).setParameter("idKom", idKom).getResultList();
            Komitent k = (kom.isEmpty()? null : kom.get(0));
            
            List<Mesto> mesta = em.createNamedQuery("Mesto.findByNaziv", Mesto.class).setParameter("naziv", novo_sediste).getResultList();
            Mesto m = (mesta.isEmpty()? null : mesta.get(0));
            
            String txt = "Sediste je uspesno promenjeno!";
            int status = 0;
            if (m == null){
                txt = "Zadato mesto ne postoji!";
                status = -1;
            }
            else if (k == null){
                txt = "Zadati komitent ne postoji!";
                status = -1;
            }
            else {
                try
                {
                    em.getTransaction().begin();
                    k.setIdMes(m);
                    em.getTransaction().commit();
                }
                catch(Exception e)
                {
                    txt = "Greska pri promeni sedista u bazi!";
                    status = -1;
                }
                finally
                {
                    if (em.getTransaction().isActive())
                        em.getTransaction().rollback();
                }
                
                // dodajemo komitenta u podsistem2
                TextMessage txtmsg = context.createTextMessage("sinhronizacija");
                txtmsg.setIntProperty("zahtev", 4);
                txtmsg.setIntProperty("IdKom", k.getIdKom());
                txtmsg.setIntProperty("IdMes", m.getIdMes());
                
                p2producer.send(p1p2queue, txtmsg);
            }
            
            msg = context.createTextMessage(txt);
            msg.setIntProperty("status", status);
        } catch (JMSException ex) {
            Logger.getLogger(Podsistem1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msg;
    }
    
    private TextMessage kreirajMesto(String naziv, String postbr){
        TextMessage msg = null;
        try {
            Mesto mes = new Mesto();
            mes.setNaziv(naziv);
            mes.setPostanskiBroj(postbr);
            
            List<Mesto> mesta = em.createNamedQuery("Mesto.findByPostanskiBroj", Mesto.class).setParameter("postanskiBroj", postbr).getResultList();
            Mesto m = (mesta.isEmpty()? null : mesta.get(0));
            
            String txt = "Mesto je uspesno kreirano!";
            int status = 0;
            if (m != null){
                txt = "Zadato mesto vec postoji!";
                status = -1;
            }
            else {
                try
                {
                    em.getTransaction().begin();
                    em.persist(mes);
                    em.getTransaction().commit();
                }
                catch(EntityExistsException e)
                {
                    txt = "Mesto vec postoji!";
                    status = -1;
                }
                finally
                {
                     if (em.getTransaction().isActive())
                        em.getTransaction().rollback();
                }
                
            }
            msg = context.createTextMessage(txt);
            msg.setIntProperty("status", status);
        } catch (JMSException ex) {
            Logger.getLogger(Podsistem1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msg;
    }
    
    private ObjectMessage dohvMesta(){
        List<Mesto> mesta = em.createNamedQuery("Mesto.findAll", Mesto.class).getResultList();
        
        ArrayList<Mesto> m = new ArrayList<>();
        
        for (Mesto mesto : mesta) 
            m.add(mesto);
        
        return context.createObjectMessage(m);
    }
    
    private TextMessage kreirajFilijalu(String naziv, String adresa, String mesto) {
        TextMessage msg = null;
        try {
            Filijala filijala = new Filijala();
            filijala.setNaziv(naziv);
            filijala.setAdresa(adresa);
            
            List<Mesto> mesta = em.createNamedQuery("Mesto.findByNaziv", Mesto.class).setParameter("naziv", mesto).getResultList();
            Mesto m = (mesta.isEmpty()? null : mesta.get(0));
            
            String txt = "Filijala uspesno kreirana!";
            int status = 0;
            if (m == null){
                txt = "Zadato mesto ne postoji!";
                status = -1;
            }
            else {
                filijala.setIdMes(m);
                
                try
                {
                    em.getTransaction().begin();
                    em.persist(filijala);
                    em.getTransaction().commit();
                }
                catch(EntityExistsException e)
                {
                    txt = "Filijala vec postoji!";
                    status = -1;
                }
                finally
                {
                     if (em.getTransaction().isActive())
                        em.getTransaction().rollback();
                }
            }
            msg = context.createTextMessage(txt);
            msg.setIntProperty("status", status);
        } catch (JMSException ex) {
            Logger.getLogger(Podsistem1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msg;
    }
    
    private ObjectMessage dohvFilijale(){
        List<Filijala> filijale = em.createNamedQuery("Filijala.findAll", Filijala.class).getResultList();
        
        ArrayList<Filijala> f = new ArrayList<>();
        
        for (Filijala fil : filijale) 
            f.add(fil);
        
        return context.createObjectMessage(f);
    }
    
    /////////////////////
    
    private void p2listener(Message msg){
        
    }
    
     private void p3listener(Message msg){
        Message response = null;
        try {
            switch(msg.getStringProperty("kopija"))
            {
                case "mesta": 
                    response = dohvMesta();
                    break;
                case "komitenti":
                    response = dohvKomitente();
                    break;
                case "filijale":
                    response = dohvFilijale();
                    break;
            }
        } catch (JMSException ex) {
            Logger.getLogger(Podsistem1.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (response != null){
            p3producer.send(p1p3queue, response);
        }
    }
    
    ////////////////////
    
    public void run(){
        String msgSelector = "podsistem=1";
        
        context = connFactory.createContext();
        context.setClientID("1");
        consumer = context.createDurableConsumer(topic, "sub1", msgSelector, false);
        producer = context.createProducer();
        
        p2producer = context.createProducer();
        p2consumer = context.createConsumer(p2p1queue);
        p2consumer.setMessageListener((Message msg) -> { p2listener(msg); });
        
        p3producer = context.createProducer();
        p3consumer = context.createConsumer(p3p1queue);
        p3consumer.setMessageListener((Message msg) -> { p3listener(msg); });
        
        int idkom;
        String naziv, adresa, mesto, postbr;
        
        while(true){
            try {
                System.out.println("Podsistem1: Cekanje zahteva...");
                TextMessage msg = (TextMessage) consumer.receive();
                int zahtev = msg.getIntProperty("zahtev");
                
                Message response = null;
                switch(zahtev){
                    case 1:
                        naziv = msg.getStringProperty("naziv");
                        postbr = msg.getStringProperty("postbr");
                        response = kreirajMesto(naziv, postbr);
                        break;
                    case 2:
                        naziv = msg.getStringProperty("naziv");
                        adresa = msg.getStringProperty("adresa");
                        mesto = msg.getStringProperty("mesto"); 
                        response = kreirajFilijalu(naziv, adresa, mesto);
                        break;
                    case 3:
                        naziv = msg.getStringProperty("naziv");
                        adresa = msg.getStringProperty("adresa");
                        mesto = msg.getStringProperty("mesto"); 
                        response = kreirajKomitenta(naziv, adresa, mesto);
                        break;
                    case 4:
                        idkom = msg.getIntProperty("idkom");
                        mesto = msg.getStringProperty("mesto"); // novo sediste
                        response = promeniSedisteKomitentu(idkom, mesto);
                        break;
                    case 10:
                        response = dohvMesta();
                        break;
                    case 11:
                        response = dohvFilijale();
                        break;
                    case 12:
                        response = dohvKomitente();
                        break;
                }
                
                producer.send(serverQueue, response);
            } catch (JMSException ex) {
                Logger.getLogger(Podsistem1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void main(String[] args) {
       
        Podsistem1 p1 = new Podsistem1();
        p1.run();
    }
    
}
