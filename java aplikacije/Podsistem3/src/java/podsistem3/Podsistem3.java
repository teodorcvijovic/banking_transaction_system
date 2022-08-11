/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package podsistem3;

import entiteti.Filijala;
import entiteti.Komitent;
import entiteti.Mesto;
import entiteti.Racun;
import entiteti.Transakcija;
import entiteti.TransakcijaPK;
import helpers.Baza;
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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.Response;

/**
 *
 * @author Teodor
 */
public class Podsistem3 {

    /**
     * @param args the command line arguments
     */
    
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("Podsistem3PU");
    static EntityManager em = emf.createEntityManager();
    
    @Resource(lookup = "connFactory")
    static ConnectionFactory connFactory;
    
    @Resource(lookup="serverTopic")
    static Topic topic;
    
    @Resource(lookup="molimteradi")
    static Queue serverQueue;
    
    @Resource(lookup="p1p3queue")
    static Queue p1p3queue; // consumer
    JMSConsumer p1consumer;
    
    @Resource(lookup="p3p1queue")
    static Queue p3p1queue; // producer
    JMSProducer p1producer;
    
    @Resource(lookup="p2p3queue")
    static Queue p2p3queue; // consumer
    JMSConsumer p2consumer;
    
    @Resource(lookup="p3p2queue")
    static Queue p3p2queue; // producer
    JMSProducer p2producer;
    
    JMSContext context;
    JMSConsumer consumer;
    JMSProducer producer;
    
    //////////////////////////////////////
    
    private ObjectMessage dohvPodatke(){
        Baza baza = new Baza();
        
        List<Mesto> mesta = em.createNamedQuery("Mesto.findAll", Mesto.class).getResultList();
        List<Filijala> filijale = em.createNamedQuery("Filijala.findAll", Filijala.class).getResultList();
        List<Komitent> komitenti = em.createNamedQuery("Komitent.findAll", Komitent.class).getResultList();
        List<Racun> racuni = em.createNamedQuery("Racun.findAll", Racun.class).getResultList();
        List<Transakcija> transakcije = em.createNamedQuery("Transakcija.findAll", Transakcija.class).getResultList();
        
        ArrayList<Mesto> m = new ArrayList<Mesto>();
        ArrayList<Filijala> f = new ArrayList<Filijala>();
        ArrayList<Komitent> k = new ArrayList<Komitent>();
        ArrayList<RacunPoruka> r = new ArrayList<RacunPoruka>();
        ArrayList<TransakcijaPoruka> t = new ArrayList<TransakcijaPoruka>();
        
        for (Mesto mes : mesta) {
            m.add(mes);
        }
        
        for (Filijala fil : filijale) {
            f.add(fil);
        }
        
        for (Komitent kom : komitenti) {
            k.add(kom);
        }
        
        for (Racun rac : racuni) {
                RacunPoruka racunPoruka = new RacunPoruka();
                racunPoruka.setIdRac(rac.getIdRac());
                racunPoruka.setIdKom(rac.getIdKom().getIdKom());
                racunPoruka.setBrTransakcija(rac.getBrTransakcija());
                racunPoruka.setDatum(rac.getDatum());
                racunPoruka.setDozvMinus(rac.getDozvMinus());
                racunPoruka.setIdMes(rac.getIdMes().getIdMes());
                racunPoruka.setStanje(rac.getStanje());
                racunPoruka.setStatus(rac.getStatus());
                
                r.add(racunPoruka);
        }
        
        for (Transakcija tra : transakcije) {
                TransakcijaPoruka tp = new TransakcijaPoruka();
                tp.setIdRac(tra.getTransakcijaPK().getIdRac());
                tp.setRb(tra.getTransakcijaPK().getRb());
                tp.setIznos(tra.getIznos());
                tp.setVrsta(tra.getVrsta());
                tp.setSvrha(tra.getSvrha());
                tp.setDatum(tra.getDatum());
                tp.setIdFil(tra.getIdFil().getIdFil());
                
                t.add(tp);
        }
        
        baza.setMesta(m);
        baza.setFilijale(f);
        baza.setKomitenti(k);
        baza.setRacuni(r);
        baza.setTransakcije(t);
        
        return context.createObjectMessage(baza);
    }
    
