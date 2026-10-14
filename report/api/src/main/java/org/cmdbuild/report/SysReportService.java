/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import jakarta.activation.DataHandler;
import org.cmdbuild.classe.access.CardHistoryService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecordValues;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.view.View;

public interface SysReportService {

    DataHandler executeClassSchemaReport(Classe classe, ReportFormat format);

    DataHandler executeSchemaReport(ReportFormat format);

    DataHandler executeUserClassReport(Classe classe, ReportFormat format, DaoQueryOptions queryOptions);

    DataHandler executeUserClassHistoryReport(Classe classe, Long cardId, ReportFormat format, DaoQueryOptions queryOptions, List<CardHistoryService.HistoryElement> historyTypes);

    DataHandler executeUserClassReport(Classe classe, ReportFormat format, DaoQueryOptions queryOptions, Supplier<Stream<? extends DatabaseRecordValues>> records);

    DataHandler executeCardReport(Card card, ReportFormat format);

    DataHandler executeViewReport(View view, ReportFormat format, DaoQueryOptions queryOptions);

}
