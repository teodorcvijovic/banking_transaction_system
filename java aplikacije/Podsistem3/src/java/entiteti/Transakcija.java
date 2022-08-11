/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entiteti;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Teodor
 */
@Entity
@Table(name = "transakcija")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Transakcija.findAll", query = "SELECT t FROM Transakcija t"),
    @NamedQuery(name = "Transakcija.findByIdRac", query = "SELECT t FROM Transakcija t WHERE t.transakcijaPK.idRac = :idRac"),
    @NamedQuery(name = "Transakcija.findByRb", query = "SELECT t FROM Transakcija t WHERE t.transakcijaPK.rb = :rb"),
    @NamedQuery(name = "Transakcija.findByVrsta", query = "SELECT t FROM Transakcija t WHERE t.vrsta = :vrsta"),
    @NamedQuery(name = "Transakcija.findByIznos", query = "SELECT t FROM Transakcija t WHERE t.iznos = :iznos"),
    @NamedQuery(name = "Transakcija.findBySvrha", query = "SELECT t FROM Transakcija t WHERE t.svrha = :svrha"),
    @NamedQuery(name = "Transakcija.findByDatum", query = "SELECT t FROM Transakcija t WHERE t.datum = :datum")})
public class Transakcija implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TransakcijaPK transakcijaPK;
    @Column(name = "Vrsta")
    private Character vrsta;
    @Column(name = "Iznos")
    private Integer iznos;
    @Size(max = 45)
    @Column(name = "Svrha")
    private String svrha;
    @Column(name = "Datum")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datum;
    @JoinColumn(name = "IdFil", referencedColumnName = "IdFil")
    @ManyToOne
    private Filijala idFil;
    @JoinColumn(name = "IdRac", referencedColumnName = "IdRac", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Racun racun;

    public Transakcija() {
    }

    public Transakcija(TransakcijaPK transakcijaPK) {
        this.transakcijaPK = transakcijaPK;
    }

    public Transakcija(int idRac, int rb) {
        this.transakcijaPK = new TransakcijaPK(idRac, rb);
    }

    public TransakcijaPK getTransakcijaPK() {
        return transakcijaPK;
    }

    public void setTransakcijaPK(TransakcijaPK transakcijaPK) {
        this.transakcijaPK = transakcijaPK;
    }

    public Character getVrsta() {
        return vrsta;
    }

    public void setVrsta(Character vrsta) {
        this.vrsta = vrsta;
    }

    public Integer getIznos() {
        return iznos;
    }

    public void setIznos(Integer iznos) {
        this.iznos = iznos;
    }

    public String getSvrha() {
        return svrha;
    }

    public void setSvrha(String svrha) {
        this.svrha = svrha;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public Filijala getIdFil() {
        return idFil;
    }

    public void setIdFil(Filijala idFil) {
        this.idFil = idFil;
    }

    public Racun getRacun() {
        return racun;
    }

    public void setRacun(Racun racun) {
        this.racun = racun;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (transakcijaPK != null ? transakcijaPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Transakcija)) {
            return false;
        }
        Transakcija other = (Transakcija) object;
        if ((this.transakcijaPK == null && other.transakcijaPK != null) || (this.transakcijaPK != null && !this.transakcijaPK.equals(other.transakcijaPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entiteti.Transakcija[ transakcijaPK=" + transakcijaPK + " ]";
    }
    
}
