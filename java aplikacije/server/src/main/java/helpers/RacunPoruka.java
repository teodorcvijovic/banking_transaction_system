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
@XmlRootElement(name="racun")
public class RacunPoruka implements Serializable {
    
    private Integer idRac;
   
    private Integer idMes;

    private Character status;
   
    private Integer stanje;
    
    private Integer dozvMinus;
    
    private Date datum;
    
    private Integer brTransakcija;
    
    private Integer idKom;
    
    ////////

    public Integer getIdRac() {
        return idRac;
    }

    public void setIdRac(Integer idRac) {
        this.idRac = idRac;
    }

    @XmlElement(name="mesto")
    public Integer getIdMes() {
        return idMes;
    }

    public void setIdMes(Integer idMes) {
        this.idMes = idMes;
    }

    @XmlElement(name="status")
    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    @XmlElement(name="stanje")
    public Integer getStanje() {
        return stanje;
    }

    public void setStanje(Integer stanje) {
        this.stanje = stanje;
    }

    @XmlElement(name="dozvMinus")
    public Integer getDozvMinus() {
        return dozvMinus;
    }

    public void setDozvMinus(Integer dozvMinus) {
        this.dozvMinus = dozvMinus;
    }

    @XmlElement(name="datum")
    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    @XmlElement(name="brTransakcija")
    public Integer getBrTransakcija() {
        return brTransakcija;
    }

    public void setBrTransakcija(Integer brTransakcija) {
        this.brTransakcija = brTransakcija;
    }

    @XmlElement(name="komitent")
    public Integer getIdKom() {
        return idKom;
    }

    public void setIdKom(Integer idKom) {
        this.idKom = idKom;
    }
    
    
}
