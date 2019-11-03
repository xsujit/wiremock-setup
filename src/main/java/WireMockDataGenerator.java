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

        WireMock.configureFor("localhost", 8090);

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
            stubFor(
                    get(urlMatching(stubData.getUrl()))
                            .willReturn(aResponse()
                                    .withHeader("Content-Type", "text/plain")
                                    .withBody(stubData.getBody())
                            )
            );
        }

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("http://localhost:8090/servicedelivery/102");
        CloseableHttpResponse response = httpClient.execute(request);

        System.out.println(response.getProtocolVersion());              // HTTP/1.1
        System.out.println(response.getStatusLine().getStatusCode());   // 200
        System.out.println(response.getStatusLine().getReasonPhrase()); // OK
        System.out.println(response.getStatusLine().toString());        // HTTP/1.1 200 OK

        response.close();
    }
}
