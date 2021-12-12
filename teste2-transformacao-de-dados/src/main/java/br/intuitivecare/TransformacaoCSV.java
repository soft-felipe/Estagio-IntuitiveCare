package br.intuitivecare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class TransformacaoCSV {

    public static void main(String[] args) throws Exception {

        // 1° Extrair o "Quadro 30 - Tabela de tipo de demandante" e exportar os dados para o
        // arquivo CSV.
        String[] quadro30 = extrairQuadro30TipoDemandante();
        vetorParaCSV(quadro30, new File("quadro30.csv"));

        // 2° Extrair o Quadro 31 - "Tabela de categoria do Padrão TISS" e exportar os dados para o
        // arquivo CSV.
        String[] quadro31 = extrairQuadro31PadraoTISS();
        vetorParaCSV(quadro31, new File("quadro31.csv"));

        // 3° Extrair o "Quadro 32 - Tabela de tipo de solicitação" e exportar os dados para o
        // arquivo CSV.
        String[] quadro32 = extrairQuadro32TipoSolicitacao();
        vetorParaCSV(quadro32, new File("quadro32.csv"));

        // 4° Comprindo todos os arquivos CSV para ZIP.
        comprimirArquivosZIP(Arrays.asList("quadro30.csv", "quadro31.csv", "quadro32.csv"), "Teste_Felipe_Moreira.zip");
    }

    /**
     * Método para identificar o quadro 30 no PDF e extraí-lo.
     * 
     * @return Os dados do quadro 30.
     * @throws Exception Problemas relacionados à conexão
     */
    public static String[] extrairQuadro30TipoDemandante() throws Exception {

        int i;
        File pdfTISS = new File("Componente_Organizacional_TISS.pdf");
        PDDocument docTISS = PDDocument.load(pdfTISS);
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        String text = pdfTextStripper.getText(docTISS);

        String[] textoPDF = text.split("\n");

        for (i = 0; i < textoPDF.length; i++) {

            if (textoPDF[i].contains("Tabela de Tipo do Demandante")) {
                break;
            }
        }

        String[] vetor = new String[6];
        int j = 0;

        for (int k = i + 1; k < i + 7; k++) {

            vetor[j] = textoPDF[k];
            j++;
        }

        docTISS.close();
        return vetor;
    }

    /**
     * Método para identificar o quadro 31 no PDF e extraí-lo.
     * 
     * @return Os dados do quadro 31.
     * @throws Exception Problemas relacionados à conexão.
     */
    public static String[] extrairQuadro31PadraoTISS() throws Exception {

        int i;
        File pdfTISS = new File("Componente_Organizacional_TISS.pdf");
        PDDocument docTISS = PDDocument.load(pdfTISS);
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        String text = pdfTextStripper.getText(docTISS);

        String[] textoPDF = text.split("\n");

        for (i = 0; i < textoPDF.length; i++) {

            if (textoPDF[i].contains("Quadro 31 – Tabela de categoria do Padr")) {
                break;
            }
        }

        String[] vetor = new String[134];
        int j = 0;

        for (int k = i + 2; k < i + 175; k++) {

            if (k == 4641 || k == 4672 || k == 4705 || k == 4739 || k == 4776) {
                k += 4;
                continue;
            }

            if (textoPDF[k].trim().length() < 5) {
                String temp = textoPDF[k].trim() + " " + textoPDF[k + 1].trim() + " " + textoPDF[k + 2].trim();
                vetor[j] = temp;
                k += 2;
            } else {
                vetor[j] = textoPDF[k].trim();
            }

            j++;
        }

        docTISS.close();
        return vetor;
    }

    /**
     * Método para identificar o quadro 32 no PDF e extraí-lo.
     * 
     * @return Os dados do quadro 32.
     * @throws Exception Probelmas relacionados à conexão.
     */
    public static String[] extrairQuadro32TipoSolicitacao() throws Exception {

        int i;
        File pdfTISS = new File("Componente_Organizacional_TISS.pdf");
        PDDocument docTISS = PDDocument.load(pdfTISS);
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        String text = pdfTextStripper.getText(docTISS);

        String[] textoPDF = text.split("\n");

        for (i = 0; i < textoPDF.length; i++) {

            if (textoPDF[i].contains("1 Altera")) {
                break;
            }
        }

        String[] vetor = new String[4];
        int j = 0;

        for (int k = i - 1; k < i + 3; k++) {
            vetor[j] = textoPDF[k];
            j++;
        }

        docTISS.close();
        return vetor;
    }

    /**
     * Método para converter os dados das tabelas em CSV.
     * 
     * @param vetor Recebe os dados das tabelas.
     * @param destino Caminho relativo da tabela estruturada em CSV.
     * @throws Exception Problemas relacionados à conexão.
     */
    public static void vetorParaCSV(String vetor[], File destino) throws Exception {

        OutputStream os = new FileOutputStream(destino);
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();

        for (String linha : vetor) {

            String[] temp = linha.split(" ", 2);

            sb.append(temp[0]);
            sb.append(',');
            sb.append(temp[1]);
            sb.append('\n');
        }

        writer.write(sb.toString());
        writer.close();
    }

    /**
     * Método para comprimir os dados das tabelas, de CSV para ZIP.
     * 
     * @param arquivos Recebendo os dados das tabelas estruturadas em CSV.
     * @param destino Caminho relativo do arquivo comprimido em ZIP.
     * @throws Exception Problemas relacionados à conexão.
     */
    public static void comprimirArquivosZIP(List<String> arquivos, String destino) throws Exception {

        FileOutputStream fos = new FileOutputStream(destino);
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        for (String srcFile : arquivos) {
            
            File fileToZip = new File(srcFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;

            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }

            fis.close();
        }

        zipOut.close();
        fos.close();
    }
}