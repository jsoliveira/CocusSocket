package br.com.cocus.cocussocket.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author jsoliveira
 */
@Entity
@Table(name = "cad_tipo_porta")
public class TipoPorta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_tipo_porta")
    private Long id;

    @Column(name = "ds_tipo_porta", length = 100)
    private String descricao;

    @Column(name = "in_ativo")
    private char inAtivo;

    @Column(name = "dt_transacao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataTransacao;

    @OneToMany(mappedBy = "tipoPorta", fetch = FetchType.LAZY)
    private List<PortaModelo> portaModeloList;

    public TipoPorta() {
    }

    public TipoPorta(Long id, String descricao, char inAtivo, Date dataTransacao) {
        this.id = id;
        this.descricao = descricao;
        this.inAtivo = inAtivo;
        this.dataTransacao = dataTransacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public char getInAtivo() {
        return inAtivo;
    }

    public void setInAtivo(char inAtivo) {
        this.inAtivo = inAtivo;
    }

    public Date getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(Date dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TipoPorta other = (TipoPorta) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @XmlTransient
    public List<PortaModelo> getPortaModeloList() {
        return portaModeloList;
    }

    public void setPortaModeloList(List<PortaModelo> portaModeloList) {
        this.portaModeloList = portaModeloList;
    }

    @Override
    public String toString() {
        return "TipoPorta{" + "id=" + id + ", descricao=" + descricao + ", dataTransacao=" + dataTransacao + '}';
    }

}
