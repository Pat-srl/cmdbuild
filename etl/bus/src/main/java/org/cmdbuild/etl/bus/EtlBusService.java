package org.cmdbuild.etl.bus;

import java.util.List;

public interface EtlBusService {

    List<EtlBus> getAll();

    EtlBus getByCode(String code);

    interface EtlBus {

        //TODO add elements for ws access
    }

}
