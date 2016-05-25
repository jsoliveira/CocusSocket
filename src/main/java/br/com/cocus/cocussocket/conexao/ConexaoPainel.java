package br.com.cocus.cocussocket.conexao;


import br.com.cocus.cocussocket.socket.TrataCliente;
import java.io.PrintStream;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author jsoliveira
 */
public class ConexaoPainel {


    private String ip;
    private Date dtHrCon;
    private PrintStream printStream;
    private Thread thread;
    private TrataCliente trataCliente;

    public ConexaoPainel() {
    }

    public ConexaoPainel(String ip, Date dtHrCon, PrintStream printStream, Thread thread,TrataCliente trataCliente) {
        this.ip = ip;
        this.dtHrCon = dtHrCon;
        this.printStream = printStream;
        this.thread = thread;
        this.trataCliente = trataCliente;
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
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.ip);
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
        final ConexaoPainel other = (ConexaoPainel) obj;
        if (!Objects.equals(this.ip, other.ip)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ConexaoPainel{" + "ip=" + ip + ", dtHrCon=" + dtHrCon + ", printStream=" + printStream + ", thread=" + thread + '}';
    }

    
  
}
