import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class DownloadTISS {
    
    public static void main(String[] args) throws Exception {
    
        // 1° - Acessando a página inicial do TISS e buscando a URL da versão mais recente do Padrão TISS.             
        URL urlTISS = encontrarURLVersaoRecenteTISS();

        // 2° - Buscando a URL do Componente Organizacional em PDF.
        URL urlComponenteOrganizacional = encontrarURLComponenteOrganizacional(urlTISS);

        // 3° - Fazendo o download do Componente Organizacional em PDF.
        baixarComponenteOrganizacional(urlComponenteOrganizacional, new File("C:\\Users\\user\\Desktop\\temp\\Componente_Organizacional_TISS.pdf"));
    }

    /**
     * Método para encontrar o endereço URL do Padrão TISS mais
     * recente, nesse caso em específico a versão de novembro/2021.
     * 
     * @return URL identificada ou <code>null</code>, caso contrário.
     * @throws Exception Problemas relacionados à conexão.
     */
    public static URL encontrarURLVersaoRecenteTISS() throws Exception {

        URL url = new URL("https://www.gov.br/ans/pt-br/assuntos/prestadores/padrao-para-troca-de-informacao-de-saude-suplementar-2013-tiss");
        BufferedReader buffer = new BufferedReader(
            new InputStreamReader(url.openStream())
        );

        String linha;

        while ((linha = buffer.readLine()) != null) {

            if (linha.contains("Clique aqui para acessar a ver")) {

                String urlIdentificada = linha.split("href=\"")[1].split("\"")[0];

                buffer.close();
                return new URL(urlIdentificada);
            }

        }

        buffer.close();
        return null;
    }


    /**
     * Método para encontrar o endereço URL do Componente Organizacional do 
     * Padrão TISS em PDF, nesse caso o organizacional de novembro/2021.
     * 
     * @param urlTISS O URL identificada do Padrão TISS mais recente.
     * @return URL do PDF ou <code>null</code>, caso contrário.
     * @throws Exception Problemas relacionados à conexão.
     */
    public static URL encontrarURLComponenteOrganizacional(URL urlTISS) throws Exception {

        BufferedReader buffer = new BufferedReader(
            new InputStreamReader(urlTISS.openStream())
        );

        String linha;

        while ((linha = buffer.readLine()) != null) {

            if (linha.contains("documento referente ao Componente Organizacional")) {

                String urlComponenteOrganizacional = linha.split("href=\"")[1].split("\"")[0];

                buffer.close();
                return new URL(urlComponenteOrganizacional);
            }
        }

        buffer.close();
        return null;
    }


    /**
     * Método para fazer o download em PDF do Componente Organizacional do Padrão TISS mais recente do site.
     * 
     * @param urlComponenteOrganizacional Endereço URL extráida da página principal.
     * @param destino Destino do arquivo em PDF.
     * @throws Exception Problemas relacionados à conexão.
     */
    public static void baixarComponenteOrganizacional(URL urlComponenteOrganizacional, File destino) throws Exception {

        InputStream is = urlComponenteOrganizacional.openStream();
        FileOutputStream fos = new FileOutputStream(destino);
        
        int bytes = 0;
        
        while ((bytes = is.read()) != -1) {
            fos.write(bytes);
        }

        is.close();
        fos.close();        
    } 
}