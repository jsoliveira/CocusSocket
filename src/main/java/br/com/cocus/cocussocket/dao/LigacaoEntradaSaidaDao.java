package br.com.cocus.cocussocket.dao;

import br.com.cocus.cocussocket.model.LigacaoEntradaSaida;
import br.com.cocus.cocussocket.model.pk.LigacaoEntradaSaidaPK;
import java.io.Serializable;

/**
 *
 * @author jsoliveira
 */
public class LigacaoEntradaSaidaDao extends GenericDao<LigacaoEntradaSaidaPK, LigacaoEntradaSaida> implements Serializable {
    
    public LigacaoEntradaSaidaDao() {
        
        setClazz(LigacaoEntradaSaida.class);
    }
    
}
