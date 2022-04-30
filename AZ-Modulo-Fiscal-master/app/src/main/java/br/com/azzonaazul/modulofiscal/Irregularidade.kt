package br.com.azzonaazul.modulofiscal

class Irregularidade {

    var placa = ""
    var foto1 = ""
    var foto2 = ""
    var foto3 = ""
    var foto4 = ""
    var imei = ""
    var data = ""

    override fun toString(): String {
        return "Irregularidade(\nplaca='$placa', \nfoto1='$foto1, \nfoto1='$foto2, \nfoto1='$foto3', \nfoto1='$foto4, \nimei=$imei, \ndata=$data')"

    }

}