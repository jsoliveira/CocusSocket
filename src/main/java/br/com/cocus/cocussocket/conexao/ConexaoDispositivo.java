package br.com.cocus.cocussocket.conexao;

import br.com.cocus.cocussocket.socket.TrataCliente;
import java.io.PrintStream;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author jsoliveira
 */
public class ConexaoDispositivo {

    private Integer devId;
    private String modulo;
    private String protocolo;
    private String ip;
    private Date dtHrCon;
    private PrintStream printStream;
    private Thread thread;
    private TrataCliente trataCliente;

    public ConexaoDispositivo() {
    }

    public ConexaoDispositivo(Integer devId, String modulo, String protocolo, String ip, Date dtHrCon, PrintStream printStream, Thread thread, TrataCliente trataCliente) {
        this.devId = devId;
        this.modulo = modulo;
        this.protocolo = protocolo;
        this.ip = ip;
        this.dtHrCon = dtHrCon;
        this.printStream = printStream;
        this.thread = thread;
        this.trataCliente = trataCliente;
    }

    public Integer getDevId() {
        return devId;
    }

    public void setDevId(Integer devId) {
        this.devId = devId;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getDtHrCon() {
        return dtHrCon;
    }

    public void setDtHrCon(Date dtHrCon) {
        this.dtHrCon = dtHrCon;
    }

    public PrintStream getPrintStream() {
        return printStream;
    }

    public void setPrintStream(PrintStream printStream) {
        this.printStream = printStream;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public TrataCliente getTrataCliente() {
        return trataCliente;
    }

    public void setTrataCliente(TrataCliente trataCliente) {
        this.trataCliente = trataCliente;
    }

    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.devId);
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
        final ConexaoDispositivo other = (ConexaoDispositivo) obj;
        if (!Objects.equals(this.devId, other.devId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ConexaoDispositivo{" + "devId=" + devId + ", modulo=" + modulo + ", protocolo=" + protocolo + ", ip=" + ip + ", dtHrCon=" + dtHrCon + ", printStream=" + printStream + ", thread=" + thread + ", trataCliente=" + trataCliente + '}';
    }

  
}
