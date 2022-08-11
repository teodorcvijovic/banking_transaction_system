/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package podsistem2;

import entiteti.Komitent;
import entiteti.Racun;
import entiteti.Transakcija;
import entiteti.TransakcijaPK;
import helpers.RacunPoruka;
import helpers.TransakcijaPoruka;
import java.sql.Timestamp;
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
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 *
 * @author Teodor
 */
public class Podsistem2 {

    /**
     * @param args the command line arguments
     */
    
    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("Podsistem2PU");
    static EntityManager em = emf.createEntityManager();
    
    @Resource(lookup = "connFactory")
    static ConnectionFactory connFactory;
    
    @Resource(lookup="serverTopic")
    static Topic topic;
    
    @Resource(lookup="molimteradi")
    static Queue serverQueue;
    
    @Resource(lookup="p1p2queue")
    static Queue p1p2queue; // consumer
    JMSConsumer p1consumer;
    
    @Resource(lookup="p2p1queue")
    static Queue p2p1queue; // producer
    JMSProducer p1producer;
    
    @Resource(lookup="p3p2queue")
    static Queue p3p2queue; // consumer
    JMSConsumer p3consumer;
    
    @Resource(lookup="p2p3queue")
    static Queue p2p3queue; // producer
    JMSProducer p3producer;
    
    JMSContext context;
    JMSConsumer consumer;
    JMSProducer producer;
    
    ///////////////////
    
