package br.com.cocus.cocussocket.socket;

import br.com.cocus.cocussocket.conexao.ConexaoDispositivo;
import br.com.cocus.cocussocket.conexao.ConexaoPainel;
import br.com.cocus.cocussocket.dao.LigacaoEntradaSaidaDao;
import br.com.cocus.cocussocket.model.LigacaoEntradaSaida;
import br.com.cocus.cocussocket.socket.json.model.Configuracao;
import br.com.cocus.cocussocket.socket.json.model.Dispositivo;
import br.com.cocus.cocussocket.socket.json.model.Status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author jsoliveira
 */
public class Servidor implements Runnable {

    private int porta;

    private volatile boolean stop = false;

    Logger logger = Logger.getLogger(Servidor.class);

    private final LigacaoEntradaSaidaDao lDao = new LigacaoEntradaSaidaDao();
    private int qtdeThreads;
    private final HashMap<Integer, ConexaoPainel> painels;
    private final HashMap<Integer, ConexaoPainel> command;
    public final HashMap<Integer, ConexaoDispositivo> clientes;
    public final HashMap<Integer, Status> status;
    public final HashMap<Integer, Configuracao> configuracao;
    private final StringBuilder msg;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private final Gson gson;
    public List<LigacaoEntradaSaida> ligacoes;

    public Servidor(int porta) {

        this.msg = new StringBuilder();

        this.porta = porta;

        this.clientes = new HashMap<>();

        this.painels = new HashMap<>();

        this.command = new HashMap<>();

        this.status = new HashMap<>();
        
        this.configuracao = new HashMap<>();

        GsonBuilder g = new GsonBuilder();
        g.setDateFormat("dd/MM/yyyy HH:mm:ss");
        gson = g.create();

        this.ligacoes = lDao.findAll("dtTransacao", Boolean.TRUE);

    }

    @Override
    public void run() {

        while (!stop) {

            executa();

        }

    }

    public void buscarLigacoes() {

        this.ligacoes = lDao.findAll("dtTransacao", Boolean.TRUE);

    }

    public void executa() {

        ServerSocket servidor;
        try {
            servidor = new ServerSocket(this.porta);
        } catch (IOException ex) {
            logger.error("Erro ao Criar Server Socket..:", ex);
            return;
        }

        logger.info("Porta " + this.porta + " aberta!");
        System.out.println("Porta " + this.porta + " aberta!");

        String cliIp;
        Socket cliente = null;

        while (true) {
            try {
                // aceita um cliente
                try {
                    cliente = servidor.accept();
                } catch (IOException ex) {
                    logger.error("Erro ao Aceitar Conexão", ex);
                    return;
                }

                cliIp = cliente.getInetAddress().getHostAddress();

                PrintStream ps = new PrintStream(cliente.getOutputStream());

                TrataCliente tc;
                tc = new TrataCliente(cliente.getInputStream(), this, cliIp, ps);
                Thread thread = new Thread(tc);
                logger.info("Conexao do Ip..:" + cliIp);

                // cria tratador de cliente numa nova thread
                thread.start();

            } catch (IOException ex) {
                logger.error("Erro ao iniciar socket", ex);
            }

        }

    }

    public void addCliente(Integer devId, String modulo, String prot, String ip, PrintStream printStream, Thread thread, TrataCliente trataCliente) {

        try {

            ConexaoDispositivo c = this.clientes.get(devId);

            if (c == null) {

                c = new ConexaoDispositivo();
                c.setDevId(devId);
                c.setModulo(modulo);
                c.setProtocolo(prot);
                c.setIp(ip);
                c.setDtHrCon(new Date());
                c.setThread(thread);
                c.setTrataCliente(trataCliente);
                c.setPrintStream(printStream);

                msg.setLength(0);
                msg.append("Nova Conexao do dispositivo..:").append(devId).append(";");
                distribuiMensagemPainel(msg.toString());
                logger.info(msg);

            } else {

                this.clientes.remove(devId);

                c.getTrataCliente().parar();
                c.setThread(null);

                c.setIp(ip);
                c.setDtHrCon(new Date());
                c.setThread(thread);
                c.setPrintStream(printStream);
                c.setTrataCliente(trataCliente);

                msg.setLength(0);
                msg.append("O dispositIvo..:").append(devId).append(" reconectou;");
                distribuiMensagemPainel(msg.toString());
                logger.info(msg);
            }

            this.clientes.put(devId, c);

            qtdeThreads++;

        } catch (Exception e) {

            msg.setLength(0);
            msg.append("Erro ao adicionar o dispositivo..:").append(devId).append(" reconectou;");
            distribuiMensagemPainel(msg.toString());
            logger.error(msg, e);

        }

    }

