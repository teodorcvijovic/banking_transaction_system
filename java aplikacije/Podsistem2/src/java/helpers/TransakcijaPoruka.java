/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helpers;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Teodor
 */
@XmlRootElement(name="transakcija")
public class TransakcijaPoruka implements Serializable {
    
    private int idRac;

    private int rb;
    
    private Character vrsta;
   
    private Integer iznos;
   
    private String svrha;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date datum;
  
    private Integer idFil;

    @XmlElement(name="idRac")
    public int getIdRac() {
        return idRac;
    }

    public void setIdRac(int idRac) {
        this.idRac = idRac;
    }

    @XmlElement(name="rb")
    public int getRb() {
        return rb;
    }

    public void setRb(int rb) {
        this.rb = rb;
    }

    @XmlElement(name="vrsta")
    public Character getVrsta() {
        return vrsta;
    }

    public void setVrsta(Character vrsta) {
        this.vrsta = vrsta;
    }

    @XmlElement(name="iznos")
    public Integer getIznos() {
        return iznos;
    }

    public void setIznos(Integer iznos) {
        this.iznos = iznos;
    }

    @XmlElement(name="svrha")
    public String getSvrha() {
        return svrha;
    }

    public void setSvrha(String svrha) {
        this.svrha = svrha;
    }

    @XmlElement(name="datum")
    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    @XmlElement(name="idFil")
    public Integer getIdFil() {
        return idFil;
    }

    public void setIdFil(Integer idFil) {
        this.idFil = idFil;
    }
    
    
}
