package ci.doci.sygescom.service;

import ci.doci.sygescom.domaine.BonDeCommande;
import ci.doci.sygescom.domaine.FacturationCorporate;
import ci.doci.sygescom.repository.CorporateDirecteRepository;
//import ci.doci.sygescom.repository.FacturationCorporateRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JspFacturationCorporateService {
   // final private FacturationCorporateRepository facturationCorporateRepository;
    final  private CorporateDirecteRepository corporateDirecteRepository;

    public JspFacturationCorporateService( CorporateDirecteRepository corporateDirecteRepository) {
        //this.facturationCorporateRepository = facturationCorporateRepository;
        this.corporateDirecteRepository = corporateDirecteRepository;
    }
   /* public ResponseEntity<byte[]> generateReport(long id, String path, String jspfileName, String pdfPath, String outputfilJspPdf, String facTitle )  throws JRException{
        try{
          //  List<FacturationCorporate>  fact = facturationCorporateRepository.findFacturationCorp(id);
            // List<FacturationCorporate> fact = corporateDirecteRepository.findById();
            //List<FacturationCorporate> fact = facturationCorporateRepository.findAll();
            String reportPath = path;
            String reportPathPdf = pdfPath;
            //compile le jasper
            JasperReport jasperReport = JasperCompileManager
                    .compileReport(reportPath+"\\"+jspfileName+".jrxml");
            //Get data source
          //  JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource();
            //Add parametters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("FacTitle", facTitle);

            //File repport name
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,parameters, jrBeanCollectionDataSource);
            //Export pdf report
            JasperExportManager.exportReportToPdfFile(jasperPrint, reportPathPdf+"\\"+ outputfilJspPdf+".pdf");
            //View in browse
            byte data[] = JasperExportManager.exportReportToPdf(jasperPrint);

            System.err.println(data);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=citiesreport.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

            // return "Rapport created successfull";

        } catch (Exception e){
            e.printStackTrace();
            // return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
            //return  "Error";
        }

        return null;
    }
*/
    /*******24/12/2022*/

    public ResponseEntity<byte[]> generateReportBc(long id, String path, String jspfileName, String pdfPath, String outputfilJspPdf, String facTitle , BonDeCommande fact)  throws JRException{
        try{
            //List<FacturationCorporate>  fact = facturationCorporateRepository.findFacturationCorp(id);
            // List<CorporateDirecte> fact =  corporateDirecteRepository.CorporateDirecteBlAccepterById(id);
           // System.out.println(fact);
            // List<FacturationCorporate> fact = corporateDirecteRepository.findById();
            //List<FacturationCorporate> fact = facturationCorporateRepository.findAll();
            String reportPath = path;
            String reportPathPdf = pdfPath;
            //compile le jasper

            JasperReport jasperReport = JasperCompileManager
                    .compileReport(reportPath+"\\"+jspfileName+".jrxml");
            //Get data source
            JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(Collections.singleton(fact));
            //Add parametters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("FacTitle", facTitle);

            //File repport name
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,parameters,jrBeanCollectionDataSource);
            //Export pdf report
            JasperExportManager.exportReportToPdfFile(jasperPrint, reportPathPdf+"\\"+ outputfilJspPdf+".pdf");
            //View in browse
            byte data[] = JasperExportManager.exportReportToPdf(jasperPrint);

            System.err.println(data);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=citiesreport.pdf");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(data);

            // return "Rapport created successfull";

        } catch (Exception e){
            e.printStackTrace();
            // return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
            //return  "Error";
        }

        return null;
    }
}
