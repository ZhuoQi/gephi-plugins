/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zq.Mule;

import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author zqlee1
 */

@ServiceProvider(service = StatisticsBuilder.class)
public class MuleBuilder implements StatisticsBuilder{
    private static final String METRIC_NAME = "Redundancy";

    @Override
    public String getName() {
        return METRIC_NAME;
    }

    @Override
    public Statistics getStatistics() {
        return new GetMule();
    }

    @Override
    public Class<? extends Statistics> getStatisticsClass() {
        return GetMule.class;
    }

}
