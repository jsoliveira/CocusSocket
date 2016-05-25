package br.com.cocus.cocussocket.socket;

import br.com.cocus.cocussocket.model.LigacaoEntradaSaida;
import br.com.cocus.cocussocket.socket.json.model.Configuracao;
import br.com.cocus.cocussocket.socket.json.model.EntAna;
import br.com.cocus.cocussocket.socket.json.model.EntDig;
import br.com.cocus.cocussocket.socket.json.model.SaiDig;
import br.com.cocus.cocussocket.socket.json.model.SaiTr;
import br.com.cocus.cocussocket.socket.json.model.Status;
import com.google.gson.Gson;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Scanner;
import org.apache.log4j.Logger;

public class TrataCliente extends Thread {

    private final InputStream cliente;
    private final PrintStream printStream;
    private final Servidor servidor;
    private final String ip;
    private volatile boolean stop = false;
    private final Logger logger = Logger.getLogger(TrataCliente.class);
    private Boolean inserirId;
    private Integer idDispositivo = null;
    private final Gson gson = new Gson();
    private Status status = null;
    private Configuracao configuracao = new Configuracao();

    public TrataCliente(InputStream cliente, Servidor servidor, String ip, PrintStream printStream) {
        this.cliente = cliente;
        this.servidor = servidor;
        this.ip = ip;
        this.printStream = printStream;

    }