    public void addPanel(String ip, PrintStream printStream, Thread thread, TrataCliente trataCliente) {

        try {

            int idIp = Integer.parseInt(ip.replaceAll("\\.", ""));
            ConexaoPainel c = this.painels.get(idIp);

            if (c == null) {

                c = new ConexaoPainel();
                c.setIp(ip);
                c.setThread(thread);
                c.setTrataCliente(trataCliente);
                c.setPrintStream(printStream);
                c.setDtHrCon(new Date());

                msg.setLength(0);
                msg.append("Nova Conexao do Painel com o ip..:").append(ip).append(";");
                distribuiMensagemPainel(msg.toString());
                logger.info(msg);
            } else {

                this.painels.remove(idIp);

                c.getTrataCliente().parar();
                c.setThread(null);

                c.setIp(ip);
                c.setPrintStream(printStream);
                c.setThread(thread);
                c.setTrataCliente(trataCliente);
                c.setDtHrCon(new Date());

                msg.setLength(0);
                msg.append("O Painel com o ip..:").append(ip).append(" reconectou;");
                distribuiMensagemPainel(msg.toString());
                logger.info(msg);
            }

            this.painels.put(idIp, c);

            qtdeThreads++;

        } catch (Exception e) {

            msg.setLength(0);
            msg.append("Erro ao adicionar o painel com ip..:").append(ip).append(";");
            distribuiMensagemPainel(msg.toString());
            logger.error(msg, e);

        }

    }

    public void addCommand(String ip, PrintStream printStream, Thread thread, TrataCliente trataCliente) {

        try {

            int idIp = Integer.parseInt(ip.replaceAll("\\.", ""));
            ConexaoPainel c = this.command.get(idIp);

            if (c == null) {

                c = new ConexaoPainel();
                c.setIp(ip);
                c.setThread(thread);
                c.setTrataCliente(trataCliente);
                c.setPrintStream(printStream);
                c.setDtHrCon(new Date());

                msg.setLength(0);
                msg.append("Nova Conexao do Command com o ip..:").append(ip).append(";");
                distribuiMensagemPainel(msg.toString());
                logger.info(msg);

                qtdeThreads++;
            } else {

                this.painels.remove(idIp);

                c.getTrataCliente().parar();
                c.setThread(null);

                c.setIp(ip);
                c.setPrintStream(printStream);
                c.setThread(thread);
                c.setTrataCliente(trataCliente);
                c.setDtHrCon(new Date());

                msg.setLength(0);
                msg.append("O Command com o ip..:").append(ip).append(" reconectou;");
                distribuiMensagemPainel(msg.toString());
                logger.info(msg);
            }

            this.command.put(idIp, c);

        } catch (Exception e) {

            msg.setLength(0);
            msg.append("Erro ao adicionar o Command com ip..:").append(ip).append(";");
            distribuiMensagemPainel(msg.toString());
            logger.error(msg, e);

        }

    }

    public void parar() {

        this.stop = true;

        System.out.println("Porta " + this.porta + " fechada!");
    }

    public void distribuiMensagem(String msg) {
        // envia msg para todo mundo
        if (msg == null || msg.isEmpty()) {
            return;
        }

        try {

            this.clientes.values().stream().map((cliente) -> {
                cliente.getPrintStream().print(msg);
                return cliente;
            }).forEach((cliente) -> {
                cliente.getPrintStream().flush();
            });

        } catch (Exception e) {

            logger.error("Erro ao enviar mensagem aos clientes", e);

        }
    }

