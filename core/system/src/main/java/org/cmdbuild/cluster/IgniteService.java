package org.cmdbuild.cluster;

import org.apache.ignite.Ignite;

public interface IgniteService extends ClusterService {

    Ignite getIgnite();
}
