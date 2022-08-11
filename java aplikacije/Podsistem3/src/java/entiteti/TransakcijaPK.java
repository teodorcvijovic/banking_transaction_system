/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entiteti;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Teodor
 */
@Embeddable
public class TransakcijaPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "IdRac")
    private int idRac;
    @Basic(optional = false)
    @NotNull
    @Column(name = "RB")
    private int rb;

    public TransakcijaPK() {
    }

    public TransakcijaPK(int idRac, int rb) {
        this.idRac = idRac;
        this.rb = rb;
    }

    public int getIdRac() {
        return idRac;
    }

    public void setIdRac(int idRac) {
        this.idRac = idRac;
    }

    public int getRb() {
        return rb;
    }

    public void setRb(int rb) {
        this.rb = rb;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idRac;
        hash += (int) rb;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransakcijaPK)) {
            return false;
        }
        TransakcijaPK other = (TransakcijaPK) object;
        if (this.idRac != other.idRac) {
            return false;
        }
        if (this.rb != other.rb) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entiteti.TransakcijaPK[ idRac=" + idRac + ", rb=" + rb + " ]";
    }
    
}