    private TextMessage otvoriRacun(int idKom, int idMes, int dozvMinus){
        TextMessage msg = null;
        try {
            Racun racun = new Racun();
            racun.setBrTransakcija(0);
            racun.setStanje(0);
            racun.setStatus('A');
            racun.setDozvMinus(dozvMinus);
            racun.setIdMes(idMes);
            
            Timestamp datum = new Timestamp(System.currentTimeMillis());
            racun.setDatum(datum);
            
            List<Komitent> komitenti = em.createNamedQuery("Komitent.findByIdKom", Komitent.class).setParameter("idKom", idKom).getResultList();
            Komitent k = (komitenti.isEmpty()? null : komitenti.get(0));
            
            String txt = "Racun je uspesno otvoren!";
            int status = 0;
            if (k == null){
                txt = "Zadati komitent ne postoji!";
                status = -1;
            }
            else {
     
                    racun.setIdKom(k);
                    
                    em.getTransaction().begin();
                    em.persist(racun);
                    em.getTransaction().commit();
              
             
                    if (em.getTransaction().isActive())
                       em.getTransaction().rollback();
            }
            msg = context.createTextMessage(txt);
            msg.setIntProperty("status", status);
        } catch (JMSException ex) {
            Logger.getLogger(Podsistem2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msg;
    }
    
    private TextMessage zatvoriRacun(int idRac){
        TextMessage msg = null;
        try {  
            List<Racun> r = em.createNamedQuery("Racun.findByIdRac", Racun.class).setParameter("idRac", idRac).getResultList();
            Racun racun = (r.isEmpty()? null : r.get(0));
      
            
            String txt = "Racun je uspesno zatvoren!";
            int status = 0;
            if (racun == null){
                txt = "Zadati racun ne postoji!";
                status = -1;
            }
            else {
                try
                {
                    em.getTransaction().begin();
                    racun.setStatus('Z');
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
                
     
            }
            msg = context.createTextMessage(txt);
            msg.setIntProperty("status", status);
        } catch (JMSException ex) {
            Logger.getLogger(Podsistem2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msg;
    }
    
    private ObjectMessage dohvRacuneKomitenta(int idKom){
        ArrayList<RacunPoruka> racuni = new ArrayList<>();
        
        List<Komitent> k = em.createNamedQuery("Komitent.findByIdKom", Komitent.class).setParameter("idKom", idKom).getResultList();
        if (!k.isEmpty()){
            Komitent komitent = k.get(0);
            List<Racun> r = em.createQuery("SELECT r FROM Racun r WHERE r.idKom = :idKom", Racun.class).setParameter("idKom", komitent).getResultList();
            for (Racun rac : r) {
                RacunPoruka racunPoruka = new RacunPoruka();
                racunPoruka.setIdRac(rac.getIdRac());
                racunPoruka.setIdKom(rac.getIdKom().getIdKom());
                racunPoruka.setBrTransakcija(rac.getBrTransakcija());
                racunPoruka.setDatum(rac.getDatum());
                racunPoruka.setDozvMinus(rac.getDozvMinus());
                racunPoruka.setIdMes(rac.getIdMes());
                racunPoruka.setStanje(rac.getStanje());
                racunPoruka.setStatus(rac.getStatus());
                
                racuni.add(racunPoruka);
            }
               
        }
   
        return context.createObjectMessage(racuni);
    }
    
    /*
    private ObjectMessage dohvRacuneKomitenta(int idKom){
        ArrayList<Racun> racuni = new ArrayList<>();
        
        List<Komitent> k = em.createNamedQuery("Komitent.findByIdKom", Komitent.class).setParameter("idKom", idKom).getResultList();
        if (!k.isEmpty()){
            Komitent komitent = k.get(0);
            List<Racun> r = em.createQuery("SELECT r FROM Racun r WHERE r.idKom = :idKom", Racun.class).setParameter("idKom", komitent).getResultList();
            for (Racun rac : r) {
                racuni.add(rac);
            }
               
        }
   
        return context.createObjectMessage(racuni);
    }
    */
    
    private TextMessage transferNovca(int idRacSa, int idRacNa, int iznos, String svrha){
        TextMessage msg = null;
        try {  
            List<Racun> r = em.createNamedQuery("Racun.findByIdRac", Racun.class).setParameter("idRac", idRacSa).getResultList();
            Racun racunSa = (r.isEmpty()? null : r.get(0));
            r = em.createNamedQuery("Racun.findByIdRac", Racun.class).setParameter("idRac", idRacNa).getResultList();
            Racun racunNa = (r.isEmpty()? null : r.get(0));
      
            
            String txt = "Transfer je uspesno izvrsen!";
            int status = 0;
            if (racunSa == null || racunNa == null){
                txt = "Greska: Racun ne postoji!";
                status = -1;
            }
            else if (racunSa.getStatus()=='Z' || racunNa.getStatus()=='Z'){
                txt = "Greska: Racun je zatvoren!";
                status = -1;
            }
            else if (racunSa.getStatus()!='A'){
                txt = "Greska: Racun nije aktivan!";
                status = -1;
            }
            else {
                em.getTransaction().begin();
                
                // isplata sa idRacSa
                racunSa.setStanje(racunSa.getStanje()-iznos);
                
                // uplata na idRacNa
                racunNa.setStanje(racunNa.getStanje()+iznos);
                
                // uvecavamo broj transakcija
                racunSa.setBrTransakcija(racunSa.getBrTransakcija()+1);
                racunNa.setBrTransakcija(racunNa.getBrTransakcija()+1);
                
                // proveravamo da li ce transakcija promeniti status racuna
                if (racunNa.getStanje() > -racunNa.getDozvMinus()) racunNa.setStatus('A');
                if (racunSa.getStanje() < -racunSa.getDozvMinus()) racunSa.setStatus('B');
                
                // kreiranje transakcija
                Transakcija t1 = new Transakcija(), t2 = new Transakcija();
                t1.setVrsta('T');
                t2.setVrsta('T');
                t1.setIznos(-iznos);
                t2.setIznos(iznos);
                t1.setSvrha(svrha);
                t2.setSvrha(svrha);
                Timestamp datum = new Timestamp(System.currentTimeMillis()); 
                t1.setDatum(datum);
                t2.setDatum(datum);
                TransakcijaPK tpk1 = new TransakcijaPK(), tpk2 = new TransakcijaPK();
                tpk1.setIdRac(idRacSa);
                tpk2.setIdRac(idRacNa);
                tpk1.setRb(racunSa.getBrTransakcija());
                tpk2.setRb(racunNa.getBrTransakcija());
                t1.setTransakcijaPK(tpk1);
                t2.setTransakcijaPK(tpk2);
                
                em.persist(t1);
                em.persist(t2);
                
                em.getTransaction().commit();
              
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
                
            }
            msg = context.createTextMessage(txt);
            msg.setIntProperty("status", status);
        } catch (JMSException ex) {
            Logger.getLogger(Podsistem2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msg;
    }
    
    private TextMessage uplataIsplata(int idRac, int iznos, int idFil, String svrha){
        TextMessage msg = null;
        try {  
            List<Racun> r = em.createNamedQuery("Racun.findByIdRac", Racun.class).setParameter("idRac", idRac).getResultList();
            Racun racun = (r.isEmpty()? null : r.get(0));
          
            String txt = "Transfer je uspesno izvrsen!";
            int status = 0;
            if (racun == null){
                txt = "Greska: Racun ne postoji!";
                status = -1;
            }
            else if (racun.getStatus()=='B' && iznos<0){
                txt = "Greska: Nije moguce izvrsiti isplatu sa blokiranog racuna!";
                status = -1;
            }
            else if (racun.getStatus()=='Z'){
                txt = "Greska: Racun je zatvoren!";
                status = -1;
            }
            else {
                em.getTransaction().begin();
                
                racun.setStanje(racun.getStanje()+iznos);
                
                // uvecavamo broj transakcija
                racun.setBrTransakcija(racun.getBrTransakcija()+1);
                
                // proveravamo da li ce transakcija promeniti status racuna
                if (racun.getStanje() > -racun.getDozvMinus()) racun.setStatus('A');
                else if (racun.getStanje() < -racun.getDozvMinus()) racun.setStatus('B');
                
                // kreiranje transakcija
                Transakcija t = new Transakcija();
                Character vrsta = (iznos<0 ? 'I' : 'U');
                t.setVrsta(vrsta);
                t.setIznos((iznos<0 ? -iznos : iznos));
                t.setSvrha(svrha);
                t.setIdFil(idFil);
                Timestamp datum = new Timestamp(System.currentTimeMillis()); 
                t.setDatum(datum);
                TransakcijaPK tpk = new TransakcijaPK();
                tpk.setIdRac(idRac);
                tpk.setRb(racun.getBrTransakcija());
                t.setTransakcijaPK(tpk);

                em.persist(t);
                
                em.getTransaction().commit();
              
                if (em.getTransaction().isActive())
                    em.getTransaction().rollback();
                
            }
            msg = context.createTextMessage(txt);
            msg.setIntProperty("status", status);
        } catch (JMSException ex) {
            Logger.getLogger(Podsistem2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msg;
    }
    
    private ObjectMessage dohvTransakcijeRacuna(int idRac){
        ArrayList<TransakcijaPoruka> transakcije = new ArrayList<>();
        
        List<Racun> r = em.createNamedQuery("Racun.findByIdRac", Racun.class).setParameter("idRac", idRac).getResultList();
        if (!r.isEmpty()){
            Racun racun = r.get(0);
            List<Transakcija> t = em.createNamedQuery("Transakcija.findByIdRac", Transakcija.class).setParameter("idRac", idRac).getResultList();
            for (Transakcija tra : t) {
                TransakcijaPoruka tp = new TransakcijaPoruka();
                tp.setIdRac(tra.getTransakcijaPK().getIdRac());
                tp.setRb(tra.getTransakcijaPK().getRb());
                tp.setIznos(tra.getIznos());
                tp.setVrsta(tra.getVrsta());
                tp.setSvrha(tra.getSvrha());
                tp.setDatum(tra.getDatum());
                tp.setIdFil(tra.getIdFil());
                
                transakcije.add(tp);
            }
               
        }
   
        return context.createObjectMessage(transakcije);
    }
    
    ///////////////////
    
    private ObjectMessage dohvRacune(){
            ArrayList<RacunPoruka> racuni = new ArrayList<>();
        
            List<Racun> r = em.createNamedQuery("Racun.findAll", Racun.class).getResultList();
            for (Racun rac : r) {
                RacunPoruka racunPoruka = new RacunPoruka();
                racunPoruka.setIdRac(rac.getIdRac());
                racunPoruka.setIdKom(rac.getIdKom().getIdKom());
                racunPoruka.setBrTransakcija(rac.getBrTransakcija());
                racunPoruka.setDatum(rac.getDatum());
                racunPoruka.setDozvMinus(rac.getDozvMinus());
                racunPoruka.setIdMes(rac.getIdMes());
                racunPoruka.setStanje(rac.getStanje());
                racunPoruka.setStatus(rac.getStatus());
                
                racuni.add(racunPoruka);
            }

            return context.createObjectMessage(racuni);
    }
    
    private ObjectMessage dohvTransakcije(){
            ArrayList<TransakcijaPoruka> transakcije = new ArrayList<>();
        
            List<Transakcija> t = em.createNamedQuery("Transakcija.findAll", Transakcija.class).getResultList();
            for (Transakcija tra : t) {
                TransakcijaPoruka tp = new TransakcijaPoruka();
                tp.setIdRac(tra.getTransakcijaPK().getIdRac());
                tp.setRb(tra.getTransakcijaPK().getRb());
                tp.setIznos(tra.getIznos());
                tp.setVrsta(tra.getVrsta());
                tp.setSvrha(tra.getSvrha());
                tp.setDatum(tra.getDatum());
                tp.setIdFil(tra.getIdFil());
                
                transakcije.add(tp);
            }
   
            return context.createObjectMessage(transakcije);
    }
    
    private void dodajKomitenta(Message msg){
        try {
            int idkom = msg.getIntProperty("IdKom");
            String naziv = msg.getStringProperty("Naziv");
            String adresa = msg.getStringProperty("Adresa");
            int idmes = msg.getIntProperty("IdMes");
            
            Komitent komitent = new Komitent();
            komitent.setIdKom(idkom);
            komitent.setNaziv(naziv);
            komitent.setAdresa(adresa);
            komitent.setIdMes(idmes);
            
            em.getTransaction().begin();
            em.persist(komitent);
            em.getTransaction().commit();
              
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();

        } catch (JMSException ex) {
            Logger.getLogger(Podsistem2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void promenaSedistaKomitenta(Message msg){
        try {
            int idkom = msg.getIntProperty("IdKom");
            int idmes = msg.getIntProperty("IdMes");
            
            List<Komitent> k = em.createNamedQuery("Komitent.findByIdKom", Komitent.class).setParameter("idKom", idkom).getResultList();
            Komitent komitent = (k.isEmpty() ? null : k.get(0));
            if (komitent != null) 
            {
                em.getTransaction().begin();
                komitent.setIdMes(idmes);
                em.getTransaction().commit();
                if (em.getTransaction().isActive())
                        em.getTransaction().rollback();
            }  
        } catch (JMSException ex) {
            Logger.getLogger(Podsistem2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void p1listener(Message msg){
        try {
            System.out.println("Podsistem2: p1listener");
            switch(msg.getIntProperty("zahtev"))
            {
                case 3: 
                    dodajKomitenta(msg);
                    break;
                case 4:
                    promenaSedistaKomitenta(msg);
                    break;
            }
        } catch (JMSException ex) {
            Logger.getLogger(Podsistem2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void p3listener(Message msg){
        Message response = null;
        try {
            switch(msg.getStringProperty("kopija"))
            {
                case "racuni": 
                    response = dohvRacune();
                    break;
                case "transakcije":
                    response = dohvTransakcije();
                    break;
            }
        } catch (JMSException ex) {
            Logger.getLogger(Podsistem2.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (response != null){
            p3producer.send(p2p3queue, response);
        }
    }
    
    ///////////////////
    
    public void run(){
        String msgSelector = "podsistem=2";
        
        context = connFactory.createContext();
        context.setClientID("2");
        consumer = context.createDurableConsumer(topic, "sub1", msgSelector, false);
        producer = context.createProducer();
        
        p1producer = context.createProducer();
        p1consumer = context.createConsumer(p1p2queue);
        p1consumer.setMessageListener((Message msg) -> { p1listener(msg); });
        
        p3producer = context.createProducer();
        p3consumer = context.createConsumer(p3p2queue);
        p3consumer.setMessageListener((Message msg) -> { p3listener(msg); });
        
        int idkom, idmes, dozvminus, idrac, idracSa, idracNa, idfil, iznos;
        String svrha;
        
        while(true){
            try {
                System.out.println("Podsistem2: Cekanje zahteva...");
                TextMessage msg = (TextMessage) consumer.receive();
                int zahtev = msg.getIntProperty("zahtev");
                
                Message response = null;
                switch(zahtev)
                {
                    case 5:
                        idkom = msg.getIntProperty("idKom");
                        idmes = msg.getIntProperty("idMes");
                        dozvminus = msg.getIntProperty("dozvMinus");
                        response = otvoriRacun(idkom, idmes, dozvminus);
                        break;
                    case 7:
                        idracSa = msg.getIntProperty("idRacSa");
                        idracNa = msg.getIntProperty("idRacNa");
                        iznos = msg.getIntProperty("iznos");
                        svrha = msg.getStringProperty("svrha");
                        response = transferNovca(idracSa, idracNa, iznos, svrha);
                        break;
                    case 8: case 9:
                        idrac = msg.getIntProperty("idRac");
                        idfil = msg.getIntProperty("idFil");
                        iznos = msg.getIntProperty("iznos");
                        svrha = msg.getStringProperty("svrha");
                        response = uplataIsplata(idrac, iznos, idfil, svrha);
                        break;
                    case 6:
                        idrac = msg.getIntProperty("idRac");
                        response = zatvoriRacun(idrac);
                        break;
                    case 13:
                        idkom = msg.getIntProperty("idKom");
                        response = dohvRacuneKomitenta(idkom);
                        break;
                    case 14:
                        idrac = msg.getIntProperty("idRac");
                        response = dohvTransakcijeRacuna(idrac);
                        break;
                    
                }
                
                producer.send(serverQueue, response);
            } catch (JMSException ex) {
                Logger.getLogger(Podsistem2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void main(String[] args) {
        Podsistem2 p2 = new Podsistem2();
        p2.run();
    }
    
}
