package br.com.cocus.cocussocket.socket.json;

import br.com.cocus.cocussocket.socket.json.model.Configuracao;
import br.com.cocus.cocussocket.socket.json.model.EntAna;
import br.com.cocus.cocussocket.socket.json.model.EntDig;
import br.com.cocus.cocussocket.socket.json.model.SaiDig;
import br.com.cocus.cocussocket.socket.json.model.SaiTr;
import br.com.cocus.cocussocket.socket.json.model.Status;
import com.google.gson.Gson;

/**
 *
 * @author jsoliveira
 */
public class ConverterJson {

    public static void main(String[] args) {

        Configuracao configuracao = new Configuracao();

        SaiDig saiDig = new SaiDig(00, 01, 02, 03, 04, 05);
        SaiTr saiTr= new SaiTr(01, 02);

        configuracao.setTipo("At");
        configuracao.setProt("1.0");
        configuracao.setModulo("ModIO-01");
        configuracao.setID(30);
        configuracao.setSaiDig(saiDig);
        configuracao.setSaiTr(saiTr);

        EntDig entDig = new EntDig(00, 01, 02, 03, 04, 05);
        EntAna entAna = new EntAna(01, 02);

        Status status = new Status();
        status.setTipo("St");
        status.setProt("1.0");
        status.setModulo("ModIO-01");
        status.setID(30);
        status.setSWifi(00);
        status.setEntDig(entDig);
        status.setEntAna(entAna);
        status.setSaiDig(saiDig);
        status.setSaiTr(saiTr);
        status.setSW(01);
        
        
        Gson g = new Gson();

        System.out.println(g.toJson(configuracao));
        System.out.println(g.toJson(status));
//        
        
      //  String asss={"Tipo":"St","Prot":"1.0","Modulo":"ModIO-01","ID":30,"SWifi":0,"EntDig":{"ED0":0,"ED1":1,"ED2":2,"ED3":3,"ED4":4,"ED5":5},"EntAna":{"EA0":1,"EA1":2},"SaiDig":{"SD0":0,"SD1":1,"SD2":2,"SD3":3,"SD4":4,"SD5":5},"SaiTr":{"ST0":1,"ST1":2},"SW":1}

        String asd = "{\"titulo\":\"JSON James\",\"ano\":2012,\"generos\":[\"Western\"]}";
        ///String ss = "{\"Tipo\":\"At\",\"Prot\":\"1.0\",\"Modulo\":\"ModIO-01\",\"ID\":30,SaiDig{\"SD0":10,"SD1":05,:"SD2":20,"SD3":00,"SD4":00,"SD5":0},SaiTr:{"ST0":10,"ST1":00}}";
//        
//        System.out.println(g.fromJson(null, null));

    }

}