    private ObjectMessage dohvRazlikuUPodacima(){ // TO DO
        Baza baza = new Baza();
        TextMessage msg = null;
        Message response = null;
        
        //////////////////////// podsistem3 ///////////////////////////////
        
        List<Mesto> mesta3 = em.createNamedQuery("Mesto.findAll", Mesto.class).getResultList();
        List<Filijala> filijale3 = em.createNamedQuery("Filijala.findAll", Filijala.class).getResultList();
        List<Komitent> komitenti3 = em.createNamedQuery("Komitent.findAll", Komitent.class).getResultList();
        List<Racun> racuni3 = em.createNamedQuery("Racun.findAll", Racun.class).getResultList();
        List<Transakcija> transakcije3 = em.createNamedQuery("Transakcija.findAll", Transakcija.class).getResultList();
        
        //////////////////////// podsistem1 ///////////////////////////////
        
        ArrayList<Mesto> mesta1 = null;
        
        try {
            // message
            msg = context.createTextMessage("zahtev");
            msg.setStringProperty("kopija", "mesta");
            
            p1producer.send(p3p1queue, msg);
            
            // response
            response = p1consumer.receive();
            if (!(response instanceof ObjectMessage)) throw new ClassCastException();
            ObjectMessage objMsg = (ObjectMessage) response;
            mesta1 = (ArrayList<Mesto>) objMsg.getObject();
        } catch (JMSException ex) {
            Logger.getLogger(Mesto.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassCastException ex) {
            System.out.println("Greska: Neodgovarajuci tip objekta!");
        }
        
        /////////////////////////////////
        
        ArrayList<Komitent> komitenti1 = null;
        
        try {
            // message
            msg = context.createTextMessage("zahtev");
            msg.setStringProperty("kopija", "komitenti");
            
            p1producer.send(p3p1queue, msg);
            
            // response
            response = p1consumer.receive();
            if (!(response instanceof ObjectMessage)) throw new ClassCastException();
            ObjectMessage objMsg = (ObjectMessage) response;
            komitenti1 = (ArrayList<Komitent>) objMsg.getObject();
        } catch (JMSException ex) {
            Logger.getLogger(Komitent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassCastException ex) {
            System.out.println("Greska: Neodgovarajuci tip objekta!");
        }
      
        
        ////////////////////////////////
        
        ArrayList<Filijala> filijale1 = null;
        
        try {
            // message
            msg = context.createTextMessage("zahtev");
            msg.setStringProperty("kopija", "filijale");
            
            p1producer.send(p3p1queue, msg);
            
            // response
            response = p1consumer.receive();
            if (!(response instanceof ObjectMessage)) throw new ClassCastException();
            ObjectMessage objMsg = (ObjectMessage) response;
            filijale1 = (ArrayList<Filijala>) objMsg.getObject();
        } catch (JMSException ex) {
            Logger.getLogger(Filijala.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassCastException ex) {
            System.out.println("Greska: Neodgovarajuci tip objekta!");
        }
        
        //////////////////////// podsistem2 ///////////////////////////////
        
        ArrayList<RacunPoruka> racunP2 = null;
        ArrayList<Racun> racuni2 = new ArrayList<Racun>();
        
        try {
            // message
            msg = context.createTextMessage("zahtev");
            msg.setStringProperty("kopija", "racuni");
            
            p2producer.send(p3p2queue, msg);
            
            // response
            response = p2consumer.receive();
            if (!(response instanceof ObjectMessage)) throw new ClassCastException();
            ObjectMessage objMsg = (ObjectMessage) response;
            racunP2 = (ArrayList<RacunPoruka>) objMsg.getObject();
        } catch (JMSException ex) {
            Logger.getLogger(RacunPoruka.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassCastException ex) {
            System.out.println("Greska: Neodgovarajuci tip objekta!");
        }
        
        for (RacunPoruka rp : racunP2) {
            Racun r = new Racun();
            
            r.setIdRac(rp.getIdRac());
            r.setStanje(rp.getStanje());
            r.setStatus(rp.getStatus());
            r.setBrTransakcija(rp.getBrTransakcija());
            r.setDatum(rp.getDatum());
            r.setDozvMinus(rp.getDozvMinus());
            
            // komitent
            List<Komitent> k = em.createNamedQuery("Komitent.findByIdKom", Komitent.class).setParameter("idKom", rp.getIdKom()).getResultList();
            Komitent komitent = (k.isEmpty()? null : k.get(0));
            
            // mesto
            List<Mesto> m = em.createNamedQuery("Mesto.findByIdMes", Mesto.class).setParameter("idMes", rp.getIdMes()).getResultList();
            Mesto mesto = (m.isEmpty()? null : m.get(0));
            
            if (komitent == null || mesto == null) continue;
            
            r.setIdKom(komitent);
            r.setIdMes(mesto);
            
            racuni2.add(r);
        }
        
        /////////////////////////////////
        
        ArrayList<TransakcijaPoruka> transakcijaP2 = null;
        ArrayList<Transakcija> transakcije2 = new ArrayList<Transakcija>();
        
        
        try {
            // message
            msg = context.createTextMessage("zahtev");
            msg.setStringProperty("kopija", "transakcije");
            
            p2producer.send(p3p2queue, msg);
            
            // response
            response = p2consumer.receive();
            if (!(response instanceof ObjectMessage)) throw new ClassCastException();
            ObjectMessage objMsg = (ObjectMessage) response;
            transakcijaP2 = (ArrayList<TransakcijaPoruka>) objMsg.getObject();
        } catch (JMSException ex) {
            Logger.getLogger(TransakcijaPoruka.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassCastException ex) {
            System.out.println("Greska: Neodgovarajuci tip objekta!");
        }
        
        for (TransakcijaPoruka tp : transakcijaP2) {
            Transakcija t = new Transakcija();
            
            t.setDatum(tp.getDatum());
            t.setIznos(tp.getIznos());
            t.setVrsta(tp.getVrsta());
            t.setSvrha(tp.getSvrha());
            
            // racun
            List<Racun> r = em.createNamedQuery("Racun.findByIdRac", Racun.class).setParameter("idRac", tp.getIdRac()).getResultList();
            Racun racun = (r.isEmpty()? null : r.get(0));
            
            // filijala
            List<Filijala> f = em.createNamedQuery("Filijala.findByIdFil", Filijala.class).setParameter("idFil", tp.getIdFil()).getResultList();
            Filijala filijala = (f.isEmpty()? null : f.get(0));
            
            TransakcijaPK tpk = new TransakcijaPK();
            tpk.setIdRac(tp.getIdRac());
            tpk.setRb(tp.getRb());
            
            t.setTransakcijaPK(tpk);
            t.setRacun(racun);
            t.setIdFil(filijala);
            
            if (racun == null) continue;
            transakcije2.add(t);
        }
        
        ///////////////////////////////////////////////////////////////////
          
        ArrayList<Mesto> m = new ArrayList<Mesto>((mesta3.size()>mesta1.size() ? mesta3 : mesta1));
        ArrayList<Filijala> f = new ArrayList<Filijala>((filijale3.size()>filijale1.size() ? filijale3 : filijale1));
        ArrayList<Komitent> k = new ArrayList<Komitent>((komitenti3.size()>komitenti1.size() ? komitenti3 : komitenti1));
        ArrayList<Racun> racuniRazlika = new ArrayList<Racun>((racuni3.size()>racuni2.size() ? racuni3 : racuni2));
        ArrayList<Transakcija> transakcijeRazlika = new ArrayList<Transakcija>((transakcije3.size()>transakcije2.size() ? transakcije3 : transakcije2));
        
        m.removeAll((mesta3.size()>mesta1.size() ? mesta1 : mesta3));
        f.removeAll((filijale3.size()>filijale1.size() ? filijale1 : filijale3));
        k.removeAll((komitenti3.size()>komitenti1.size() ? komitenti1 : komitenti3));
        racuniRazlika.removeAll((racuni3.size()>racuni2.size() ? racuni2 : racuni3));
        transakcijeRazlika.removeAll((transakcije3.size()>transakcije2.size() ? transakcije2 : transakcije3));
          
        ArrayList<RacunPoruka> r = new ArrayList<RacunPoruka>();
        ArrayList<TransakcijaPoruka> t = new ArrayList<TransakcijaPoruka>();
        
        for (Racun rac : racuniRazlika) {
                RacunPoruka racunPoruka = new RacunPoruka();
                racunPoruka.setIdRac(rac.getIdRac());
                racunPoruka.setIdKom(rac.getIdKom().getIdKom());
                racunPoruka.setBrTransakcija(rac.getBrTransakcija());
                racunPoruka.setDatum(rac.getDatum());
                racunPoruka.setDozvMinus(rac.getDozvMinus());
                racunPoruka.setIdMes(rac.getIdMes().getIdMes());
                racunPoruka.setStanje(rac.getStanje());
                racunPoruka.setStatus(rac.getStatus());
                
                r.add(racunPoruka);
        }
        
        for (Transakcija tra : transakcijeRazlika) {
                TransakcijaPoruka tp = new TransakcijaPoruka();
                tp.setIdRac(tra.getTransakcijaPK().getIdRac());
                tp.setRb(tra.getTransakcijaPK().getRb());
                tp.setIznos(tra.getIznos());
                tp.setVrsta(tra.getVrsta());
                tp.setSvrha(tra.getSvrha());
                tp.setDatum(tra.getDatum());
                tp.setIdFil(tra.getIdFil().getIdFil());
                
                t.add(tp);
        }
        
        baza.setMesta(m);
        baza.setFilijale(f);
        baza.setKomitenti(k);
        baza.setRacuni(r);
        baza.setTransakcije(t);
        
        return context.createObjectMessage(baza);
    }
    
    //////////////////////////////////////
    
    private void serverListener(Message msg){
        Message response = null;
        try {
            switch(msg.getIntProperty("zahtev"))
            {
                case 15: 
                    response = dohvPodatke();
                    break;
                case 16:
                    response = dohvRazlikuUPodacima();
                    break;
            }
        } catch (JMSException ex) {
            Logger.getLogger(Podsistem3.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (response != null) producer.send(serverQueue, response);
    }
    
    //////////////////////////////////////
    
    public void run(){
        String msgSelector = "podsistem=3";
        
        // komunikacija sa serverom
        context = connFactory.createContext();
        context.setClientID("3");
        consumer = context.createDurableConsumer(topic, "sub1", msgSelector, false);
        consumer.setMessageListener((Message msg) -> { serverListener(msg); });
        producer = context.createProducer();
        
        p2producer = context.createProducer();
        p2consumer = context.createConsumer(p2p3queue);
        
        p1producer = context.createProducer();
        p1consumer = context.createConsumer(p1p3queue);
     
        try {
            while(true)
            {
                System.out.println("Podsistem3 zapocinje cuvanje kopije podataka...");
                
                cuvanjeKopije1();
                
                cuvanjeKopije2();
                
                System.out.println("Kopija podataka je uspesno sacuvana!");
                Thread.sleep(120000); // 2 min
            }
        } catch (InterruptedException ex) {
                Logger.getLogger(Podsistem3.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Podsistem3 obustavlja rad...");
    }
    
    private void cuvanjeKopije1() {
       
        TextMessage msg = null;
        Message response = null;
        
        /////////////////////////
        
        ArrayList<Mesto> mesta1 = null;
        
        try {
            // message
            msg = context.createTextMessage("zahtev");
            msg.setStringProperty("kopija", "mesta");
            
            p1producer.send(p3p1queue, msg);
            
            // response
            response = p1consumer.receive();
            if (!(response instanceof ObjectMessage)) throw new ClassCastException();
            ObjectMessage objMsg = (ObjectMessage) response;
            mesta1 = (ArrayList<Mesto>) objMsg.getObject();
        } catch (JMSException ex) {
            Logger.getLogger(Mesto.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassCastException ex) {
            System.out.println("Greska: Neodgovarajuci tip objekta!");
        }
        
        /*
        List<Mesto> mesta3 = em.createNamedQuery("Mesto.findAll", Mesto.class).getResultList();
        
        List<Mesto> mestaRazlika = new ArrayList<Mesto>(mesta1);
        mestaRazlika.removeAll(mesta3);
        
        em.getTransaction().begin();
        for (Mesto mesto : mestaRazlika) {
            em.persist(mesto);
        }
        em.getTransaction().commit();
        */
   
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Mesto").executeUpdate();
        for (Mesto mesto : mesta1) {
            em.persist(mesto);
        }
        em.getTransaction().commit();
        
        /////////////////////////////////
        
        ArrayList<Komitent> komitenti1 = null;
        
        try {
            // message
            msg = context.createTextMessage("zahtev");
            msg.setStringProperty("kopija", "komitenti");
            
            p1producer.send(p3p1queue, msg);
            
            // response
            response = p1consumer.receive();
            if (!(response instanceof ObjectMessage)) throw new ClassCastException();
            ObjectMessage objMsg = (ObjectMessage) response;
            komitenti1 = (ArrayList<Komitent>) objMsg.getObject();
        } catch (JMSException ex) {
            Logger.getLogger(Komitent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassCastException ex) {
            System.out.println("Greska: Neodgovarajuci tip objekta!");
        }
        
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Komitent").executeUpdate();
        for (Komitent k : komitenti1) {
            em.persist(k);
        }
        em.getTransaction().commit();
        
        ////////////////////////////////
        
        ArrayList<Filijala> filijale1 = null;
        
        try {
            // message
            msg = context.createTextMessage("zahtev");
            msg.setStringProperty("kopija", "filijale");
            
            p1producer.send(p3p1queue, msg);
            
            // response
            response = p1consumer.receive();
            if (!(response instanceof ObjectMessage)) throw new ClassCastException();
            ObjectMessage objMsg = (ObjectMessage) response;
            filijale1 = (ArrayList<Filijala>) objMsg.getObject();
        } catch (JMSException ex) {
            Logger.getLogger(Filijala.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassCastException ex) {
            System.out.println("Greska: Neodgovarajuci tip objekta!");
        }
        
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Filijala").executeUpdate();
        for (Filijala f : filijale1) {
            em.persist(f);
        }
        em.getTransaction().commit();
  
    }

    private void cuvanjeKopije2() {
        
        TextMessage msg = null;
        Message response = null;
        
        //////////////////////
        
        ArrayList<RacunPoruka> racunP1 = null;
        
        try {
            // message
            msg = context.createTextMessage("zahtev");
            msg.setStringProperty("kopija", "racuni");
            
            p2producer.send(p3p2queue, msg);
            
            // response
            response = p2consumer.receive();
            if (!(response instanceof ObjectMessage)) throw new ClassCastException();
            ObjectMessage objMsg = (ObjectMessage) response;
            racunP1 = (ArrayList<RacunPoruka>) objMsg.getObject();
        } catch (JMSException ex) {
            Logger.getLogger(RacunPoruka.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassCastException ex) {
            System.out.println("Greska: Neodgovarajuci tip objekta!");
        }
        
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Racun").executeUpdate();
        for (RacunPoruka rp : racunP1) {
            Racun r = new Racun();
            
            r.setIdRac(rp.getIdRac());
            r.setStanje(rp.getStanje());
            r.setStatus(rp.getStatus());
            r.setBrTransakcija(rp.getBrTransakcija());
            r.setDatum(rp.getDatum());
            r.setDozvMinus(rp.getDozvMinus());
            
            // komitent
            List<Komitent> k = em.createNamedQuery("Komitent.findByIdKom", Komitent.class).setParameter("idKom", rp.getIdKom()).getResultList();
            Komitent komitent = (k.isEmpty()? null : k.get(0));
            
            // mesto
            List<Mesto> m = em.createNamedQuery("Mesto.findByIdMes", Mesto.class).setParameter("idMes", rp.getIdMes()).getResultList();
            Mesto mesto = (m.isEmpty()? null : m.get(0));
            
            if (komitent == null || mesto == null) continue;
            
            r.setIdKom(komitent);
            r.setIdMes(mesto);
            
            em.persist(r);
        }
        em.getTransaction().commit();
        
        /////////////////////////////////
        
        ArrayList<TransakcijaPoruka> transakcijaP1 = null;
        
        try {
            // message
            msg = context.createTextMessage("zahtev");
            msg.setStringProperty("kopija", "transakcije");
            
            p2producer.send(p3p2queue, msg);
            
            // response
            response = p2consumer.receive();
            if (!(response instanceof ObjectMessage)) throw new ClassCastException();
            ObjectMessage objMsg = (ObjectMessage) response;
            transakcijaP1 = (ArrayList<TransakcijaPoruka>) objMsg.getObject();
        } catch (JMSException ex) {
            Logger.getLogger(TransakcijaPoruka.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassCastException ex) {
            System.out.println("Greska: Neodgovarajuci tip objekta!");
        }
        
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Transakcija").executeUpdate();
        for (TransakcijaPoruka tp : transakcijaP1) {
            Transakcija t = new Transakcija();
            
            t.setDatum(tp.getDatum());
            t.setIznos(tp.getIznos());
            t.setVrsta(tp.getVrsta());
            t.setSvrha(tp.getSvrha());
            
            // racun
            List<Racun> r = em.createNamedQuery("Racun.findByIdRac", Racun.class).setParameter("idRac", tp.getIdRac()).getResultList();
            Racun racun = (r.isEmpty()? null : r.get(0));
            
            // filijala
            List<Filijala> f = em.createNamedQuery("Filijala.findByIdFil", Filijala.class).setParameter("idFil", tp.getIdFil()).getResultList();
            Filijala filijala = (f.isEmpty()? null : f.get(0));
            
            TransakcijaPK tpk = new TransakcijaPK();
            tpk.setIdRac(tp.getIdRac());
            tpk.setRb(tp.getRb());
            
            t.setTransakcijaPK(tpk);
            t.setRacun(racun);
            t.setIdFil(filijala);
            
            if (racun == null) continue;
            em.persist(t);
        }
        em.getTransaction().commit();
        
    }
    
    public static void main(String[] args) {
        Podsistem3 p3 = new Podsistem3();
        p3.run();
    }
   
}