    @Override
    public void run() {

        // TRATAR MSG
        StringBuilder msg = new StringBuilder();
        StringBuilder hdr = new StringBuilder();
        String[] strings = null;

        while (!stop) {

            try (Scanner s = new Scanner(this.cliente)) {

                int qtdeMensagem = 0;

                while (s.hasNextLine()) {

                    msg.setLength(0);

                    msg.append(s.nextLine());

                    if (msg.toString().isEmpty()) {

                        continue;

                    }

                    servidor.distribuiMensagemPainel(msg.toString());

                    ///Veririficar o Tipo da Mensagem
                    try {

                        hdr.setLength(0);
                        strings = msg.toString().split(";");

                        hdr.append(strings[0]);

                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                    if (hdr.toString().trim().equalsIgnoreCase("ST")
                            || hdr.toString().trim().equalsIgnoreCase("EV")) {
                        //valida para adicionar na lista de conexoes
                        if (idDispositivo == null) {

                            if (strings != null && strings.length > 1) {

                                ///Valida se e um rastreador conectando pela primeira vez
                                try {

                                    status = (Status) gson.fromJson(strings[1], Status.class);
                                    idDispositivo = status.getID();
                                    servidor.addCliente(idDispositivo, status.getModulo(), status.getProt(), this.ip, this.printStream, this, this);

                                    configuracao.setTipo(status.getTipo());
                                    configuracao.setProt(status.getProt());
                                    configuracao.setModulo(status.getModulo());
                                    configuracao.setID(status.getID());
                                    configuracao.setSaiDig(status.getSaiDig());
                                    configuracao.setSaiTr(status.getSaiTr());

                                    setarConfiguracao(configuracao);
                                    setarStatus(status);

                                } catch (Exception e) {

                                    idDispositivo = null;
                                }

                                qtdeMensagem++;

                            }

                        }
                    }
                    try {

                        ///VERIFICA COMANDOS DO PAINEL
                        switch (hdr.toString().toUpperCase()) {

                            case "ST": {
                                try {

                                    status = (Status) gson.fromJson(strings[1], Status.class);

                                    setarStatus(status);

                                } catch (Exception e) {

                                    e.printStackTrace();
                                }
                            }
                            break;

                            case "EV": {
                                try {

                                    status = (Status) gson.fromJson(strings[1], Status.class);

                                    for (LigacaoEntradaSaida l : servidor.ligacoes) {

                                        Integer vlPortaEntrada;
                                        Integer vlPortaSaida = null;

                                        Field entrada; ///Entrada Digital
                                        if (l.getPontoEntrada().getPortaModelo().getTipoPorta().getId().intValue() == 1) {
                                            entrada = EntDig.class.getDeclaredField(l.getPontoEntrada().getPortaModelo().getDescricao());
                                            entrada.setAccessible(true);
                                            vlPortaEntrada = (Integer) entrada.get(status.getEntDig());
                                        } else {
                                            entrada = EntAna.class.getDeclaredField(l.getPontoEntrada().getPortaModelo().getDescricao());
                                            entrada.setAccessible(true);
                                            vlPortaEntrada = (Integer) entrada.get(status.getEntDig());
                                        }

                                        Field saida = null;
                                        Field saidaConf = null;
                                        switch (l.getPontoSaida().getPortaModelo().getTipoPorta().getId().intValue()) {

                                            ///Saida Digital
                                            case 3:
                                                saida = SaiDig.class.getDeclaredField(l.getPontoSaida().getPortaModelo().getDescricao());
                                                saida.setAccessible(true);

                                                saidaConf = SaiDig.class.getDeclaredField(l.getPontoSaida().getPortaModelo().getDescricao());
                                                saidaConf.setAccessible(true);

                                                vlPortaSaida = (Integer) saida.get(status.getSaiDig());

                                                if (vlPortaEntrada == 1) {

                                                    if (vlPortaSaida != 9) {

                                                        saidaConf.set(configuracao.getSaiDig(), 9);
                                                        saida.set(status.getSaiDig(), 9);

                                                    } else {

                                                        saidaConf.set(configuracao.getSaiDig(), 0);
                                                        saida.set(status.getSaiDig(), 0);
                                                    }

                                                }

                                                break;

                                            case 5:
                                                saida = SaiTr.class.getDeclaredField(l.getPontoSaida().getPortaModelo().getDescricao());
                                                saida.setAccessible(true);

                                                saidaConf = SaiTr.class.getDeclaredField(l.getPontoSaida().getPortaModelo().getDescricao());
                                                saidaConf.setAccessible(true);

                                                vlPortaSaida = (Integer) saida.get(status.getSaiTr());

                                                if (vlPortaEntrada == 1) {

                                                    if (vlPortaSaida != 9) {

                                                        saidaConf.set(configuracao.getSaiTr(), 9);
                                                        saida.set(status.getSaiTr(), 9);

                                                    } else {
                                                        //configuracao.getSaiTr().setST0(0);
                                                        saidaConf.set(configuracao.getSaiTr(), 0);
                                                        saida.set(status.getSaiTr(), 0);
                                                    }

                                                }

                                                break;
                                        }

                                    }

                                    servidor.distribuirMensagemConfiguracao(gson.toJson(configuracao));
                                    servidor.distribuiMensagemCommand(gson.toJson(status), this.ip);

                                    setarStatus(status);

                                } catch (Exception e) {

                                    servidor.distribuiMensagemPainel(" Erro na Conversao..:" + e);
                                    e.printStackTrace();
                                }

                            }
                            break;

                            ////Adicionar Painel
                            case "ADDPANEL": {

                                servidor.addPanel(this.ip, this.printStream, this, this);
                            }
                            break;

                            ///Liga ou Desliga porta
                            case "SETPORTA": {

                                try {
                                    status = servidor.status.get(Integer.valueOf(strings[1]));

                                    if (status == null) {
                                        break;
                                    }

                                    configuracao = servidor.configuracao.get(Integer.valueOf(strings[1]));

                                    if (configuracao == null) {

                                        configuracao.setTipo(status.getTipo());
                                        configuracao.setProt(status.getProt());
                                        configuracao.setModulo(status.getModulo());
                                        configuracao.setID(status.getID());
                                        configuracao.setSaiDig(status.getSaiDig());
                                        configuracao.setSaiTr(status.getSaiTr());

                                    }

                                    Field saida;
                                    Field saidaConf;
                                    Integer vlPorta = null;

                                    switch (strings[2].substring(0, 2)) {

                                        case "SD": {

                                            saida = SaiDig.class.getDeclaredField(strings[2]);
                                            saida.setAccessible(true);

                                            saidaConf = SaiDig.class.getDeclaredField(strings[2]);
                                            saidaConf.setAccessible(true);

                                            vlPorta = (Integer) saida.get(status.getSaiDig());

                                            if (vlPorta != 9) {

                                                saidaConf.set(configuracao.getSaiDig(), 9);
                                                saida.set(status.getSaiDig(), 9);

                                            } else {

                                                saidaConf.set(configuracao.getSaiDig(), 0);
                                                saida.set(status.getSaiDig(), 0);
                                            }

                                        }
                                        break;

                                        case "ST": {

                                            saida = SaiTr.class.getDeclaredField(strings[2]);
                                            saida.setAccessible(true);

                                            saidaConf = SaiTr.class.getDeclaredField(strings[2]);
                                            saidaConf.setAccessible(true);

                                            vlPorta = (Integer) saida.get(status.getSaiTr());

                                            if (vlPorta != 9) {

                                                saidaConf.set(configuracao.getSaiTr(), 9);
                                                saida.set(status.getSaiTr(), 9);

                                            } else {
                                                //configuracao.getSaiTr().setST0(0);
                                                saidaConf.set(configuracao.getSaiTr(), 0);
                                                saida.set(status.getSaiTr(), 0);
                                            }

                                        }

                                        break;

                                    }

                                    servidor.distribuirMensagemConfiguracao(gson.toJson(configuracao));
                                    setarStatus(status);

                                } catch (Exception e) {

                                    e.printStackTrace();
                                }

                            }
                            break;

                            ///Recupera o valor das portas
                            case "GETPORTA": {

                                try {
                                    status = servidor.status.get(Integer.valueOf(strings[1]));

                                    Field f;
                                    Integer vlPorta = null;
                                    switch (strings[2].substring(0, 2)) {

                                        case "ED": {

                                            f = EntDig.class.getDeclaredField(strings[2]);
                                            f.setAccessible(true);

                                            vlPorta = (Integer) f.get(status.getEntDig());

                                        }
                                        break;

                                        case "EA": {

                                            f = EntAna.class.getDeclaredField(strings[2]);
                                            f.setAccessible(true);

                                            vlPorta = (Integer) f.get(status.getEntAna());

                                        }
                                        break;

                                        case "SD": {

                                            f = SaiDig.class.getDeclaredField(strings[2]);
                                            f.setAccessible(true);

                                            vlPorta = (Integer) f.get(status.getSaiDig());

                                        }
                                        break;

                                        case "ST": {

                                            f = SaiTr.class.getDeclaredField(strings[2]);
                                            f.setAccessible(true);
                                            vlPorta = (Integer) f.get(status.getSaiTr());

                                        }
                                        break;

                                    }

                                    servidor.distribuiMensagemCommand(vlPorta.toString(), this.ip);

                                } catch (Exception e) {

                                    servidor.distribuiMensagemCommand("0", this.ip);
                                }
                            }
                            break;

                            //Atualiza Ligacoes
                            case "ATTLIGACOES": {

                                servidor.buscarLigacoes();
                            }
                            break;

                            ////Adicionar Command
                            case "ADDCOMMAND": {

                                servidor.addCommand(this.ip, this.printStream, this, this);
                            }
                            break;

                            ////Mensagem de ConfiguraÃ§Ã£o do dispositivo
                            case "MSG": {

                                try {

                                    msg.delete(0, msg.indexOf(";") + 1);
                                    //servidor.distribuirMensagemConfiguracao(msg.toString());
                                    servidor.distribuiMensagemGeral(msg.toString());

                                } catch (Exception e) {

                                    logger.error("Erro ao distribuir mensagem de configuracao", e);
                                }

                            }
                            break;

                            ///Recuperar Lista de Dispositivos Onlive
                            case "GETONLINE": {

                                servidor.distribuiMensagemCommand(servidor.getListDispOnline(), this.ip);

                            }
                            break;

                            ///Status dos dispositivos
                            case "GETSTATUS": {

                                servidor.distribuiMensagemCommand(servidor.getListDispOnline(), this.ip);

                            }
                            break;

                            default: {

                            }
                            break;

                        }

                    } catch (Exception e) {

                        logger.error("Erro ao verificar tipo do comando no painel", e);
                    }

                }

            }

        }
    }

    public void setarStatus(Status s) {

        if (servidor.status.get(s.getID()) == null) {

            servidor.status.put(s.getID(), status);

        } else {

            servidor.status.replace(s.getID(), status);

        }

    }

    public void setarConfiguracao(Configuracao c) {

        if (servidor.configuracao.get(c.getID()) == null) {

            servidor.configuracao.put(c.getID(), configuracao);

        } else {

            servidor.configuracao.replace(c.getID(), configuracao);

        }

    }

    public void parar() {

        this.stop = true;
        logger.info("Tratamento de Cliente Fechado ");
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
