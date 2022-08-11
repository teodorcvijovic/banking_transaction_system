/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import entiteti.Filijala;
import entiteti.Komitent;
import entiteti.Mesto;
import java.io.Serializable;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Teodor
 */
@XmlRootElement(name="baza")
public class Baza implements Serializable {
    
    private ArrayList<Mesto> mesta;
    
    private ArrayList<Filijala> filijale;
    
    private ArrayList<Komitent> komitenti;
    
    private ArrayList<RacunPoruka> racuni;
    
    private ArrayList<TransakcijaPoruka> transakcije;

    @XmlElement(name="mesto")
    public ArrayList<Mesto> getMesta() {
        return mesta;
    }

    public void setMesta(ArrayList<Mesto> mesta) {
        this.mesta = mesta;
    }

    @XmlElement(name="filijala")
    public ArrayList<Filijala> getFilijale() {
        return filijale;
    }

    public void setFilijale(ArrayList<Filijala> filijale) {
        this.filijale = filijale;
    }

    @XmlElement(name="komitent")
    public ArrayList<Komitent> getKomitenti() {
        return komitenti;
    }

    public void setKomitenti(ArrayList<Komitent> komitenti) {
        this.komitenti = komitenti;
    }

    @XmlElement(name="racun")
    public ArrayList<RacunPoruka> getRacuni() {
        return racuni;
    }

    public void setRacuni(ArrayList<RacunPoruka> racuni) {
        this.racuni = racuni;
    }

    @XmlElement(name="transakcija")
    public ArrayList<TransakcijaPoruka> getTransakcije() {
        return transakcije;
    }

    public void setTransakcije(ArrayList<TransakcijaPoruka> transakcije) {
        this.transakcije = transakcije;
    }
}