    public void distribuirMensagemConfiguracao(String msg) {

        if (msg == null || msg.isEmpty()) {
            return;
        }

        try {

            Configuracao s = (Configuracao) gson.fromJson(msg, Configuracao.class);

            ConexaoDispositivo c = this.clientes.get(s.getID());

            this.msg.setLength(0);
            if (c == null) {

                this.msg.append("Nao foi possivel entregar a mensagem para o dispositivo..:").append(s.getID()).append(", dispositivo nao encontrado na lista de clientes");

            } else {

                c.getPrintStream().println(msg);
                c.getPrintStream().flush();

                this.msg.append("Mensagem para o dispositivo..:").append(s.getID()).append(" enviada com sucesso!!!");
            }

            distribuiMensagemPainel(this.msg.toString());
            logger.info(msg);

        } catch (Exception e) {

            logger.error("Erro ao enviar mensagem..:", e);

        }

    }

    public void distribuiMensagemGeral(String msg) {
        // envia msg para todos os Paineis
        if (msg == null || msg.isEmpty()) {
            return;
        }

        if (this.clientes == null || this.clientes.isEmpty()) {

            return;
        }

        try {

            this.clientes.values().stream().forEach((p) -> {
                p.getPrintStream().println(msg);
                p.getPrintStream().flush();
            });

        } catch (Exception e) {

            logger.error("Erro ao distribuir mensagem", e);

        }

    }

    public void distribuiMensagemPainel(String msg) {
        // envia msg para todos os Paineis
        if (msg == null || msg.isEmpty()) {
            return;
        }

        if (this.painels == null || this.painels.isEmpty()) {

            return;
        }

        try {

            this.painels.values().stream().forEach((p) -> {
                p.getPrintStream().println(msg);
                p.getPrintStream().flush();
            });

        } catch (Exception e) {

            logger.error("Erro ao distribuir mensagem", e);

        }

    }

    public void distribuiMensagemCommand(String msg) {
        // envia msg para todos os Paineis
        if (msg == null || msg.isEmpty()) {
            return;
        }

        if (this.command == null || this.command.isEmpty()) {

            return;
        }

        try {

            this.command.values().stream().forEach((p) -> {
                p.getPrintStream().println(msg);
                p.getPrintStream().flush();
            });

        } catch (Exception e) {

            logger.error("Erro ao distribuir mensagem", e);

        }

    }

    public void distribuiMensagemCommand(String msg, String ip) {
        // envia msg para todos os Paineis
        if (msg == null || msg.isEmpty()) {
            return;
        }

        if (ip == null || ip.isEmpty()) {
            return;
        }

        if (this.command == null || this.command.isEmpty()) {

            return;
        }

        try {
            ConexaoPainel c = this.command.get(Integer.parseInt(ip.replaceAll("\\.", "")));

            if (c != null) {
                c.getPrintStream().println(msg);
                c.getPrintStream().flush();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public String getListDispOnline() {

        List<Dispositivo> dispositivos = new ArrayList<>();

        this.clientes.values().stream().forEach((c) -> {

            Dispositivo d = new Dispositivo();
            d.setID(c.getDevId());
            d.setIP(c.getIp());
            d.setModulo(c.getModulo());
            d.setProt(c.getProtocolo());

            dispositivos.add(d);

            ///s.append(c.getDevId()).append("-").append(sdf.format(c.getDtHrCon())).append(";");
        });

        return gson.toJson(dispositivos);
    }

    public String getStatusDisp() {

        List<Status> status = new ArrayList<>();

        this.clientes.values().stream().forEach((c) -> {

            status.add(c.getTrataCliente().getStatus());

        });

        return gson.toJson(status);

    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }

    public static void main(String[] args) throws IOException {

        new Servidor(3017).executa();

    }

}
