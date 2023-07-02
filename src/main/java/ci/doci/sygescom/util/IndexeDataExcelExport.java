package ci.doci.sygescom.util;

import ci.doci.sygescom.domaine.Indexes;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class IndexeDataExcelExport extends AbstractXlsView {
    @Override
    protected void buildExcelDocument(Map<String, Object> map,
                                      Workbook workbook,
                                      HttpServletRequest httpServletRequest,
                                      HttpServletResponse httpServletResponse) throws Exception {


        // definition du nom du fichier Excel à expoter
        httpServletResponse.addHeader("Content-Disposition", "attachment;fileName=IndexData.xlsx");

        //lecture des données fournies par le controller
        List<Indexes> list = (List<Indexes>)map.get("list");

        //création de la feuille
        Sheet sheet = workbook.createSheet("Indexes");

        //Création des entetes de lignes
        Row row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("ID");
        row0.createCell(1).setCellValue("INDEXE FIN ESSENCE");
        row0.createCell(0).setCellValue("INDEXE FIN GAZOIL");
        row0.createCell(0).setCellValue("CUVE ESSENCE");
        row0.createCell(0).setCellValue("CUVE GAZOIL");

        // Créer la ligne 1 a partir de la liste
        int rowNum = 1;
        for(Indexes spec : list){
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(spec.getId());
            //row.createCell(1).setCellValue(spec.getEssenceIndexeFin());
           // row.createCell(2).setCellValue(spec.getGazoilIndexeFin());
            row.createCell(2).setCellValue(spec.getCuveEssence());
            row.createCell(3).setCellValue(spec.getCuveGazoil());
        }

    }
}
