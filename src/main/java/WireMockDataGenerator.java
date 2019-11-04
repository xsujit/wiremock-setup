import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import wiremock.org.apache.http.client.methods.CloseableHttpResponse;
import wiremock.org.apache.http.client.methods.HttpGet;
import wiremock.org.apache.http.impl.client.CloseableHttpClient;
import wiremock.org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WireMockDataGenerator {
    /**
     * This program is intended to configure a standalone wiremock instance
     * First run wiremock standalone
     * Then run this program
     * java -cp wmock.jar WireMockDataGenerator
     */

    public static void main(String[] args) throws IOException {

        if(args[0] == null)
            WireMock.configureFor("localhost", 8090);
        else
            WireMock.configureFor("localhost", Integer.parseInt(args[0]));

        XSSFWorkbook workbook = new XSSFWorkbook(WireMockDataGenerator.class.getResourceAsStream("/data.xlsx"));
        DataFormatter dataFormatter = new DataFormatter();
        Iterator<Row> rowIterator = workbook.getSheetAt(0).rowIterator();

        List<StubData> stubDataList = new ArrayList<>();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() == 0)
                continue;
            Iterator<Cell> cellIterator = row.cellIterator();
            StubData stubData = new StubData();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                switch (cell.getColumnIndex()) {
                    case 1:
                        stubData.setMethod(dataFormatter.formatCellValue(cell));
                        break;
                    case 2:
                        stubData.setUrl(dataFormatter.formatCellValue(cell));
                        break;
                    case 3:
                        stubData.setBody(dataFormatter.formatCellValue(cell));
                        break;
                    case 4:
                        stubData.setStatusCode(Integer.parseInt(dataFormatter.formatCellValue(cell)));
                        break;
                }
            }
            stubDataList.add(stubData);
        }

        for (StubData stubData : stubDataList) {
            switch (stubData.getMethod().toUpperCase()) {
                case "GET":
                    stubFor(get(urlMatching(stubData.getUrl()))
                            .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(stubData.getBody())
                                    .withStatus(stubData.getStatusCode())
                            )
                    );
                case "POST":
                    stubFor(post(urlMatching(stubData.getUrl()))
                            .willReturn(aResponse()
                                    .withStatus(stubData.getStatusCode())
                            )
                    );
            }
        }
    }
}
